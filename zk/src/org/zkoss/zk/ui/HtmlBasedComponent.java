/* HtmlBasedComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Dec 31 12:30:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.fn.ZkFns;

/**
 * A skeletal implementation for HTML based components.
 * It simplifies to implement methods common to HTML based components.
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
 */
abstract public class HtmlBasedComponent extends AbstractComponent implements org.zkoss.zk.ui.api.HtmlBasedComponent{
	private String _tooltiptext;
	/** The width. */
	protected String _width;
	/** The height. */
	protected String _height;
	/** The CSS class. */
	private String _sclass;
	/** The ZK CSS class. */
	protected String _zclass;
	/** The CSS style. */
	private String _style;
	protected String _left, _top;
	/** The draggable. */
	private String _draggable;
	/** The droppable. */
	private String _droppable;
	private int _zIndex = -1;
	/** The prolog content that shall be generated before real content. */
	private String _prolog;

	static {
		addClientEvent(HtmlBasedComponent.class, Events.ON_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_DOUBLE_CLICK, 0);
		addClientEvent(HtmlBasedComponent.class, Events.ON_RIGHT_CLICK, 0);
	}

	protected HtmlBasedComponent() {
	}

	/** Returns the left position.
	 */
	public String getLeft() {
		return _left;
	}
	/** Sets the left position.
	 */
	public void setLeft(String left) {
		if (!Objects.equals(_left, left)) {
			_left = left;
			smartUpdate("left", _left);
		}
	}
	/** Returns the top position.
	 */
	public String getTop() {
		return _top;
	}
	/** Sets the top position.
	 */
	public void setTop(String top) {
		if (_top != top) {
			_top = top;
			smartUpdate("top", _top);
		}
	}
	/** Returns the Z index.
	 * <p>Default: -1 (means system default;
	 */
	public int getZIndex() {
		return _zIndex;
	}
	/** Sets the Z index.
	 */
	public void setZIndex(int zIndex) {
		if (_zIndex < -1)
			_zIndex = -1;
		if (_zIndex != zIndex) {
			_zIndex = zIndex;
			if (_zIndex < 0)
				smartUpdate("zIndex", (Object)null);
			else
				smartUpdate("zIndex", _zIndex);
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
		return _height;
	}
	/** Sets the height. If null, the best fit is used.
	 */
	public void setHeight(String height) {
		if (height != null && height.length() == 0)
			height = null;
		if (!Objects.equals(_height, height)) {
			_height = height;
			smartUpdate("height", _height);
		}
	}
	/** Returns the width. If null, the best fit is used.
	 * <p>Default: null.
	 */
	public String getWidth() {
		return _width;
	}
	/** Sets the width. If null, the best fit is used.
	 */
	public void setWidth(String width) {
		if (width != null && width.length() == 0)
			width = null;
		if (!Objects.equals(_width, width)) {
			_width = width;
			smartUpdate("width", _width);
		}
	}

	/** Returns the text as the tooltip.
	 * <p>Default: null.
	 */
	public String getTooltiptext() {
		return _tooltiptext;
	}
	/** Sets the text as the tooltip.
	 */
	public void setTooltiptext(String tooltiptext) {
		if (tooltiptext != null && tooltiptext.length() == 0)
			tooltiptext = null;
		if (!Objects.equals(_tooltiptext, tooltiptext)) {
			_tooltiptext = tooltiptext;
			smartUpdate("tooltiptext", _tooltiptext);
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
		return _sclass;
	}
	/** Sets the CSS class.
	 *
	 * @see #setZclass
	 */
	public void setSclass(String sclass) {
		if (sclass != null && sclass.length() == 0) sclass = null;
		if (!Objects.equals(_sclass, sclass)) {
			_sclass = sclass;
			smartUpdate("sclass", _sclass);
		}
	}
	/** Sets the CSS class. This method is a bit confused with Java's class,
	 * but we provide it for XUL compatibility.
	 * The same as {@link #setSclass}.
	 */
	public final void setClass(String sclass) {
		setSclass(sclass);
	}

	/** Returns the CSS style.
	 * <p>Default: null.
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the CSS style.
	 */
	public void setStyle(String style) {
		if (style != null && style.length() == 0) style = null;
		if (!Objects.equals(_style, style)) {
			_style = style;
			smartUpdate("style", _style);
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
	 * @param draggable "false", null or "" to denote non-draggable; "true" for draggable
	 * with anonymous identifier; others for an identifier of draggable.
	 */
	public void setDraggable(String draggable) {
		if (draggable != null
		&& (draggable.length() == 0 || "false".equals(draggable)))
			draggable = null;

		if (!Objects.equals(_draggable, draggable)) {
			_draggable = draggable;
			smartUpdate("draggable", _draggable); //getDraggable is final
		}
	}
	/** Returns the identifier of a draggable type of objects, or "false"
	 * if not draggable (never null nor empty).
	 */
	public final String getDraggable() { //Note: it is final
		return _draggable != null ? _draggable: "false";
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
	 * For example, if this component accpets dg1 and dg2, then
	 * assign "dg1, dg2" to this attribute.
	 *
	 * @param droppable "false", null or "" to denote not-droppable;
	 * "true" for accepting any draggable types; a list of identifiers,
	 * separated by comma for identifiers of draggables this compoent
	 * accept (to be dropped in).
	 */
	public void setDroppable(String droppable) {
		if (droppable != null
		&& (droppable.length() == 0 || "false".equals(droppable)))
			droppable = null;

		if (!Objects.equals(_droppable, droppable)) {
			_droppable = droppable;
			smartUpdate("droppable", _droppable); //getDroppable is final
		}
	}
	/** Returns the identifier of a droppable type of objects, or "false"
	 * if not droppable (never null nor empty).
	 */
	public final String getDroppable() { //Note: it is final
		return _droppable != null ? _droppable: "false";
	}

	/** Sets focus to this element. If an element does not accept focus,
	 * this method has no effect.
	 */
	public void focus() {
		response("focus", new AuFocus(this));
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

		render(renderer, "tooltiptext", _tooltiptext);
		render(renderer, "width", _width);
		render(renderer, "height", _height);
		render(renderer, "sclass", _sclass);
		render(renderer, "zclass", _zclass);
		render(renderer, "style", _style);
		render(renderer, "left", _left);
		render(renderer, "top", _top);
		render(renderer, "draggble", _draggable); //getDraggable is final
		render(renderer, "droppable", _droppable);  //getDroppable is final

		if (_zIndex >= 0) renderer.render("zIndex", _zIndex);

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
		final String name = request.getName();
		if (name.equals(Events.ON_CLICK)
		|| name.equals(Events.ON_DOUBLE_CLICK)
		|| name.equals(Events.ON_RIGHT_CLICK)) {
			Events.postEvent(MouseEvent.getMouseEvent(request));
		} else if (name.equals(Events.ON_MOVE)) {
			MoveEvent evt = MoveEvent.getMoveEvent(request);
			_left = evt.getLeft();
			_top = evt.getTop();
			Events.postEvent(evt);
		} else if (name.equals(Events.ON_SIZE)) {
			SizeEvent evt = SizeEvent.getSizeEvent(request);
			_width = evt.getWidth();
			_height = evt.getHeight();
			Events.postEvent(evt);
		} else if (name.equals(Events.ON_Z_INDEX)) {
			ZIndexEvent evt = ZIndexEvent.getZIndexEvent(request);
			_zIndex = evt.getZIndex();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	/** Used by {@link #getExtraCtrl} to create a client control.
	 * It is used only by component developers.
	 *
	 * <p>Defaut: creates an instance of {@link HtmlBasedComponent.ExtraCtrl}.
	 */
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 *
	 * <p>If a component requires more client controls, it is suggested to
	 * override {@link #newExtraCtrl} to return an instance that extends from
	 * this interface.
	 */
	protected class ExtraCtrl implements PrologAllowed {
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prolog = prolog;
		}
	}
}
