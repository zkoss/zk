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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.html.HTMLs;
import org.zkoss.lang.Objects;
import org.zkoss.xml.XMLs;
import org.zkoss.zk.au.DeferredValue;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.render.DirectContent;
import org.zkoss.zk.ui.sys.BooleanPropertyAccess;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.StringPropertyAccess;

/**
 * The raw component used to generate raw HTML elements.
 *
 * <p>
 * Note: ZHTML components ignore the page listener since it handles non-deferrable event listeners
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 *
 * @author tomyeh
 */
public class AbstractTag extends AbstractComponent implements DynamicPropertied, RawId {
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

	/**
	 * Returns the CSS class. Due to Java's limitation, we cannot use the name called getClas.
	 * <p>
	 * Default: null (the default value depends on element).
	 */
	public String getSclass() {
		return (String) getDynamicProperty("class");
	}

	/**
	 * Sets the CSS class.
	 */
	public void setSclass(String sclass) {
		setDynamicProperty("class", sclass);
	}

	/**
	 * Returns the CSS style.
	 * <p>
	 * Default: null.
	 */
	public String getStyle() {
		return (String) getDynamicProperty("style");
	}

	/**
	 * Sets the CSS style.
	 *
	 * <p>
	 * Note: if display is not specified as part of style, the returned value of {@link #isVisible}
	 * is assumed. In other words, if not visible and display is not specified as part of style,
	 * "display:none" is appended.
	 *
	 * <p>
	 * On the other hand, if display is specified, then {@link #setVisible} is called to reflect the
	 * visibility, if necessary.
	 */
	public void setStyle(String style) {
		setDynamicProperty("style", style);
	}

	/**
	 * Returns the accesskey of this tag.
	 * @since 8.0.3
	 */
	public String getAccesskey() {
		return (String) getDynamicProperty("accesskey");
	}

	/**
	 * Sets the accesskey of this tag.
	 * @since 8.0.3
	 */
	public void setAccesskey(String accesskey) throws WrongValueException {
		setDynamicProperty("accesskey", accesskey);
	}
	/**
	 * Returns the contenteditable of this tag.
	 * @since 8.0.3
	 */
	public Boolean isContenteditable() {
		return (Boolean) getDynamicProperty("contenteditable");
	}

	/**
	 * Sets the contenteditable of this tag.
	 * @since 8.0.3
	 */
	public void setContenteditable(Boolean contenteditable) throws WrongValueException {
		setDynamicProperty("contenteditable", contenteditable);
	}
	/**
	 * Returns the dir of this tag.
	 * @since 8.0.3
	 */
	public String getDir() {
		return (String) getDynamicProperty("dir");
	}

	/**
	 * Sets the dir of this tag.
	 * @since 8.0.3
	 */
	public void setDir(String dir) throws WrongValueException {
		setDynamicProperty("dir", dir);
	}
	/**
	 * Returns the draggable of this tag.
	 * @since 8.0.3
	 */
	public Boolean isDraggable() {
		return (Boolean) getDynamicProperty("draggable");
	}

	/**
	 * Sets the draggable of this tag.
	 * @since 8.0.3
	 */
	public void setDraggable(Boolean draggable) throws WrongValueException {
		setDynamicProperty("draggable", draggable);
	}
	/**
	 * Returns the hidden of this tag.
	 * @since 8.0.3
	 */
	public Boolean isHidden() {
		return getDynamicProperty("hidden") != null;
	}

	/**
	 * Sets the hidden of this tag.
	 * @since 8.0.3
	 */
	public void setHidden(Boolean hidden) throws WrongValueException {
		setDynamicProperty("hidden", hidden ? true : null);
	}
	/**
	 * Returns the lang of this tag.
	 * @since 8.0.3
	 */
	public String getLang() {
		return (String) getDynamicProperty("lang");
	}

	/**
	 * Sets the lang of this tag.
	 * @since 8.0.3
	 */
	public void setLang(String lang) throws WrongValueException {
		setDynamicProperty("lang", lang);
	}
	/**
	 * Returns the spellcheck of this tag.
	 * @since 8.0.3
	 */
	public Boolean isSpellcheck() {
		return (Boolean) getDynamicProperty("spellcheck");
	}

	/**
	 * Sets the spellcheck of this tag.
	 * @since 8.0.3
	 */
	public void setSpellcheck(Boolean spellcheck) throws WrongValueException {
		setDynamicProperty("spellcheck", spellcheck);
	}
	/**
	 * Returns the tabindex of this tag.
	 * @since 8.0.3
	 */
	public Integer getTabindex() {
		return (Integer) getDynamicProperty("tabindex");
	}

	/**
	 * Sets the tabindex of this tag.
	 * @since 8.0.3
	 */
	public void setTabindex(Integer tabindex) throws WrongValueException {
		setDynamicProperty("tabindex", tabindex);
	}
	/**
	 * Returns the title of this tag.
	 * @since 8.0.3
	 */
	public String getTitle() {
		return (String) getDynamicProperty("title");
	}

	/**
	 * Sets the title of this tag.
	 * @since 8.0.3
	 */
	public void setTitle(String title) throws WrongValueException {
		setDynamicProperty("title", title);
	}

	/**
	 * Returns the tag name.
	 */
	public String getTag() {
		return _tagnm;
	}

	// -- DynamicPropertys --//
	public boolean hasDynamicProperty(String name) {
		return ComponentsCtrl.isReservedAttribute(name);
	}

	/**
	 * Returns the dynamic property, or null if not found. Note: it must be a String object or null.
	 */
	public Object getDynamicProperty(String name) {
		return _props != null ? _props.get(name) : null;
	}

	/**
	 * Sets the dynamic property. Note: it converts the value to a string object (by use of
	 * {@link Objects#toString}).
	 *
	 * <p>
	 * Note: it handles the style property specially. Refer to {@link #setStyle} for details.
	 */
	public void setDynamicProperty(String name, Object value) throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name is required");
		if (!hasDynamicProperty(name))
			throw new WrongValueException("Attribute not allowed: " + name
					+ "\nSpecify the ZK namespace if you want to use special ZK attributes");

		String sval = Objects.toString(value);
		if ("style".equals(name)) {
			sval = filterStyle(sval);
			setDynaProp(name, sval);
		} else if ("src".equals(name)) {
			// ZK-3011: should defer until render
			EncodedURL url = new EncodedURL(sval);
			setDynaProp(name, url);
			smartUpdate("dynamicProperty", new Object[] { name, url }, true);
			return;
		} else if ("textContent".equals(name)) {
			setDynaProp(name, sval);
			if (!getChildren().isEmpty())
				invalidate();
		} else
			setDynaProp(name, value);
		// B80-ZK-2716: style and textContent are both dynamiccProperty
		smartUpdate("dynamicProperty", new String[] { name, sval }, true);
	}

	private String getEncodedURL(String src) {
		final Desktop dt = getDesktop(); // it might not belong to any desktop
		return dt != null ? dt.getExecution().encodeURL(src != null ? src : "~./img/spacer.gif")
				: "";
	}

	/** Processes the style. */
	private String filterStyle(String style) {
		if (style != null) {
			final int j = HTMLs.getSubstyleIndex(style, "display");
			if (j >= 0) { // display is specified
				super.setVisible(!"none".equals(HTMLs.getSubstyleValue(style, j)));
				return style; // done
			}
		}

		if (!isVisible()) {
			int len = style != null ? style.length() : 0;
			if (len == 0)
				return "display:none;";
			if (style.charAt(len - 1) != ';')
				style += ';';
			style += "display:none;";
		}
		return style;
	}

	/** Set the dynamic property 'blindly'. */
	private void setDynaProp(String name, Object value) {
		if (value == null) {
			if (_props != null)
				_props.remove(name);
		} else {
			if (_props == null)
				_props = new LinkedHashMap<String, Object>();
			_props.put(name, value);
		}
	}

	/**
	 * Whether to hide the id attribute.
	 * <p>
	 * Default: false.
	 * <p>
	 * Some tags, such as {@link org.zkoss.zhtml.Html}, won't generate the id attribute. They shall
	 * override this method to return true.
	 */
	protected boolean shallHideId() {
		return false;
	}

	// -- Component --//
	/**
	 * Changes the visibility of this component.
	 * <p>
	 * Note: it will adjust the style ({@link #getStyle}) based on the visibility.
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
							if (k >= 0)
								newstyle += style.substring(k + 1);
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
							if (k >= 0)
								newstyle += style.substring(k + 1);
							setDynaProp("style", newstyle);
						}
					} else {
						final int len = style.length();
						String newstyle = len > 0 && style.charAt(len - 1) != ';' ? style + ';'
								: style;
						setDynaProp("style", style + "display:none;");
					}
				}
			}
		}
		return old;
	}

	/**
	 * Returns the widget class, "zhtml.Widget".
	 * 
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		return "zhtml.Widget";
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (_tagnm == null)
			throw new UiException("The tag name is not initialized yet");

		final Execution exec = Executions.getCurrent();
		if (exec == null || exec.isAsyncUpdate(null) || !HtmlPageRenders.isDirectContent(exec)) {
			super.redraw(out); // generate JavaScript
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
		rc.renderBegin(this, getClientEvents(), getSpecialRendererOutput(this), false);

		redrawChildrenDirectly(rc, exec, out);

		out.write(getEpilogHalf());
		rc.renderEnd(this);

		if (rcRequired) {
			out.write(rc.complete());
			PageRenderer.afterRenderTag(exec, ret);
		}
	}

	/**
	 * Renders the children directly to the given output. Notice it is called only if
	 * {@link #redraw} is going to render the content (HTML tags) directly. If it is about to
	 * generate the JavaScript code {@link #redrawChildren} will be called instead.
	 * <p>
	 * You have to override this method if the deriving class has additional information to render.
	 * 
	 * @since 5.0.7
	 */
	protected void redrawChildrenDirectly(TagRenderContext rc, Execution exec, java.io.Writer out)
			throws java.io.IOException {
		for (Component child = getFirstChild(); child != null;) {
			Component next = child.getNextSibling();
			if (((ComponentCtrl) child).getExtraCtrl() instanceof DirectContent) {
				((ComponentCtrl) child).redraw(out);
			} else {
				HtmlPageRenders.setDirectContent(exec, false);
				rc.renderBegin(child, null, getSpecialRendererOutput(child), true);
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
	 * @param hideUuidIfNoId
	 *            whether not to generate UUID if possible
	 */
	/* package */ String getPrologHalf(boolean hideUuidIfNoId) {
		final StringBuilder sb = new StringBuilder(128).append('<').append(_tagnm);

		if ((!hideUuidIfNoId && !shallHideId()) || getId().length() > 0)
			sb.append(" id=\"").append(getUuid()).append('"');

		if (_props != null) {
			for (Iterator it = _props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry) it.next();
				if (!"textContent".equals(me.getKey())) { // ignore textContent
					// ZK-3011: should getValue if it's a deferredValue
					Object v = me.getValue();
					if (v instanceof DeferredValue) {
						v = ((DeferredValue) v).getValue();
					}
					sb.append(' ').append(me.getKey()).append("=\"")
							.append(XMLs.encodeAttribute(Objects.toString(v))).append('"');
				}
			}
		}

		if (!isOrphanTag())
			sb.append('/');

		sb.append('>');

		Object textContent = getDynamicProperty("textContent");
		if (textContent != null)
			sb.append(XMLs.escapeXML((String) textContent));
		return sb.toString();
	}

	/* package */ String getEpilogHalf() {
		return isOrphanTag() ? "</" + _tagnm + '>' : "";
	}

	protected boolean isChildable() {
		return isOrphanTag();
	}

	/**
	 * Returns whether this tag is an orphan tag, i.e., it shall be in the form of &lt;tag/&gt;.
	 * 
	 * @since 5.0.8
	 */
	protected boolean isOrphanTag() {
		return !HTMLs.isOrphanTag(_tagnm);
	}

	// --ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(
			5);

	static {
		_properties.put("id", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((AbstractTag) cmp).setId(value);
			}

			public String getValue(Component cmp) {
				return ((AbstractTag) cmp).getId();
			}
		});
		_properties.put("sclass", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((AbstractTag) cmp).setSclass(value);
			}

			public String getValue(Component cmp) {
				return ((AbstractTag) cmp).getSclass();
			}
		});
		_properties.put("style", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((AbstractTag) cmp).setStyle(value);
			}

			public String getValue(Component cmp) {
				return ((AbstractTag) cmp).getStyle();
			}
		});
		_properties.put("visible", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean value) {
				((AbstractTag) cmp).setVisible(value);
			}

			public Boolean getValue(Component cmp) {
				return ((AbstractTag) cmp).isVisible();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	// Cloneable//
	public Object clone() {
		final AbstractTag clone = (AbstractTag) super.clone();
		if (clone._props != null)
			clone._props = new LinkedHashMap<String, Object>(clone._props);
		return clone;
	}

	// Object//
	public String toString() {
		return "[" + _tagnm + ' ' + super.toString() + ']';
	}

	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl implements DirectContent {
	}

	// ZK-3097
	private class EncodedURL implements org.zkoss.zk.au.DeferredValue, Serializable {
		private String _src;

		public EncodedURL(String src) {
			_src = src;
		}

		public Object getValue() {
			return getEncodedURL(_src);
		}
	}
}
