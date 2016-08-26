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

	/** A component attribute used to indicate the max-size of file upload.
	 * It is a non-negative integer.
	 */
	public static final String UPLOAD_MAX_SIZE = "org.zkoss.zk.upload.maxsize";

	//Component//
	/** A desktop attribute used to store a map of data associated
	 * with the echo event ({@link org.zkoss.zk.ui.event.Events#echoEvent}).
	 * @since 5.0.4
	 */
	public static final String ECHO_DATA = "org.zkoss.zk.ui.event.echo.data";
	/**
	 * A component attribute used to store a map of properties ({@link org.zkoss.zk.ui.metainfo.Property})
	 * which have deferred expressions.
	 * @since 8.0.0
	 */
	public static final String DEFERRED_PROPERTIES = "org.zkoss.zk.ui.metainfo.deferred.properties";

	//Session//

	//Execution//
	/** An execution attribute used to store whether the page has been created.
	 * @since 6.5.5
	 */
	public static final String PAGE_CREATED = "org.zkoss.zk.ui.page.created";
}
