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

/**
 * Represents a Web browser with the Ajax support.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public class AjaxDevice extends GenericDevice {
	public String getContentType() {
		return "text/html;charset=UTF-8";
	}
	public String getDocType() {
		return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
			+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

	}
}
