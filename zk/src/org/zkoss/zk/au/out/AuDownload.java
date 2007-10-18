/* AuDownload.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 16 14:37:20     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to download the specified URI.
 *
 * <p>data[0]: the URL to download the file from.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuDownload extends AuResponse {
	/**
	 * @param url the URI of the file to download, never null.
	 */
	public AuDownload(String url) {
		super("download", new String[] {url});
	}
}
