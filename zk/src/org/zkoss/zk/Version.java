/* Version.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb  7 18:03:51     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk;

/**
 * The ZK version info.
 * {@link #UID} must be the same as the version specified in config.xml.
 *
 * @author tomyeh
 */
public class Version {
	/** The version UID used to identify the resources.
	 */
	public static final String UID = "3.5.3";
	/** The release version. The official version.
	 * It is the same as {@link org.zkoss.zk.ui.WebApp#getVersion}.
	 * @since 3.0.0
	 */
	public static final String RELEASE = "3.5.3-FL";
}
