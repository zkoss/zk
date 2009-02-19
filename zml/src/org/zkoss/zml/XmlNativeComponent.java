/* XmlNativeComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 23 17:29:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zml;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.xml.HTMLs;
import org.zkoss.xml.XMLs;
import org.zkoss.idom.Namespace;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.impl.NativeHelpers;

/**
 * A comonent used to represent XML elements that are associated
 * with the inline namespace (http://www.zkoss.org/2005/zk/inline).
 *
 * <p>It contains the content that shall be sent directly to client.
 * It has three parts: prolog, children and epilog.
 * The prolog ({@link #getPrologContent}) and epilog ({@link #getEpilogContent})
 * are both {@link String}.
 *
 * <p>When this component is renderred ({@link #redraw}), it generates
 * the prolog first, the children and then the epilog.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class XmlNativeComponent extends AbstractComponent
implements DynamicTag, Native {
	private static final Helper _helper = new XmlHelper();

	private String _tag;
	private String _prolog = "", _epilog = "";
	private Map _props;
	/** Declared namespaces ({@link Namespace}). */
	private List _dns;

	public XmlNativeComponent() {
	}
	public XmlNativeComponent(String tag) {
		setTag(tag);
	}

	/** Returns the tag name, or null if plain text.
	 */
	public String getTag() {
		return _tag;
	}

	//Native//
	public List getDeclaredNamespaces() {
		return _dns != null ? _dns: Collections.EMPTY_LIST;
	}
	public void addDeclaredNamespace(Namespace ns) {
		if (ns == null)
			throw new IllegalArgumentException();

		if (_dns == null)
			_dns = new LinkedList();
		_dns.add(ns);
	}
	public String getPrologContent() {
		return _prolog;
	}
	public void setPrologContent(String prolog) {
		_prolog = prolog != null ? prolog: "";
	}
	public String getEpilogContent() {
		return _epilog;
	}
	public void setEpilogContent(String epilog) {
		_epilog = epilog != null ? epilog: "";
	}

	public Helper getHelper() {
		return _helper;
	}

	public void setId(String id) {
		super.setId(id);
		setDynamicProperty("id", id);
	}
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Use client-dependent attribute, such as display:none");
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();
			//don't use _helper directly, since the derive might override it

		//first half
		helper.getFirstHalf(sb, _tag, _props, _dns);
		write(out, sb);

		//prolog
		sb.append(_prolog); //no encoding
		write(out, sb);

		//children
		for (Component child = getFirstChild(); child != null;
		child = child.getNextSibling())
			((ComponentCtrl)child).redraw(out);

		//epilog
		sb.append(_epilog);
		write(out, sb);

		//second half
		helper.getSecondHalf(sb, _tag);
		out.write(sb.toString());
	}
	/** Writes the content of stringbuffer to the writer.
	 * After written, stringbuffer is reset.
	 */
	private static void write(java.io.Writer out, StringBuffer sb)
	throws java.io.IOException {
		out.write(sb.toString());
		sb.setLength(0);
	}

	//DynamicTag//
	/** Sets the tag name.
	 *
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 */
	public void setTag(String tag) throws WrongValueException {
		_tag = tag != null && tag.length() > 0 ? tag: null;
	}
	public boolean hasTag(String tag) {
		return true; //accept anything
	}
	public boolean hasDynamicProperty(String name) {
		return ComponentsCtrl.isReservedAttribute(name);
	}
	public Object getDynamicProperty(String name) {
		return _props != null ? _props.get(name): null;
	}
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name required");

		if (value == null) {
			if (_props != null) _props.remove(name);
		} else {
			if (_props == null)
				_props = new LinkedHashMap();
			_props.put(name, value);
		}
	}

	/** The HTML helper.
	 */
	public static class XmlHelper implements Helper {
		public Component newNative(String text) {
			final XmlNativeComponent nc = new XmlNativeComponent();
			if (text != null)
				nc.setPrologContent(text);
			return nc;
		}
		public void getFirstHalf(StringBuffer sb, String tag,
		Map props, Collection namespaces) {
			if (tag != null)
				sb.append('<').append(tag);

			NativeHelpers.getAttributes(sb, props, namespaces);

			if (tag != null)
				sb.append(">");
		}
		public void getSecondHalf(StringBuffer sb, String tag) {
			if (tag != null)
				sb.append("</").append(tag).append(">\n");
		}
		public void appendText(StringBuffer sb, String text) {
			XMLs.encodeText(sb, text);
		}
	}
}
