/* FileWcsExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  9 14:11:25     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.util.Iterator;
import java.io.StringWriter;
import java.io.Writer;
import java.io.FileInputStream;
import java.io.File;

/**
 * Used by ZK Lighter to generate the CSS content of a WCS file
 * for <a href="http://code.google.com/p/zkuery/">ZKuery</a>.
 * Unlike {@link WcsExtendlet}, it is based on the file system.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class FileWcsExtendlet extends WcsExtendlet {
	/** Parses and return the content of the specified WCS file.
	 */
	public String service(File fl, Includer includer) throws Exception {
		setProvider(new FileProvider(fl, isDebugJS()));
		try {
			final WcsInfo wi = (WcsInfo)parse(new FileInputStream(fl), fl.getPath());
			final StringWriter sw = new StringWriter();
			for (int j = 0; j < wi.items.length; ++j) {
				final Object o = wi.items[j];
				if (o instanceof String) {
					try {
						includer.include((String)o, sw);
					} catch (Throwable ex) {
						log.realCauseBriefly("Unable to load "+wi.items[j], ex);
					}
				} else { //static method
					sw.write(invoke((MethodInfo)o));
				}
				sw.write('\n');
			}
			for (String uri: wi.langdef.getCSSURIs()) {
				try {
					includer.include(uri, sw);
				} catch (Throwable ex) {
					log.realCauseBriefly("Unable to load "+uri, ex);
				}
			}
			return sw.getBuffer().toString();
		} finally {
			setProvider(null);
		}
	}
	/** Work with {@link #service} to inlcude a resource. */
	public interface Includer {
		/** Includes the specified resource.
		 * @param uri the URI to include. The format of uri is <code>~./xxx</code>.
		 * @param out the writer to write the content of the resouce to.
		 */
		public void include(String uri, Writer out)
		throws java.io.IOException;
	}
}
