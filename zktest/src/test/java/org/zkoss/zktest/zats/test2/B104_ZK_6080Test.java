/* B104_ZK_6080Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 17:37:42 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_6080Test extends WebDriverTestCase {

	private static final String ZUL_XSD_RESOURCE = "metainfo/xml/zul.xsd";

	@Test
	public void rowsTypeAllowsGroupfootChild() throws Exception {
		// Per ZK-6080, groupfoot must be in the rowsType allowed-child list
		// alongside row and group.
		Document doc = loadXsd();

		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new javax.xml.namespace.NamespaceContext() {
			public String getNamespaceURI(String prefix) {
				return "xs".equals(prefix) ? "http://www.w3.org/2001/XMLSchema" : null;
			}
			public String getPrefix(String uri) { return null; }
			public java.util.Iterator<String> getPrefixes(String uri) { return null; }
		});
		NodeList refs = (NodeList) xpath.evaluate(
				"//xs:complexType[@name='rowsType']/xs:choice/xs:element/@ref",
				doc, XPathConstants.NODESET);
		assertNotNull(refs, "rowsType/xs:choice must exist");

		boolean hasRow = false, hasGroup = false, hasGroupfoot = false;
		for (int i = 0; i < refs.getLength(); i++) {
			String name = refs.item(i).getNodeValue();
			if ("row".equals(name)) hasRow = true;
			else if ("group".equals(name)) hasGroup = true;
			else if ("groupfoot".equals(name)) hasGroupfoot = true;
		}
		assertTrue(hasRow, "rowsType must allow <row> (sanity)");
		assertTrue(hasGroup, "rowsType must allow <group> (sanity)");
		assertTrue(hasGroupfoot,
				"ZK-6080: rowsType must list <xs:element ref=\"groupfoot\" /> as a valid child");
	}

	@Test
	public void groupfootRendersInsideRowsAtRuntime() {
		connect();
		waitResponse();
		assertTrue(jq("$grid").exists(), "grid should render");
		assertTrue(jq("$gf1").exists(), "groupfoot should render inside rows");
		assertEquals("Fruit subtotal: $1.50", jq("$footLabel").text(),
				"groupfoot child label should be rendered");
	}

	private static Document loadXsd() throws Exception {
		// zul.xsd is shipped on the test classpath via the zul module's
		// resources/metainfo. Load through the classloader so the test
		// works regardless of cwd / packaging.
		try (InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(ZUL_XSD_RESOURCE)) {
			assertNotNull(in, "zul.xsd must be reachable on the test classpath at "
					+ ZUL_XSD_RESOURCE);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			return builder.parse(in);
		}
	}
}
