/* Version.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb  7 18:03:51     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk;

/**
 * Specified this in lang.xml, such that ZK knows what lang.xml is associated
 * with zk.jar.
 *
 * @author tomyeh
 */
public class Version {
	/** The version UID used to identify the resources.
	 */
	public static final String UID = "2.5.0";
	/** The release version. The official version.
	 * It is the same as {@link org.zkoss.zk.ui.WebApp#getVersion}.
	 * @since 2.5.0
	 */
	public static final String RELEASE = "2.5.0-FL";
}
