/* FileWpdExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  9 14:12:42     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.io.File;
import java.io.FileInputStream;

/**
 * Used by ZK Lighter to generate the JavaScript content of a WPD file
 for <a href="http://code.google.com/p/zkuery/">ZKuery</a>.
 * Unlike {@link WpdExtendlet}, it is based on the file system.
 * 
 * @author tomyeh
 */
public class FileWpdExtendlet extends WpdExtendlet {
	/** Parses and return the content of the specified WPD file.
	 */
	public byte[] service(File fl) throws Exception {
		setProvider(new WpdFileProvider(fl, isDebugJS()));
		try {
			final Object rawdata = parse(new FileInputStream(fl), fl.getPath());
			return rawdata instanceof ByteContent ?
				((ByteContent)rawdata).content:
				((WpdContent)rawdata).toByteArray(null);
		} finally {
			setProvider(null);
		}
	}
	/*package*/ class WpdFileProvider extends FileProvider {
		/*package*/ WpdFileProvider(File file, boolean debugJS) {
			super(file, debugJS);
		}
		protected String getRealPath(String path) {
			if (path.indexOf("/msgzk*.js") >= 0) {
				if ("../../zk/lang/msgzk*.js".equals(path))
					path = "../../../../../../../zk/codegen/archive/web/js/zk/lang/msgzk*.js";
						//dirty solution
				else
					System.out.println("Warning: assumption changed for "+path);
			}
			return super.getRealPath(path);
		}
	}
}
