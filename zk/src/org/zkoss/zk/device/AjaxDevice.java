/* AjaxDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:13:07     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;

/**
 * Represents a Web browser with the Ajax support.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public class AjaxDevice implements Device, java.io.Serializable {
	private String _type;
	private String _uamsg;

	//Device//
	public String getType() {
		return _type;
	}
	public String getUnavailableMessage() {
		return _uamsg;
	}
	public void setUnavailableMessage(String unavailmsg) {
		_uamsg = unavailmsg;
	}

	public void init(String type, Desktop desktop, String unavailmsg) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type");
		_type = type;
		_uamsg = unavailmsg;
	}
	public void sessionWillPassivate(Desktop desktop) {
	}
	public void sessionDidActivate(Desktop desktop) {
	}

	public String getRawTagBegin(String tagname, Map props) {
		if (tagname == null)
			return "";

		final StringBuffer sb = new StringBuffer(80)
			.append('<').append(tagname);

		if (props != null)
			for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				HTMLs.appendAttribute(sb,
					Objects.toString(me.getKey()),
					Objects.toString(me.getValue()));
			}

		final String tn = tagname.toLowerCase();
		if (HTMLs.isOrphanTag(tn))
			sb.append('/');
		sb.append('>');
		if (_beglftags.contains(tn))
			sb.append('\n'); //make it more readable
		return sb.toString();
	}
	public String getRawTagEnd(String tagname) {
		if (tagname == null)
			return "";

		final String tn = tagname.toLowerCase();
		if (HTMLs.isOrphanTag(tn))
			return "br".equals(tn) ? "\n": "";

		final StringBuffer sb = new StringBuffer()
			.append("</").append(tagname).append('>');

		if (_endlftags.contains(tn))
			sb.append('\n'); //make it more readable

		return sb.toString();
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
