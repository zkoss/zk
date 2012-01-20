/* AbstractTag.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct  4 09:15:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml.impl;

import java.lang.Object;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import org.zkoss.lang.Objects;
import org.zkoss.xml.XMLs;
import org.zkoss.html.HTMLs;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.render.DirectContent;

/**
 * The raw component used to generate raw HTML elements.
 *
 * <p>Note: ZHTML components ignore the page listener since it handles
 * non-deferrable event listeners
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 *
 * @author tomyeh
 */
public class AbstractTag extends AbstractComponent
implements DynamicPropertied, RawId {
	static {
		addClientEvent(AbstractTag.class, Events.ON_CLICK, 0);
	}

	/** The tag name. */
	protected String _tagnm;
	private Map<String, Object> _props;

	protected AbstractTag(String tagname) {
		if (tagname == null || tagname.length() == 0)
			throw new IllegalArgumentException("A tag name is required");
		_tagnm = tagname;
	}
	protected AbstractTag() {
	}

	/** Returns the CSS class.
	 * Due to Java's limitation, we cannot use the name called getClas.
	 * <p>Default: null (the default value depends on element).
	 */
	public String getSclass() {
		return (String)getDynamicProperty("class");
	}
	/** Sets the CSS class.
	 */
	public void setSclass(String sclass) {
		setDynamicProperty("class", sclass);
	}
	/** Returns the CSS style.
	 * <p>Default: null.
	 */
	public String getStyle() {
		return (String)getDynamicProperty("style");
	}
	/** Sets the CSS style.
	 *
	 * <p>Note: if display is not specified as part of style,
	 * the returned value of {@link #isVisible} is assumed.
	 * In other words, if not visible and dispaly is not specified as part of style,
	 * "display:none" is appended.
	 *
	 * <p>On the other hand, if display is specified, then {@link #setVisible}
	 * is called to reflect the visibility, if necessary.
	 */
	public void setStyle(String style) {
		setDynamicProperty("style", style);
	}

	/** Returns the tag name.
	 */
	public String getTag() {
		return _tagnm;
	}

	//-- DynamicPropertys --//
	public boolean hasDynamicProperty(String name) {
		return ComponentsCtrl.isReservedAttribute(name);
	}
	/** Returns the dynamic property, or null if not found.
	 * Note: it must be a String object or null.
	 */
	public Object getDynamicProperty(String name) {
		return _props != null ? _props.get(name): null;
	}
	/** Sets the dynamic property.
	 * Note: it converts the value to a string object (by use of
	 * {@link Objects#toString}).
	 *
	 * <p>Note: it handles the style property specially. Refer to {@link #setStyle}
	 * for details.
	 */
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name is required");
		if (!hasDynamicProperty(name))
			throw new WrongValueException("Attribute not allowed: "+name+"\nSpecify the ZK namespace if you want to use special ZK attributes");

		String sval = Objects.toString(value);
		if ("style".equals(name)) {
			sval = filterStyle(sval);
			setDynaProp(name, sval);
		} else
			setDynaProp(name, value);
		smartUpdate("dynamicProperty", new String[] {name, sval});
	}
	/** Processes the style. */
	private String filterStyle(String style) {
		if (style != null) {
			final int j = HTMLs.getSubstyleIndex(style, "display");
			if (j >= 0) { //display is specified
				super.setVisible(
					!"none".equals(HTMLs.getSubstyleValue(style, j)));
				return style; //done
			}
		}

		if (!isVisible()) {
			int len = style != null ? style.length(): 0;
			if (len == 0) return "display:none;";
			if (style.charAt(len - 1) != ';') style += ';';
			style += "display:none;";
		}
		return style;
	}
	/** Set the dynamic property 'blindly'. */
	private void setDynaProp(String name, Object value) {
		if (value == null) {
			if (_props != null) _props.remove(name);
		} else {
			if (_props == null)
				_props = new LinkedHashMap<String, Object>();
			_props.put(name, value);
		}
	}

	/** Whether to hide the id attribute.
	 * <p>Default: false.
	 * <p>Some tags, such as {@link org.zkoss.zhtml.Html}, won't generate the id attribute.
	 * They shall override this method to return true.
	 */
	protected boolean shallHideId() {
		return false;
	}

	//-- Component --//
	/** Changes the visibility of this component.
	 * <p>Note: it will adjust the style ({@link #getStyle}) based on the visibility.
	 *
	 * @return the previous visibility
	 */
	public boolean setVisible(boolean visible) {
		final boolean old = super.setVisible(visible);
		if (old != visible) {
			final String style = getStyle();
			if (visible) {
				if (style != null) {
					final int j = HTMLs.getSubstyleIndex(style, "display");
					if (j >= 0) {
						final String val = HTMLs.getSubstyleValue(style, j);
						if ("none".equals(val)) {
							String newstyle = style.substring(0, j);
							final int k = style.indexOf(';', j + 7);
							if (k >= 0) newstyle += style.substring(k + 1);
							setDynaProp("style", newstyle);
						}
					}
				}
			} else {
				if (style == null) {
					setDynaProp("style", "display:none;");
				} else {
					final int j = HTMLs.getSubstyleIndex(style, "display");
					if (j >= 0) {
						final String val = HTMLs.getSubstyleValue(style, j);
						if (!"none".equals(val)) {
							String newstyle = style.substring(0, j) + "display:none;";
							final int k = style.indexOf(';', j + 7);
							if (k >= 0) newstyle += style.substring(k + 1);
							setDynaProp("style", newstyle);
						}
					} else {
						final int len = style.length();
						String newstyle =
							len > 0 && style.charAt(len - 1) != ';' ?
								style + ';': style;
						setDynaProp("style", style + "display:none;");
					}
				}
			}
		}
		return old;
	}

	/** Returns the widget class, "zhtml.Widget".
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		return "zhtml.Widget";
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (_tagnm == null)
			throw new UiException("The tag name is not initialized yet");

		final Execution exec = Executions.getCurrent();
		if (exec == null || exec.isAsyncUpdate(null)
		|| !HtmlPageRenders.isDirectContent(exec)) {
			super.redraw(out); //generate JavaScript
			return;
		}

		TagRenderContext rc = PageRenderer.getTagRenderContext(exec);
		final boolean rcRequired = rc == null;
		Object ret = null;
		if (rcRequired) {
			ret = PageRenderer.beforeRenderTag(exec);
			rc = PageRenderer.getTagRenderContext(exec);
		}

		out.write(getPrologHalf(false));
		rc.renderBegin(this, getClientEvents(), false);

		redrawChildrenDirectly(rc, exec, out);

		out.write(getEpilogHalf());
		rc.renderEnd(this);

		if (rcRequired) {
			out.write(rc.complete());
			PageRenderer.afterRenderTag(exec, ret);
		}
	}
	/** Renders the children directly to the given output.
	 * Notice it is called only if {@link #redraw} is going to render
	 * the content (HTML tags) directly.
	 * If it is about to generate the JavaScript code
	 * {@link #redrawChildren} will be called instead.
	 * <p>You have to override this method if the deriving class
	 * has additional information to render.
	 * @since 5.0.7
	 */
	protected void redrawChildrenDirectly(TagRenderContext rc, Execution exec,
	java.io.Writer out) throws java.io.IOException {
		for (Component child = getFirstChild(); child != null;) {
			Component next = child.getNextSibling();
			if (((ComponentCtrl)child).getExtraCtrl() instanceof DirectContent) {
				((ComponentCtrl)child).redraw(out);
			} else {
				HtmlPageRenders.setDirectContent(exec, false);
				rc.renderBegin(child, null, true);
				HtmlPageRenders.outStandalone(exec, child, out);
				rc.renderEnd(child);
				HtmlPageRenders.setDirectContent(exec, true);
			}
			child = next;
		}
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "prolog", getPrologHalf(false));
		render(renderer, "epilog", getEpilogHalf());
	}
	/**
	 * @param hideUuidIfNoId whether not to generate UUID if possible
	 */
	/*package*/ String getPrologHalf(boolean hideUuidIfNoId) {
		final StringBuffer sb = new StringBuffer(128)
			.append('<').append(_tagnm);

		if ((!hideUuidIfNoId && !shallHideId()) || getId().length() > 0)
			sb.append(" id=\"").append(getUuid()).append('"');

		if (_props != null) {
			for (Iterator it = _props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				sb.append(' ').append(me.getKey()).append("=\"")
					.append(XMLs.encodeAttribute(Objects.toString(me.getValue())))
					.append('"');
			}
		}

		if (!isOrphanTag())
			sb.append('/');

		return sb.append('>').toString();
	}
	/*package*/ String getEpilogHalf() {
		return isOrphanTag() ? "</" + _tagnm + '>': "";
	}
	protected boolean isChildable() {
		return isOrphanTag();
	}
	/** Returns whether this tag is an orphan tag, i.e., it shall be in the
	 * form of &lt;tag/&gt;.
	 * @since 5.0.8
	 */
	protected boolean isOrphanTag() {
		return !HTMLs.isOrphanTag(_tagnm);
	}

	//Cloneable//
	public Object clone() {
		final AbstractTag clone = (AbstractTag)super.clone();
		if (clone._props != null)
			clone._props = new LinkedHashMap<String, Object>(clone._props);
		return clone;
	}

	//Object//
	public String toString() {
		return "["+_tagnm+' '+super.toString()+']';
	}

	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl implements DirectContent {
	}
}
