/* MilComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 31, 2007 4:25:10 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.mil.au.impl.CommandCommand;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * Super class of MIL component.
 * 
 * @author henrichen
 */
abstract public class MilComponent extends AbstractComponent {
	/** Returns whether to send back the request of the specified event
	 * immediately -- i.e., non-deferrable.
	 * Returns true if you want the component (on the server)
	 * to process the event immediately.
	 *
	 * <p>Default: return true if any non-deferable event listener of
	 * the specified event is found. In other words, it returns
	 * {@link Events#isListened} with asap = true.
	 */
	protected boolean isAsapRequired(String evtnm) {
		return Events.isListened(this, evtnm, true);
	}
	/** Appends the HTML attribute for the specified event name, say, onChange.
	 * It is called by derived's {@link #getOuterAttrs}.
	 *
	 * @param sb the string buffer to hold the HTML attribute. If null and
	 * {@link #isAsapRequired} is true, a string buffer is created and returned.
	 * @return the string buffer. If sb is null and {@link #isAsapRequired}
	 * returns false, null is returned.
	 * If the caller passed non-null sb, the returned value must be the same
	 * as sb (so it usually ignores the returned value).
	 */
	protected StringBuffer appendAsapAttr(StringBuffer sb, String evtnm) {
		if (isAsapRequired(evtnm)) {
			if (sb == null) sb = new StringBuffer(80);
			HTMLs.appendAttribute(sb, getAttrOfEvent(evtnm), "t");
		}
		return sb;
	}

	private static String getAttrOfEvent(String evtnm) {
		return "onBack".equals(evtnm) ? "bc" :
			"onCancel".equals(evtnm) ? "cc" :
			"onExit".equals(evtnm) ? "ec" :
			"onHelp".equals(evtnm) ? "hc" :
			"onItem".equals(evtnm) ? "in" :
			"onOK".equals(evtnm) ? "oc" :
			"onScreen".equals(evtnm) ? "sc" :
			"onStop".equals(evtnm) ? "sn" : 
			"onChange".equals(evtnm) ? "on" :
			"onChanging".equals(evtnm) ? "og" :
			"onSelect".equals(evtnm) ? "os" :
			"onSlide".equals(evtnm) ? "ol" : "";
	}

	/**
	 * Encode text to make it complies to XML standard.
	 * @param str the input string
	 * @return the output string that complies to XML standard.
	 */
	protected String encodeString(String str) {
		if (str == null) return null;
		final int len = str.length();
		final StringBuffer sb = new StringBuffer(len+100);
		for(int j=0; j < len; ++j) {
			final char c = str.charAt(j);
			switch(c) {
			case '"':
				sb.append("&#34;");
				break;
			case '&':
				sb.append("&#38;");
				break;
			case '<':
				sb.append("&#60;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	//-- Component --//
	public boolean addEventListener(String evtnm, EventListener listener) {
		final boolean asap = isAsapRequired(evtnm);
		final boolean ret = super.addEventListener(evtnm, listener);
		if (ret && !asap && isAsapRequired(evtnm))
			smartUpdate(getAttrOfEvent(evtnm), "t");
		return ret;
	}

	public boolean removeEventListener(String evtnm, EventListener listener) {
		final boolean asap = isAsapRequired(evtnm);
		final boolean ret = super.removeEventListener(evtnm, listener);
		if (ret && asap && !isAsapRequired(evtnm))
			smartUpdate(getAttrOfEvent(evtnm), null);
		return ret;
	}

	public String getOuterAttrs() {
		return "";
	}

	//register the MIL related event
	static {	
		new CommandCommand("onCommand", org.zkoss.zk.au.Command.IGNORE_OLD_EQUIV);
	}
}
