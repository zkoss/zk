/** ClientBinderCommandParser.java.

	Purpose:
		
	Description:
		
	History:
		12:15:09 PM Jan 7, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import org.zkoss.idom.Attribute;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.NamespaceParser;
import org.zkoss.zk.ui.metainfo.NativeInfo;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * A client binder command parser for <tt>@command</tt> and <tt>@global-command</tt>
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class ClientBinderCommandParser implements NamespaceParser {
	private static String SCRIPT = "zkbind.Binder.%1s(this,%2s)";

	public boolean isMatched(String nsURI) {
		return isNative(nsURI) || isClient(nsURI) || "".equals(nsURI); // if native component without namespace 
	}

	private boolean isClient(String nsURI) {
		return "client".equals(nsURI) || LanguageDefinition.CLIENT_NAMESPACE.equals(nsURI);
	}

	private boolean isNative(String nsURI) {
		return "native".equals(nsURI) || LanguageDefinition.NATIVE_NAMESPACE.equals(nsURI);
	}

	public boolean parse(Attribute attr, ComponentInfo compInfo, PageDefinition pgdef) throws Exception {
		if (!(compInfo instanceof NativeInfo) && "".equals(attr.getNamespaceURI()))
			return false; // not what we care.

		final String name = attr.getLocalName();
		final String value = attr.getValue();
		if (Events.isValid(name)) {
			if (value.startsWith("@command(")) {
				final String subValue = value.substring(9, value.lastIndexOf(')'));
				final String[] strings = subValue.split(",");
				addAttribute(compInfo, attr, name, String.format(SCRIPT, "postCommand", Strings.join(strings)));
				return true;
			} else if (value.startsWith("@global-command(")) {
				final String subValue = value.substring(16, value.lastIndexOf(')'));
				final String[] strings = subValue.split(",");
				addAttribute(compInfo, attr, name, String.format(SCRIPT, "postGlobalCommand", Strings.join(strings)));
				return true;
			}
		}
		return false;
	}

	private void addAttribute(ComponentInfo compInfo, Attribute attr, String name, String value) {
		if (compInfo instanceof NativeInfo || isNative(attr.getNamespaceURI())) {
			compInfo.addProperty(name, value, null);
		} else {
			compInfo.addWidgetListener(name, value, null);
		}
	}

	public int getPriority() {
		return 10000;
	}

}
