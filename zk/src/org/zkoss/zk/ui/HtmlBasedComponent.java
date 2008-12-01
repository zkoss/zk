/* HtmlBasedComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Dec 31 12:30:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.client.Movable;
import org.zkoss.zk.ui.ext.client.Sizable;
import org.zkoss.zk.ui.ext.client.ZIndexed;
import org.zkoss.zk.ui.ext.render.ZidRequired;
import org.zkoss.zk.ui.ext.render.Floating;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
import org.zkoss.zk.ui.sys.ComponentCtrl;
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
 * <li>{@link #getOuterAttrs}</li>
 * <li>{@link #getInnerAttrs}</li>
 * <li>{@link #focus}</li>
 * </ul>
 *
 * @author tomyeh
 */
abstract public class HtmlBasedComponent extends AbstractComponent implements org.zkoss.zk.ui.api.HtmlBasedComponent{
	private String _tooltiptext;
	/** The width. */
	private String _width;
	/** The height. */
	private String _height;
	/** The CSS class. */
	private String _sclass;
	/** The ZK CSS class. */
	protected String _zclass;
	/** The CSS style. */
	private String _style;
	private String _left, _top;
	/** The draggable. */
	private String _draggable;
	/** The droppable. */
	private String _droppable;
	private int _zIndex = -1;
	/** The prolog content that shall be generated before real content. */
	private String _prolog;

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
			smartUpdate("style.left", getLeft());
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
			smartUpdate("style.top", getTop());
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
				smartUpdate("style.zIndex", null);
			else
				smartUpdate("style.zIndex", _zIndex);
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
			smartUpdate("style.height", getHeight());
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
			smartUpdate("style.width", getWidth());
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
			smartUpdate("title", _tooltiptext);
		}
	}

	/** @deprecated As of release 3.5.1, replaced with {@link #getZclass}.
	 */
	public String getMoldSclass() {
		return getZclass();
	}
	/** @deprecated As of release 3.5.1, replaced with {@link #setZclass}.
	 */
	public void setMoldSclass(String moldSclass) {
		setZclass(moldSclass);
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
	  * @see #getRealSclass
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
			invalidate();
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
	 * @see #getRealSclass
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
			invalidate();
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
			smartUpdate("style", getRealStyle());
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
			smartUpdate("z.drag", _draggable);
		}
	}
	/** Returns the identifier of a draggable type of objects, or "false"
	 * if not draggable (never null nor empty).
	 */
	public final String getDraggable() {
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
			smartUpdate("z.drop", _droppable);
		}
	}
	/** Returns the identifier of a droppable type of objects, or "false"
	 * if not droppable (never null nor empty).
	 */
	public final String getDroppable() {
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

	//-- component developer only --//
	/** Returns the attributes for onClick, onRightClick and onDoubleClick
	 * by checking whether the corresponding listeners are added,
	 * or null if none is added.
	 *
	 * @since 3.0.5
	 */
	protected String getAllOnClickAttrs() {
		StringBuffer sb = null;
		sb = appendAsapAttr(sb, Events.ON_CLICK);
		sb = appendAsapAttr(sb, Events.ON_DOUBLE_CLICK);
		sb = appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
		return sb != null ? sb.toString():  null;
	}
	/** Returns the exterior attributes for generating the enclosing
	 * HTML tag; never return null.
	 *
	 * <p>Used only by component developers.
	 *
	 * <p>Default: Generates the tooltip text, style, sclass, draggable
	 * and droppable attribute if necessary.
	 * In other words, the corresponding attribute is generated if
	 * {@link #getTooltiptext}, {@link #getRealStyle},
	 * {@link #getSclass}, {@link #getDraggable}, {@link #getDroppable}
	 * are defined.
	 *
	 * <p>You have to call both {@link #getOuterAttrs} and
	 * {@link #getInnerAttrs} to generate complete attributes.
	 *
	 * <p>For simple components that all attributes are put on
	 * the outest HTML element, all you need is as follows.
	 *
	 * <pre><code>&lt;xx id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}&gt;</code></pre>
	 *
	 * <p>If you want to put attributes in a nested HTML element, you
	 * shall use the following pattern. Notice: if {@link #getInnerAttrs}
	 * in a different tag, the tag must be named with "${self.uuid}!real".
	 *
	 * <pre><code>&lt;xx id="${self.uuid}"${self.outerAttrs}&gt;
	 * &lt;yy id="${self.uuid}!real"${self.innerAttrs}&gt;...
	 *</code></pre>
	 *
	 * <p>Note: This class handles non-deferrable event listeners automatically.
	 * However, you have to invoke {@link #appendAsapAttr} for each event
	 * the component handles in {@link #getOuterAttrs} as follows.
	 *<pre><code>
	 *	appendAsapAttr(sb, Events.ON_OPEN);
	 *  appendAsapAttr(sb, Events.ON_CHANGE);
	 *</code></pre>
	 *
	 * <p>Theorectically, you could put any attributes in either
	 * {@link #getInnerAttrs} or {@link #getOuterAttrs}.
	 * However, zkau.js assumes all attributes are put at the outer one.
	 * If you want something different, you have to provide your own
	 * setAttr (refer to how checkbox is implemented).
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "class", getRealSclass());
		HTMLs.appendAttribute(sb, "style", getRealStyle());
		HTMLs.appendAttribute(sb, "title", getTooltiptext());
		HTMLs.appendAttribute(sb, "z.drag", _draggable);
		HTMLs.appendAttribute(sb, "z.drop", _droppable);
		HTMLs.appendAttribute(sb, "z.zcls", getZclass());

		final Object xc = getExtraCtrl();
		if ((xc instanceof ZidRequired) && ((ZidRequired)xc).isZidRequired())
			HTMLs.appendAttribute(sb, "z.zid", getId());
		if ((xc instanceof Floating) && ((Floating)xc).isFloating())
			sb.append(" z.float=\"true\"");
		return sb.toString();
	}
	/** Returns the interior attributes for generating the inner HTML tag;
	 * never return null.
	 *
	 * <p>Used only by component developers.
	 *
	 * <p>Default: empty string.
	 * Refer to {@link #getOuterAttrs} for more details.
	 */
	public String getInnerAttrs() {
		return "";
	}

	/** Returns the real style class that will be generated to the client
	 * (when {@link #getOuterAttrs} is called).
	 *
	 * <p>Default: it simply returns the catenation of {@link #getSclass}
	 * and {@link #getZclass()} (since 3.5.0).
	 *
	 * <p>Derived classes might override it to provide, say, dual style classes.
	 * For example,
	 * <pre><code>final String sclass = getSclass();
	 *return sclass != null ? sclass + " my-addon": "myaddon";</code></pre>
	 *
	 * @since 3.0.0
	 * @see #getZclass()
	 */
	protected String getRealSclass() {
		final String moldsclass = getZclass();
		final String sclass = getSclass();
		return moldsclass == null ? sclass : 
				sclass == null || sclass.length() == 0 ?
					moldsclass : sclass + " " + moldsclass;
	}
	/** Returns the real style that will be generated to client
	 * (when {@link #getOuterAttrs} is called).
	 *
	 * <p>Default: this method will append width, height and others
	 * to {@link #setStyle} (never null).
	 *
	 * <p>Use {@link #getRealStyleFlags} to control what attributes to
	 * exclude.
	 */
	protected String getRealStyle() {
		final int flags = getRealStyleFlags();
		final StringBuffer sb = new StringBuffer(32);

		if ((flags & RS_NO_WIDTH) == 0)
			HTMLs.appendStyle(sb, "width", getWidth());
		if ((flags & RS_NO_HEIGHT) == 0)
			HTMLs.appendStyle(sb, "height", getHeight());

		HTMLs.appendStyle(sb, "left", getLeft());
		HTMLs.appendStyle(sb, "top", getTop());

		final int zIndex = getZIndex();
		if (zIndex > 0)
			HTMLs.appendStyle(sb, "z-index", Integer.toString(zIndex));

		String style = getStyle();
		if (style != null && style.length() > 0) {
			sb.append(style);
			if (!style.endsWith(";")) sb.append(';');
		}
		if ((flags & RS_NO_DISPLAY) == 0 && !isVisible()) {
			if (sb.length() == 0)
				return "display:none;";
			sb.append("display:none;");
		}
		return sb.toString();
	}
	/** Used by {@link #getRealStyleFlags} to denote that {@link #getRealStyle}
	 * shall not generate the width style.
	 */
	protected static final int RS_NO_WIDTH = 0x0001;
	/** Used by {@link #getRealStyleFlags} to denote that {@link #getRealStyle}
	 * shall not generate the height style.
	 */
	protected static final int RS_NO_HEIGHT = 0x0002;
	/** Used by {@link #getRealStyleFlags} to denote that {@link #getRealStyle}
	 * shall not generate the display style.
	 */
	protected static final int RS_NO_DISPLAY = 0x0004;
	/** Returns a combination of {@link #RS_NO_WIDTH} and {@link #RS_NO_HEIGHT}.
	 * <p>Default: return 0.
	 */
	protected int getRealStyleFlags() {
		return 0;
	}

	//-- Component --//
	public void redraw(Writer out) throws IOException {
		if (_prolog != null) {
			final Writer o = out != null ? out: ZkFns.getCurrentOut();
			if (o != null) o.write(_prolog);
			//don't reset _prolog, since it might be redrawn later
		}
		super.redraw(out);
	}

	//--ComponentCtrl--//
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
	protected class ExtraCtrl implements Movable, Sizable, ZIndexed, PrologAllowed {
		//-- Movable --//
		public void setLeftByClient(String left) {
			_left =left;
		}
		public void setTopByClient(String top) {
			_top = top;
		}
		//-- Sizable --//
		public void setWidthByClient(String width) {
			_width = width;
		}
		public void setHeightByClient(String height) {
			_height = height;
		}
		//-- ZIndexed --//
		public void setZIndexByClient(int zIndex) {
			_zIndex = zIndex;
		}
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prolog = prolog;
		}
	}
}
