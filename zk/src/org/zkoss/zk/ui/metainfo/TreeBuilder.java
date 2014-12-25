/** TreeBuilder.java.

	Purpose:
		
	Description:
		
	History:
		4:44:33 PM Sep 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.metainfo;

import java.io.File;
import java.io.Reader;
import java.net.URL;

import org.zkoss.idom.Document;

/**
 * A tree builder for parsing a page to {@link Document} 
 * @author jumperchen
 * @since 8.0.0
 */
public interface TreeBuilder {
	/**
	 * Parse the content of a file to a {@link Document}
	 */
	public Document parse(File file) throws Exception;
	/**
	 * Parse the content from a URL to a {@link Document}
	 */
	public Document parse(URL url) throws Exception;
	/**
	 * Parse the content from a Reader to a {@link Document}
	 */
	public Document parse(Reader reader) throws Exception;
}
