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
 * Attributes used internally for implementation only.
 * @author tomyeh
 */
public class Attributes extends org.zkoss.zk.ui.sys.Attributes {
	//Desktop//
	/** A desktop attribute to indicate the completeness percentage of 
	 * the current file upload.
	 * It is an integer ranging from 0 to 99.
	 */
	public static final String UPLOAD_PERCENT = "org.zkoss.zk.upload.percent";
	/** A desktop attribute to indicate the number of bytes of the current
	 * file upload.
	 * It is a non-negative long.
	 */
	public static final String UPLOAD_SIZE = "org.zkoss.zk.upload.size";

	//Component//
	/** A desktop attribute used to store a map of data associated
	 * with the echo event ({@link org.zkoss.zk.ui.event.Events#echoEvent}).
	 * @since 5.0.4
	 */
	public static final String ECHO_DATA = "org.zkoss.zk.ui.event.echo.data";

	//Session//
}
