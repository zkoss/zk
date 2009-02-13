/* AjaxDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:13:07     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

/**
 * Represents a Web browser with the Ajax support.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public class AjaxDevice extends GenericDevice {
	/** It supports {@link #RESEND}.
	 */
	public boolean isSupported(int func) {
		return func == RESEND;
	}
	/** Return false to indicate it is not cacheable.
	 */
	public boolean isCacheable() {
		return false;
	}
	/** Returns <code>text/html</code>
	 */
	public String getContentType() {
		return "text/html";
	}
	/** Returns <code>&lt;!DOCTYPE html ...XHTML 1.0 Transitional...&gt;</code>.
	 */
	public String getDocType() {
		return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
			+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	}
}
