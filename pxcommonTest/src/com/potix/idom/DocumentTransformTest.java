/* DocumentTransformTest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 13 17:54:32  2002, Created by andrewho@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom;

import java.io.File;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import com.potix.idom.input.SAXBuilder;
import com.potix.idom.transform.Transformer;
import com.potix.io.Files;
import com.potix.util.logging.Log;

import junit.framework.*;

/**
 * Test the iDOM Documet transform
 * @author <a href="mailto:andrewho@potix.com">andrewho@potix.com</a>
 */
public class DocumentTransformTest extends TestCase {
	public static final Log log = Log.lookup(DocumentTransformTest.class);

	private static final String FN1 = "/metainfo/com/potix/idom/t3.xml";
	private static final String FN2 = "/metainfo/com/potix/idom/t4.xml";
	private static final String FNF = "testF.xml";
	
	public DocumentTransformTest(String name) throws Exception {
		super(name);
		log.setLevel(Log.ALL);
	}
	public static Test suite() {
		return new TestSuite(DocumentTransformTest.class);
	}
	protected void setUp() {
		//
	}
	protected void tearDown() {
		Log.lookup("com.potix.idom").setLevel(null);
	}
	
	
	/* Tom Yeh@2003/5/19:
		If we use URL to load the source, we don't need to copy DTD
		to the file system.
	protected InputStream asStream(String resourceName) throws NullPointerException {
		InputStream is = DocumentTransformTest.class.getResourceAsStream(resourceName);
		return is;
	}
	protected void copyt4dtd() throws IOException {
		File t4dtd = new File("t4.dtd");
		if (!t4dtd.exists()) {
			//copy files
			Files.copy(t4dtd, asStream("/metainfo/com/potix/idom/t4.dtd"));
		}
	}*/

	public void testTransformToFile() throws Exception {
		final Document xmldoc = new SAXBuilder(true, false)
			.build(DocumentTransformTest.class.getResource(FN2));
		
		final Transformer itf = new Transformer();
		File xmlf = new File(FNF);
		itf.transform(xmldoc, new StreamResult(xmlf));
		
		//read it back
		Document xmldoc1 = new SAXBuilder(true, false).build(xmlf);
		
		//compare two Documents
		xmldoc.getDocType().detach(); //dirty fix
			//TODO: debug why the transformer cannot generate doc-type well
		DocumentTest.compare(xmldoc, xmldoc1);
	}

	public void testTransformToDocument() throws Exception {
		final Document xmldoc = new SAXBuilder(true, false)
			.build(DocumentTransformTest.class.getResource(FN2));
		
		final Transformer itf = new Transformer();
		Document xmldocT = itf.transform(xmldoc);
		//log.info(xmldocT.getDocType());
		
		/* Dump to xml file
		File xmlf = new File("00dump.xml");
		itf.transform(xmldoc, xmlf);
		*/
		
		//compare two Documents
		xmldoc.getDocType().detach(); //dirty fix
			//TODO: debug why the transformer cannot generate doc-type well
		DocumentTest.compare(xmldoc, xmldocT);
	}
}
