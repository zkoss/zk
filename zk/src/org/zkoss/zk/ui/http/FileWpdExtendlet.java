/* FileWpdExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  9 14:12:42     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.io.File;
import java.io.FileInputStream;

/**
 * Used by ZK Lighter to generate the JavaScript content of a WPD file.
 * Unlike {@link WpdExtendlet}, it is based on the file system.
 * 
 * @author tomyeh
 */
public class FileWpdExtendlet extends WpdExtendlet {
	/** Parses and return the content of the specified WPD file.
	 */
	public byte[] service(File fl) throws Exception {
		setProvider(new FileProvider(fl, isDebugJS()));
		try {
			final Object rawdata = parse(new FileInputStream(fl), fl.getPath());
			return rawdata instanceof byte[] ? (byte[])rawdata:
				((WpdContent)rawdata).toByteArray(null);
		} finally {
			setProvider(null);
		}
	}
}
