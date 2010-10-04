/* HtmlBasedComponent.java

	Purpose:

	Description:

	History:
		Sat Dec 31 12:30:18     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
import org.zkoss.zk.ui.ext.DragControl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.fn.ZkFns;

/**
 * A skeletal implementation for HTML based components.
 * It simplifies to implement methods common to HTML based components.
 *
 * <p>Events:<br/>
 *  onClick, onDoubleClick, onRightClick, onMove, onSize, onZIndex, onDrop,
 *  onMouseOver, onMouseOut, onOK, onCacnel and onCtrlKey.<br/>
 * 
 * <p>It supports
 * <ul>
 * <li>{@link #getSclass} and {@link #getStyle}.</li>
 * <li>{@link #getWidth}, {@link #getHeight}, {@link #getLeft},
 * {@link #getTop}, {@link #getZIndex}</li>
 * <li>{@link #focus}</li>
 * </ul>
 *
 * @author tomyeh
 * @since 5.0.0 supports onOK event.
 * @since 5.0.0 supports onCancel event.
 * @since 5.0.0 supports onCtrlKey event.
 */
abstract public class HtmlBasedComponent extends AbstractComponent implements org.zkoss.zk.ui.api.HtmlBasedComponent {
	/** The ZK CSS class. */
	protected String _zclass;
	/** The prolog content that shall be generated before real content. */
	private String _prolog;
	/** AuxInfo: use a class (rather than multiple member) to save footprint */
	private AuxInfo _auxinf;

	static {
		addClientEvent(HtmlBasedComponent.class, Events.ON_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_DOUBLE_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_RIGHT_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_OK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_CANCEL, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_CTRL_KEY, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_DROP, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_SIZE, CE_DUPLICATE_IGNORE|CE_IMPORTANT);
		addClientEvent(HtmlBasedComponent.class, Events.ON_MOUSE_OVER, 0); //not to use CE_DUPLICATE_IGNORE since there is an order issue
		addClientEvent(HtmlBasedComponent.class, Events.ON_MOUSE_OUT, 0);
	}

	protected HtmlBasedComponent() {
	}

	/** Returns the left position.
	 */
	public String getLeft() {
		return _auxinf != null ? _auxinf.left: null;
	}
	/** Sets the left position.
	 * <p>If you want to specify <code>right</code>, use {@link #setStyle} instead.
	 * For example, <code>setStyle("right: 0px");</code>
	 * @param left the left position. Remember to specify <code>px</code>, <code>pt</code> or <code>%</code>.
	 */
	public void setLeft(String left) {
		if (!Objects.equals(getLeft(), left)) {
			initAuxInfo().left = left;
			smartUpdate("left", getLeft());
		}
	}
	/** Returns the top position.
	 */
	public String getTop() {
		return _auxinf != null ? _auxinf.top: null;
	}
	/** Sets the top position.
	 * <p>If you want to specify <code>bottom</code>, use {@link #setStyle} instead.
	 * For example, <code>setStyle("bottom: 0px");</code>
	 * @param top the top position. Remember to specify <code>px</code>, <code>pt</code> or <code>%</code>.
	 */
	public void setTop(String top) {
		if (!Objects.equals(getTop(), top)) {
			initAuxInfo().top = top;
			smartUpdate("top", getTop());
		}
	}
	/** Returns the Z index.
	 * <p>Default: -1 (means system default;
	 */
	public int getZIndex() {
		return _auxinf != null ? _auxinf.zIndex: -1;
	}
	/** Sets the Z index.
	 */
	public void setZIndex(int zIndex) {
		if (zIndex < -1)
			zIndex = -1;
		if (getZIndex() != zIndex) {
			initAuxInfo().zIndex = zIndex;
			if ((zIndex = getZIndex()) < 0)
				smartUpdate("zIndex", (Object)null);
			else
				smartUpdate("zIndex", zIndex);
		}
	}
	/** Returns the Z index.
	 * It is the same as {@link #getZIndex}.
	 * @since 3.5.2
	 */
	public int getZindex() {
		return getZIndex();
	}
	/** Sets the Z index.
	 * It is the same as {@link #setZIndex}.
	 * @since 3.5.2
	 */
	public void setZindex(int zIndex) {
		setZIndex(zIndex);
	}
	/** Returns the height. If null, the best fit is used.
	 * <p>Default: null.
	 */
	public String getHeight() {
		return _auxinf != null ? _auxinf.height: null;
	}
	/** Sets the height. If null, the best fit is used.
	 */
	public void setHeight(String height) {
		if (height != null && height.length() == 0)
			height = null;
		if (!Objects.equals(getHeight(), height)) {
			initAuxInfo().height = height;
			smartUpdate("height", getHeight());
		}
	}
	/** Returns the width. If null, the best fit is used.
	 * <p>Default: null.
	 */
	public String getWidth() {
		return _auxinf != null ? _auxinf.width: null;
	}
	/** Sets the width. If null, the best fit is used.
	 * @see #setWidthDirectly
	 * @see #disableClientUpdate
	 */
	public void setWidth(String width) {
		if (width != null && width.length() == 0)
			width = null;
		if (!Objects.equals(getWidth(), width)) {
			initAuxInfo().width = width;
			smartUpdate("width", getWidth());
		}
	}
	/** Sets the width directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setWidthDirectly(String width) {
		initAuxInfo().width = width;
	}
	/** Sets the height directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setHeightDirectly(String height) {
		initAuxInfo().height = height;
	}
	/** Sets the left directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setLeftDirectly(String left) {
		initAuxInfo().left = left;
	}
	/** Sets the top directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setTopDirectly(String top) {
		initAuxInfo().top = top;
	}
	/** Sets the z-index directly without sending back the result
	 * (smart update) to the client
	 * @since 5.0.4
	 */
	protected void setZIndexDirectly(int zIndex) {
		initAuxInfo().zIndex = zIndex;
	}

	/** Returns the text as the tooltip.
	 * <p>Default: null.
	 */
	public String getTooltiptext() {
		return _auxinf != null ? _auxinf.tooltiptext: null;
	}
	/** Sets the text as the tooltip.
	 */
	public void setTooltiptext(String tooltiptext) {
		if (tooltiptext != null && tooltiptext.length() == 0)
			tooltiptext = null;
		if (!Objects.equals(getTooltiptext(), tooltiptext)) {
			initAuxInfo().tooltiptext = tooltiptext;
			smartUpdate("tooltiptext", getTooltiptext());
		}
	}

	/**
	  * Returns the ZK Cascading Style class(es) for this component.
	  * It usually depends on the implementation of the mold (@{link #getMold}).
	  *
	  * <p>Default: null (the default value depends on element).
	  *
	  * <p>{@link #setZclass}) will completely replace the default style
	  * of a component. In other words, the default style of a component
	  * is associated with the default value of {@link #getZclass}.
	  * Once it is changed, the default style won't be applied at all.
	  * If you want to perform small adjustments, use {@link #setSclass}
	  * instead.
	  *
	  * @since 3.5.1
	  * @see #getSclass
	  */
	 public String getZclass() {
		 return _zclass;
	 }
	 /**
	  * Sets the ZK Cascading Style class(es) for this component.
	  * It usually depends on the implementation of the mold (@{link #getMold}).
	  *
	  * @param zclass the style class used to apply the whote widget.
	  * @since 3.5.0
	  * @see #setSclass
	  * @see #getZclass
	  */
	 public void setZclass(String zclass) {
		if (zclass != null && zclass.length() == 0) zclass = null;
		if (!Objects.equals(_zclass, zclass)) {
			_zclass = zclass;
			smartUpdate("zclass", _zclass);
		}
	 }
	/** Returns the CSS class.
	 *
	 * <p>Default: null.
	 *
	 * <p>The default styles of ZK components doesn't depend on the value
	 * of {@link #getSclass}. Rather, {@link #setSclass} is provided to
	 * perform small adjustment, e.g., only changing the font size.
	 * In other words, the default style is still applied if you change
	 * the value of {@link #getSclass}, unless you override it.
	 * To replace the default style completely, use
	 * {@link #setZclass} instead.
	 *
	 * @see #getZclass
	 */
	public String getSclass() {
		return _auxinf != null ? _auxinf.sclass: null;
	}
	/** Sets the CSS class.
	 *
	 * @see #setZclass
	 */
	public void setSclass(String sclass) {
		if (sclass != null && sclass.length() == 0) sclass = null;
		if (!Objects.equals(getSclass(), sclass)) {
			initAuxInfo().sclass = sclass;
			smartUpdate("sclass", getSclass());
		}
	}
	/** Sets the CSS class. This method is a bit confused with Java's class,
	 * but we provide it for XUL compatibility.
	 * The same as {@link #setSclass}.
	 */
	public void setClass(String sclass) {
		setSclass(sclass);
	}

	/** Returns the CSS style.
	 * <p>Default: null.
	 */
	public String getStyle() {
		return _auxinf != null ? _auxinf.style: null;
	}
	/** Sets the CSS style.
	 */
	public void setStyle(String style) {
		if (style != null && style.length() == 0) style = null;
		if (!Objects.equals(getStyle(), style)) {
			initAuxInfo().style = style;
			smartUpdate("style", getStyle());
		}
	}

	/** Sets "true" or "false" to denote whether a component is draggable,
	 * or an identifier of a draggable type of objects.
	 *
	 * <p>The simplest way to make a component draggable is to set
	 * this attribute to true. To disable it, set this to false.
	 *
	 * <p>If there are several types of draggable objects, you could
	 * assign an identifier for each type of draggable object.
	 * The identifier could be anything but empty.
	 *
	 * @param draggable "false", "" or null to denote non-draggable; "true" for draggable
	 * with anonymous identifier; others for an identifier of draggable.<br/>
	 * Notice that if the parent is {@link DragControl} and draggable is null,
	 * then it means draggable.
	 */
	public void setDraggable(String draggable) {
		if (draggable != null && draggable.length() == 0) //empty means false (but not null)
			draggable = "false";

		if (!Objects.equals(_auxinf != null ? _auxinf.draggable: null, draggable)) {
			initAuxInfo().draggable = draggable;
			smartUpdate("draggable", draggable); //getDraggable is final
		}
	}
	/** Returns the identifier of a draggable type of objects, or "false"
	 * if not draggable (never null nor empty).
	 */
	public String getDraggable() {
		return _auxinf != null && _auxinf.draggable != null ? _auxinf.draggable:
			getParent() instanceof DragControl ? "true": "false";
	}
	/** Sets "true" or "false" to denote whether a component is droppable,
	 * or a list of identifiers of draggable types of objects that could
	 * be droped to this component.
	 *
	 * <p>The simplest way to make a component droppable is to set
	 * this attribute to true. To disable it, set this to false.
	 *
	 * <p>If there are several types of draggable objects and this
	 * component accepts only some of them, you could assign a list of
	 * identifiers that this component accepts, separated by comma.
	 * For example, if this component accepts dg1 and dg2, then
	 * assign "dg1, dg2" to this attribute.
	 *
	 * @param droppable "false", null or "" to denote not-droppable;
	 * "true" for accepting any draggable types; a list of identifiers,
	 * separated by comma for identifiers of draggables this component
	 * accept (to be dropped in).
	 */
	public void setDroppable(String droppable) {
		if (droppable != null
		&& (droppable.length() == 0 || "false".equals(droppable)))
			droppable = null;

		if (!Objects.equals(_auxinf != null ? _auxinf.droppable: null, droppable)) {
			initAuxInfo().droppable = droppable;
			smartUpdate("droppable", droppable);
		}
	}
	/** Returns the identifier, or a list of identifiers of a droppable type
	 * of objects, or "false"
	 * if not droppable (never null nor empty).
	 */
	public String getDroppable() {
		return _auxinf != null && _auxinf.droppable != null ?
			_auxinf.droppable: "false";
	}

	/** Sets focus to this element. If an element does not accept focus,
	 * this method has no effect.
	 */
	public void focus() {
		response(new AuFocus(this));
	}
	/** Sets focus to this element.
	 * It is same as {@link #focus}, but used to allow ZUML to set focus
	 * to particular component.
	 *
	 * <pre><code>&lt;textbox focus="true"/&gt;</code></pre>
	 *
	 * @param focus whether to set focus. If false, this method has no effect.
	 * @since 3.0.5
	 */
	public void setFocus(boolean focus) {
		if (focus) focus();
	}

	/**
	 * Sets vertical flexibility hint of this component. 
	 * <p>Number flex indicates how 
	 * this component's container distributes remaining empty space among its 
	 * children vertically. Flexible component grow and shrink to fit their 
	 * given space. Flexible components with larger flex values will be made 
	 * larger than components with lower flex values, at the ratio determined by 
	 * all flexible components. The actual flex value is not relevant unless 
	 * there are other flexible components within the same container. Once the 
	 * default sizes of components in a container are calculated, the remaining 
	 * space in the container is divided among the flexible components, 
	 * according to their flex ratios.</p>
	 * <p>Specify a flex value of negative value, 0,
	 * or "false" has the same effect as leaving the flex attribute out entirely. 
	 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
	 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
	 * given to this flexible component to enclose all of its children components.
	 * That is, the flexible component grow and shrink to fit its children components.</p> 
	 * 
	 * @param flex the vertical flex hint.
	 * @since 5.0.0
	 * @see #setHflex
	 * @see #getVflex 
	 */
	public void setVflex(String flex) {
		if (flex != null && flex.length() == 0)
			flex = null;
		if (!Objects.equals(getVflex(), flex)) {
			initAuxInfo().vflex = flex;
			smartUpdate("vflex", getVflex());
		}
	}
	/**
	 * Return vertical flex hint of this component.
	 * <p>Default: null
	 * @return vertical flex hint of this component.
	 * @since 5.0.0
	 * @see #setVflex 
	 */
	public String getVflex() {
		return _auxinf != null ? _auxinf.vflex: null;
	}
	/**
	 * Sets horizontal flex hint of this component.
	 * <p>Number flex indicates how 
	 * this component's container distributes remaining empty space among its 
	 * children horizontally. Flexible component grow and shrink to fit their 
	 * given space. Flexible components with larger flex values will be made 
	 * larger than components with lower flex values, at the ratio determined by 
	 * all flexible components. The actual flex value is not relevant unless 
	 * there are other flexible components within the same container. Once the 
	 * default sizes of components in a container are calculated, the remaining 
	 * space in the container is divided among the flexible components, 
	 * according to their flex ratios.</p>
	 * <p>Specify a flex value of negative value, 0, or "false" has the same 
	 * effect as leaving the flex attribute out entirely. 
	 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
	 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
	 * given to this flexible component to enclose all of its children components.
	 * That is, the flexible component grow and shrink to fit its children components.</p> 
	 * @param flex horizontal flex hint of this component.
	 * @since 5.0.0 
	 * @see #setVflex
	 * @see #getHflex 
	 */
	public void setHflex(String flex) {
		if (flex != null && flex.length() == 0)
			flex = null;
		if (!Objects.equals(getHflex(), flex)) {
			initAuxInfo().hflex = flex;
			smartUpdate("hflex", getHflex());
		}
	}
	
	/**
	 * Returns horizontal flex hint of this component.
	 * <p>Default: null
	 * @return horizontal flex hint of this component.
	 * @since 5.0.0
	 * @see #setHflex 
	 */
	public String getHflex() {
		return _auxinf != null ? _auxinf.hflex: null;
	}

	/** Returns the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: -1 (don't wait).
	 * @since 5.0.2
	 */
	public int getRenderdefer() {
		return _auxinf != null ? _auxinf.renderdefer: -1;
	}
	/** Sets the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: -1 (don't wait).
	 *
	 * <p>This method is useful if you have a sophiscated page that takes
	 * long to render at a slow client. You can specify a non-negative value
	 * as the render-defer delay such that the other part of the UI can appear
	 * earlier. The styling of the render-deferred widget is controlled by
	 * a CSS class called <code>z-renderdefer</code>.
	 *
	 * <p>Notice that it has no effect if the component has been rendered
	 * at the client.
	 * @param ms time to wait in milliseconds before rendering.
	 * Notice: 0 also implies deferring the rendering (just right after
	 * all others are renderred).
	 * @since 5.0.2
	 */
	public void setRenderdefer(int ms) {
		initAuxInfo().renderdefer = ms;
	}

	//-- rendering --//
	/** Renders the content of this component, excluding the enclosing
	 * tags and children.
	 *
	 * <p>See also
	 * <a href="http://docs.zkoss.org/zk/Render_Special_Properties">Render Special Properties</a>
	 * @since 5.0.0
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "vflex", getVflex());
		render(renderer, "hflex", getHflex());
		render(renderer, "tooltiptext", getTooltiptext());
		render(renderer, "width", getWidth());
		render(renderer, "height", getHeight());
		render(renderer, "sclass", getSclass());
		render(renderer, "zclass", _zclass);
		render(renderer, "style", getStyle());
		render(renderer, "left", getLeft());
		render(renderer, "top", getTop());

		final String draggable = _auxinf != null ? _auxinf.draggable: null;
		if (draggable != null
		&& (getParent() instanceof DragControl || !draggable.equals("false")))
			render(renderer, "draggable", draggable);

		render(renderer, "droppable", _auxinf != null ? _auxinf.droppable: null);

		int v = getZIndex();
		if (v >= 0) renderer.render("zIndex", v);
		v = getRenderdefer();
		if (v >= 0) renderer.render("renderdefer", v);

		render(renderer, "prolog", _prolog);
	}

	//--ComponentCtrl--//
	/** Processes an AU request.
	 *
	 * <p>Default: it handles onClick, onDoubleClick, onRightClick
	 * onMove, onSize, onZIndex.
	 * @since 5.0.0
	 */
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CLICK)
		|| cmd.equals(Events.ON_DOUBLE_CLICK)
		|| cmd.equals(Events.ON_RIGHT_CLICK)
		|| cmd.equals(Events.ON_MOUSE_OVER)
		|| cmd.equals(Events.ON_MOUSE_OUT)) {
			Events.postEvent(MouseEvent.getMouseEvent(request));
		} else if (cmd.equals(Events.ON_OK) || cmd.equals(Events.ON_CANCEL)
		|| cmd.equals(Events.ON_CTRL_KEY)) {
			Events.postEvent(KeyEvent.getKeyEvent(request));
		} else if (cmd.equals(Events.ON_MOVE)) {
			MoveEvent evt = MoveEvent.getMoveEvent(request);
			setLeftDirectly(evt.getLeft());
			setTopDirectly(evt.getTop());
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_SIZE)) {
			SizeEvent evt = SizeEvent.getSizeEvent(request);
			setWidthDirectly(evt.getWidth());
			setHeightDirectly(evt.getHeight());
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_Z_INDEX)) {
			ZIndexEvent evt = ZIndexEvent.getZIndexEvent(request);
			setZIndexDirectly(evt.getZIndex());
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_DROP)) {
			DropEvent evt = DropEvent.getDropEvent(request);
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	/** Returns the client control for this component.
	 * It is used only by component developers.
	 *
	 * <p>Default: creates an instance of {@link HtmlBasedComponent.ExtraCtrl}.
	 */
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 *
	 * <p>If a component requires more client controls, it is suggested to
	 * override {@link #getExtraCtrl} to return an instance that extends from
	 * this class.
	 */
	protected class ExtraCtrl implements PrologAllowed {
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prolog = prolog;
		}
	}

	//Cloneable//
	public Object clone() {
		final HtmlBasedComponent clone = (HtmlBasedComponent)super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo)_auxinf.clone();
		return clone;
	}

	private final AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}
	/** Merge multiple memembers into an single object (and create on demand)
	 * to minimize the footprint
	 * @since 5.0.4
	 */
	private static class AuxInfo implements java.io.Serializable, Cloneable {
		/** The width. */
		private String width;
		/** The height. */
		private  String height;
		private String left;
		private String top;
		/** The virtical flex */
		private String vflex;
		/** The horizontal flex */
		private String hflex;
		/** The CSS class. */
		private String sclass;
		/** The CSS style. */
		private String style;
		/** The tooltip text. */
		private String tooltiptext;
		/** The draggable. */
		private String draggable;
		/** The droppable. */
		private String droppable;
		private int zIndex = -1;
		private int renderdefer = -1;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
