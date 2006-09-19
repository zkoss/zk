/* Attributes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 21:57:02     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

/**
 * Attributes used internally.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Attributes {
	/** An integer (0~99) indicating the completeness percentage of 
	 * the current file upload.
	 */
	public static final String UPLOAD_PERCENT = "zk_uploadPercent";
	/** A long indicating the number of bytes of the current file upload.
	 */
	public static final String UPLOAD_SIZE = "zk_uploadSize";
}
