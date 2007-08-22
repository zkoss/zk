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

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.ext.Native;

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
	private String _tag;
	private String _prolog = "", _epilog = "";
	private Map _props;
	private Helper _helper = new HtmlHelper();

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

	public void redraw(Writer out) throws IOException {
		final StringBuffer sb = new StringBuffer(128);
		_helper.getFirstHalf(sb, _tag, _props);
		sb.append(_prolog); //no encoding
		out.write(sb.toString());
		sb.setLength(0);

		for (Iterator it = getChildren().iterator(); it.hasNext();)
			((Component)it.next()).redraw(out);

		sb.append(_epilog);
		_helper.getSecondHalf(sb, _tag);
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
		public void getFirstHalf(StringBuffer sb, String tag, Map props) {
			sb.append('<').append(tag);

			if (props != null)
				for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					HTMLs.appendAttribute(sb,
						Objects.toString(me.getKey()),
						Objects.toString(me.getValue()));
				}

			final String tn = tag.toLowerCase();
			if (HTMLs.isOrphanTag(tn))
				sb.append('/');
			sb.append('>');

			if (_beglftags.contains(tn))
				sb.append('\n'); //make it more readable
		}
		public void getSecondHalf(StringBuffer sb, String tag) {
			final String tn = tag.toLowerCase();
			if (HTMLs.isOrphanTag(tn)) {
				if ("br".equals(tn))
					sb.append('\n');
				return;
			}

			sb.append("</").append(tag).append('>');

			if (_endlftags.contains(tn))
				sb.append('\n'); //make it more readable
		}
	}
	/** A set of tags that we shall append linefeed to it.
	 */
	private static final Set
		_endlftags = new HashSet(), _beglftags = new HashSet();
	static {
		final String[] beglftags = {
			"table", "tr", "tbody", "ul", "ol"
		};

		for (int j = beglftags.length; --j >= 0;)
			_beglftags.add(beglftags[j]);

		final String[] endlftags = {
			"tr", "td", "th", "p", "li", "dt", "dd"
		};
		for (int j = endlftags.length; --j >= 0;)
			_endlftags.add(endlftags[j]);
	}
}
