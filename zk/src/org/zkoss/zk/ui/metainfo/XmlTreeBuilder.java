/** XulTreeBuilder.java.

	Purpose:
		
	Description:
		
	History:
		4:55:38 PM Sep 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.metainfo;

import java.io.File;
import java.io.Reader;
import java.net.URL;

import org.zkoss.idom.Document;
import org.zkoss.idom.input.SAXBuilder;

/**
 * A tree builder for parsing XML format
 * @author jumperchen
 * @since 8.0.0
 */
public class XmlTreeBuilder implements TreeBuilder {

	public Document parse(File file) throws Exception {
		return new SAXBuilder(true, false, true).build(file);
	}

	public Document parse(URL url) throws Exception  {
		return new SAXBuilder(true, false, true).build(url);
	}
	
	public Document parse(Reader reader) throws Exception  {
		return new SAXBuilder(true, false, true).build(reader);
	}

}
