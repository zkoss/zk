/* AbstractTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct  4 09:15:59     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.lang.Objects;
import org.zkoss.xml.XMLs;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.RawId;

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
	/** The tag name. */
	protected String _tagnm;
	private Map _props;

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
		if ("style".equals(name)) sval = filterStyle(sval);

		setDynaProp(name, sval);
		smartUpdate(name, sval);
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
	private void setDynaProp(String name, String value) {
		if (value == null) {
			if (_props != null) _props.remove(name);
		} else {
			if (_props == null)
				_props = new LinkedHashMap();
			_props.put(name, value);
		}
	}

	/** Whether to hide the id attribute.
	 * <p>Default: false.
	 * <p>Some tags, such as {@link org.zkoss.zhtml.Html}, won't generate the id attribute.
	 * They will override this method to return true.
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
			final String style = (String)getDynamicProperty("style");
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
	public boolean addEventListener(String evtnm, EventListener listener) {
		final EventInfo ei;
		for (int j = 0;; ++j) {
			if (j >= _evts.length)
				throw new UiException("Not supported event: "+evtnm);
			if (_evts[j].name.equals(evtnm)) { //found
				ei = _evts[j];
				break;
			}
		}

		final boolean bAddType = ei.typed && !isTypeDeclared();
		final boolean ret = super.addEventListener(evtnm, listener);
		if (ret) {
			smartUpdate(ei.attr,
				Events.isListened(this, evtnm, true) ? "true": null);
				//Bug 1477271: Tom M Yeh: 
				//We check non-deferable only. Otherwise, if users add a page
				//event listener, all ZHTML will generate z.onChange.
			if (bAddType && isTypeDeclared()) {
				smartUpdate("z.type", "zhtml.main.Raw");
				smartUpdate("z.init", true);
			}
		}
		return ret;
	}
	private boolean isTypeDeclared() {
		for (int j = 0; j < _evts.length; ++j)
			if (_evts[j].typed
			&& Events.isListened(this, _evts[j].name, true)) //asap only
				return true;
		return false;
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (_tagnm == null)
			throw new UiException("The tag name is not initialized yet");

		out.write('<');
		out.write(_tagnm);

		boolean typeDeclared = false;
		for (int j = 0; j < _evts.length; ++j) {
			if (Events.isListened(this, _evts[j].name, true)) { //asap only
				if (_evts[j].typed) typeDeclared = true;
				out.write(' ');
				out.write(_evts[j].attr);
				out.write("=\"true\"");
			}
		}

		if (typeDeclared)
			out.write(" z.type=\"zhtml.main.Raw\"");

		if (typeDeclared || !shallHideId() || !Components.isAutoId(getUuid())) {
			out.write(" id=\"");
			out.write(getUuid());
			out.write('"');
		}

		if (_props != null) {
			for (Iterator it = _props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String key = (String)me.getKey();
				final String val = (String)me.getValue();
				out.write(' ');
				out.write(key);
				out.write("=\"");
				out.write(XMLs.encodeAttribute(val));
				out.write('"');
			}
		}

		if (isChildable()) {
			boolean divGened = false;
			if ("body".equals(_tagnm)) {
				if (_props != null && _props.containsKey("class")) {
					out.write("><div class=\"zk\">\n");
					divGened = true;
				} else {
					out.write(" class=\"zk\">\n");
				}
			} else {
				out.write('>');
			}

			for (Iterator it = getChildren().iterator(); it.hasNext();)
				((Component)it.next()).redraw(out);

			if (divGened)
				out.write("\n</div>");
			out.write("</");
			out.write(_tagnm);
			out.write('>');
		} else {
			out.write("/>");
		}
	}
	public boolean isChildable() {
		return !HTMLs.isOrphanTag(_tagnm);
	}

	//Cloneable//
	public Object clone() {
		final AbstractTag clone = (AbstractTag)super.clone();
		if (clone._props != null)
			clone._props = new LinkedHashMap(clone._props);
		return clone;
	}

	//Object//
	public String toString() {
		return "["+_tagnm+' '+super.toString()+']';
	}

	private static class EventInfo {
		/** The event name.
		 */
		private final String name;
		/** The attribute that will be generated to the client side.
		 */
		private final String attr;
		/** Whether to generate z.type
		 */
		private final boolean typed;
		private EventInfo(String name, String attr, boolean typed) {
			this.name = name;
			this.attr = attr;
			this.typed = typed;
		}
	}
	private static final EventInfo[] _evts = {
		new EventInfo(Events.ON_CLICK, "z.lfclk", false),
		new EventInfo(Events.ON_CHANGE, "z.onChange", true)
	};
}
