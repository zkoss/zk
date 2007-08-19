/* Attributes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 21:57:02     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

/**
 * Attributes of desktops, pages and components that are used internally.
 *
 * @author tomyeh
 */
public class Attributes {
	/** A desktop attribute to indicate the completeness percentage of 
	 * the current file upload.
	 * It is an integer ranging from 0 to 99.
	 */
	public static final String UPLOAD_PERCENT = "zk_uploadPercent";
	/** A desktop attribute indicated the number of bytes of the current
	 * file upload.
	 * It is a non-negative long.
	 */
	public static final String UPLOAD_SIZE = "zk_uploadSize";

}
