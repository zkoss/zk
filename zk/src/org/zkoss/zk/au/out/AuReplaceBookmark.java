/* AuReplaceBookmark.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 27 12:31:09 TST 2010, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to replace the bookmark to the desktop.
 *
 * <p>data[0]: the name of the replaceBookmark.
 * 
 * @author jumperchen
 * @since 3.6.4
 */
public class AuReplaceBookmark extends AuResponse {
	/**
	 * @param name the replaceBookmark name.
	 */
	public AuReplaceBookmark(String name) {
		super("replaceBookmark", name); //component-independent
	}
}
