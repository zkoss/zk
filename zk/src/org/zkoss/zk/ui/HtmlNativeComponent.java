/* HtmlNativeComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 19 18:05:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.Writer;

import org.zkoss.xml.HTMLs;
import org.zkoss.idom.Namespace;

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
 * @since 2.5.0
 */
public class HtmlNativeComponent extends AbstractComponent
implements DynamicTag, Native {
	private static Helper _helper = new HtmlHelper();

	private String _tag;
	private String _prolog = "", _epilog = "";
	private Map _props;
	/** Declared namespaces ({@link Namespace}). */
	private List _dns;

	/** Contructs a {@link HtmlNativeComponent} component.
	 * 
	 */
	public HtmlNativeComponent() {
	}
	/** Constructs a {@link HtmlNativeComponent} component with the specified tag name.
	 *
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 */
	public HtmlNativeComponent(String tag) {
		setTag(tag);
	}

	/** Contructs a {@link HtmlNativeComponent} component with the specified
	 * prolog ad epilog.
	 */
	public HtmlNativeComponent(String tag, String prolog, String epilog) {
		this(tag);

		_prolog = prolog != null ? prolog: "";
		_epilog = epilog != null ? epilog: "";
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

	//-- Component --//
	public void setId(String id) {
		super.setId(id);
		setDynamicProperty("id", id);
	}
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Use client-dependent attribute, such as display:none");
	}

	public void redraw(Writer out) throws java.io.IOException {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();
			//don't use _helper directly, since the derive might override it
		helper.getFirstHalf(sb, _tag, _props, _dns);
		sb.append(_prolog); //no encoding
		out.write(sb.toString());
		sb.setLength(0);

		for (Iterator it = getChildren().iterator(); it.hasNext();)
			((Component)it.next()).redraw(out);

		sb.append(_epilog);
		helper.getSecondHalf(sb, _tag);
		out.write(sb.toString());
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
		return name != null && !"use".equals(name) && !"if".equals(name)
			&& !"unless".equals(name) && !"forEach".equals(name);
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
	public static class HtmlHelper implements Helper {
		public void getFirstHalf(StringBuffer sb, String tag, Map props,
		Collection namespaces) {
			sb.append('<').append(tag);

			NativeHelpers.getAttributes(sb, props, namespaces);

			final String tn = tag.toLowerCase();
			if (HTMLs.isOrphanTag(tn))
				sb.append('/');
			sb.append('>');

			if (!_noLFs.contains(tn) && !_begNoLFs.contains(tn))
				sb.append('\n'); //make it more readable
		}
		public void getSecondHalf(StringBuffer sb, String tag) {
			final String tn = tag.toLowerCase();
			if (HTMLs.isOrphanTag(tn))
				return;

			sb.append("</").append(tag).append('>');

			if (!_noLFs.contains(tn))
				sb.append('\n'); //make it more readable
		}
	}
	/** A set of tags that we shall append linefeed to it.
	 */
	private static final Set _noLFs, _begNoLFs;
	static {
		final String[] noLFs = {
			"a", "abbr", "acronym", "address",
			"b", "basefont", "bdo", "big", "blink",
			"cite", "code",
			"del", "dfn", "dir",
			"em",
			"font",
			"i", "img", "input", "ins", "kbd", "q",
			"s", "samp", "small", "strike", "strong", "style", "sub", "sup",
			"u"
		};
		_noLFs = new HashSet((noLFs.length << 2) / 5);
		for (int j = noLFs.length; --j >= 0;)
			_noLFs.add(noLFs[j]);

		final String[] begNoLFs = {
			"caption", "dd", "div", "dt", "legend", "li",
			"p", "pre",
			"span", "td", "tfoot", "th", "title"
			
		};
		_begNoLFs = new HashSet((begNoLFs.length << 2) / 5);
		for (int j = begNoLFs.length; --j >= 0;)
			_begNoLFs.add(begNoLFs[j]);
	}
}
