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

import java.util.Collection;
import java.util.Map;

import org.zkoss.xml.XMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
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
public class XmlNativeComponent extends HtmlNativeComponent {
	private static final Helper _helper = new XmlHelper();

	//super//
	public Helper getHelper() {
		return _helper;
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
