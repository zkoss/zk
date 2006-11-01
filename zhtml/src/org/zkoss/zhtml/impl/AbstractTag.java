/* AbstractTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct  4 09:15:59     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.lang.Objects;
import org.zkoss.xml.XMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.RawId;

/**
 * The raw component used to generate raw HTML elements.
 *
 * <p>Note: ZHTML components ignore the page listener since it handles
 * only ASAP event listeners.
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
		return name != null && !"use".equals(name) && !"if".equals(name)
			&& !"unless".equals(name);
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
	 */
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name is required");
		if (!hasDynamicProperty(name))
			throw new WrongValueException("Attribute not allowed: "+name+"\nSpecify the ZK namespace if you want to use special ZK attributes");

		final String sval = Objects.toString(value);
		if (sval == null) {
			if (_props != null) _props.remove(name);
		} else {
			if (_props == null)
				_props = new HashMap();
			_props.put(name, sval);
		}
		smartUpdate(name, sval);
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
	public boolean addEventListener(String evtnm, EventListener listener) {
		if (!listener.isAsap())
			throw new UiException("ZHTML accepts only ASAP listener");

		//Bug 1477271: Tom M Yeh: 
		//We can check ASAP only. Otherwise, if users add a page listener,
		//all ZHTML will generate z:onChange. Too complicate!

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
				Events.isListenerAvailable(this, evtnm, true) ? "true": null); //asap only
			if (bAddType && isTypeDeclared()) {
				smartUpdate("z:type", "zhtml.main.Raw");
				smartUpdate("z:init", true);
			}
		}
		return ret;
	}
	private boolean isTypeDeclared() {
		for (int j = 0; j < _evts.length; ++j)
			if (_evts[j].typed
			&& Events.isListenerAvailable(this, _evts[j].name, true)) //asap only
				return true;
		return false;
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (_tagnm == null)
			throw new UiException("The tag name is not initialized yet");

		out.write('<');
		out.write(_tagnm);

		if ("html".equals(_tagnm))
			out.write(" xmlns:z=\"http://www.zkoss.org/2005/zk\"");

		boolean typeDeclared = false;
		for (int j = 0; j < _evts.length; ++j) {
			if (Events.isListenerAvailable(this, _evts[j].name, true)) { //asap only
				if (_evts[j].typed) typeDeclared = true;
				out.write(' ');
				out.write(_evts[j].attr);
				out.write("=\"true\"");
			}
		}

		if (typeDeclared)
			out.write(" z:type=\"zhtml.main.Raw\"");

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
			out.write('>');

			for (Iterator it = getChildren().iterator(); it.hasNext();)
				((Component)it.next()).redraw(out);

			out.write("</");
			out.write(_tagnm);
			out.write('>');
		} else {
			out.write("/>");
		}
	}
	public boolean isChildable() {
		return !_childless.contains(_tagnm);
	}

	//Cloneable//
	public Object clone() {
		final AbstractTag clone = (AbstractTag)super.clone();
		clone._props = new HashMap(clone._props);
		return clone;
	}

	//Object//
	public String toString() {
		return "["+_tagnm+' '+getId()+']';
	}

	/** A set of tags that don't have child. */
	private static final Set _childless = new HashSet(29);
	static {
		final String[] childless = {
			"area", "base", "basefont", "bgsound", "br",
			"col", "colgroup", "embed", "hr", "img", "input",
			"isindex", "keygen", "link", "meta", "plaintext",
			"spacer", "wbr"
		};
		for (int j = childless.length; --j >= 0;)
			_childless.add(childless[j]);
	}

	private static class EventInfo {
		/** The event name.
		 */
		private final String name;
		/** The attribute that will be generated to the client side.
		 */
		private final String attr;
		/** Whether to generate z:type
		 */
		private final boolean typed;
		private EventInfo(String name, String attr, boolean typed) {
			this.name = name;
			this.attr = attr;
			this.typed = typed;
		}
	}
	private static final EventInfo[] _evts = {
		new EventInfo(Events.ON_CLICK, "z:lfclk", false),
		new EventInfo(Events.ON_CHANGE, "z:onChange", true)
	};
}
