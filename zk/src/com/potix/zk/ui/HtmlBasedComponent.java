/* HtmlBasedComponent.java

{{IS_NOTE
	$Id: HtmlBasedComponent.java,v 1.11 2006/05/03 08:35:38 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sat Dec 31 12:30:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

import com.potix.lang.Objects;
import com.potix.lang.Strings;
import com.potix.util.logging.Log;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.AbstractComponent;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.ext.Moveable;
import com.potix.zk.ui.ext.ZIndexed;
import com.potix.zk.au.AuFocus;

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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.11 $ $Date: 2006/05/03 08:35:38 $
 */
abstract public class HtmlBasedComponent extends AbstractComponent
implements Moveable, ZIndexed {
	private static final Log log = Log.lookup(HtmlBasedComponent.class);

	private String _tooltiptext;
	/** The width. */
	private String _width;
	/** The height. */
	private String _height;
	/** The CSS class. */
	private String _sclass;
	/** The CSS style. */
	private String _style;
	private String _left, _top;
	/** The draggable. */
	private String _draggable;
	/** The droppable. */
	private String _droppable;
	private int _zIndex = -1;

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

	/** Returns the CSS class.
	 * Due to Java's limitation, we cannot use the name called getClas.
	 * <p>Default: null (the default value depends on element).
	 */
	public String getSclass() {
		return _sclass;
	}
	/** Sets the CSS class.
	 */
	public void setSclass(String sclass) {
		if (sclass != null && sclass.length() == 0) sclass = null;
		if (!Objects.equals(_sclass, sclass)) {
			_sclass = sclass;
			smartUpdate("class", getSclass());
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
			smartUpdate("zk_drag", _draggable);
		}
	}
	/** Returns the identifier of a draggable type of objects, or "false"
	 * if not draggable (never null or empty).
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
			smartUpdate("zk_drop", _droppable);
		}
	}
	/** Returns the identifier of a droppable type of objects, or "false"
	 * if not droppable (never null or empty).
	 */
	public final String getDroppable() {
		return _droppable != null ? _droppable: "false";
	}

	/** Sets focus to this element. For element that does not accept focus,
	 * this method has no effect.
	 */
	public void focus() {
		response("focus", new AuFocus(this));
	}

	//-- component developer only --//
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
	 * <p>Note: To handling an event, you do something similar as follows.
	 * <pre><code>
	 *	if (isAsapRequired("onCheck"))
	 *		HTMLs.appendAttribute(sb, "zk_onCheck", true);</code></pre>
	 *
	 * <p>Theorectically, you could put any attributes in either
	 * {@link #getInnerAttrs} or {@link #getOuterAttrs}.
	 * However, zkau.js assumes all attributes are put at the outer one.
	 * If you want something different, you have to provide your own
	 * setAttr (refer to how checkbox is implemented).
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "class", getSclass());
		HTMLs.appendAttribute(sb, "style", getRealStyle());
		HTMLs.appendAttribute(sb, "title", getTooltiptext());
		HTMLs.appendAttribute(sb, "zk_drag", _draggable);
		HTMLs.appendAttribute(sb, "zk_drop", _droppable);
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

	/** Returns the real style after appending width, height and others
	 * to {@link #setStyle}.
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
		if (!isVisible()) {
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
	/** Returns a combination of {@link #RS_NO_WIDTH} and {@link #RS_NO_HEIGHT}.
	 * <p>Default: return 0.
	 */
	protected int getRealStyleFlags() {
		return 0;
	}

	/** Returns whether to send back the request of the specified event
	 * immediately (ASAP). Returns true if you want the component (on the server)
	 * to process the event immediately.
	 *
	 * <p>Default: return true if any ASAP event handler or listener of
	 * the specified event is found. In other words, it returns
	 * {@link Events#isListenerAvailable}.
	 */
	protected boolean isAsapRequired(String evtnm) {
		return Events.isListenerAvailable(this, evtnm, true);
	}

	//-- Component --//
	public boolean addEventListener(String evtnm, EventListener listener) {
		if (isTransparent())
			return super.addEventListener(evtnm, listener);
			//Deriver has to handle it

		final boolean asap = isAsapRequired(evtnm);
		final boolean ret = super.addEventListener(evtnm, listener);
		if (ret && !asap && isAsapRequired(evtnm))
			smartUpdate("zk_" + evtnm, "true");
		return ret;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		if (isTransparent())
			return super.removeEventListener(evtnm, listener);

		final boolean asap = isAsapRequired(evtnm);
		final boolean ret = super.removeEventListener(evtnm, listener);
		if (ret && asap && !isAsapRequired(evtnm))
			smartUpdate("zk_" + evtnm, null);
		return ret;
	}

	//-- Moveable --//
	public void setLeftByClient(String left) {
		_left =left;
	}
	public void setTopByClient(String top) {
		_top = top;
	}
	public void setZIndexByClient(int zIndex) {
		_zIndex = zIndex;
	}
}
