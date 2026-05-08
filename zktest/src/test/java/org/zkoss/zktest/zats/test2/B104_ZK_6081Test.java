/* B104_ZK_6081Test.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 17:42:06 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

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
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6081: fileupload missing from anyGroup in zul.xsd.
 *
 * Verifies, by inspecting zul.xsd directly, that the {@code fileupload}
 * element is referenced from both the {@code anyGroup} and
 * {@code anyGroupSingle} choice groups, so ZUL such as
 * {@code <div><fileupload/></div>} is permitted by the schema. Also
 * confirms the page renders the fileupload widget at runtime.
 */
public class B104_ZK_6081Test extends WebDriverTestCase {

	private static final String XSD_RESOURCE = "metainfo/xml/zul.xsd";

	@Test
	public void anyGroupContainsFileupload() throws Exception {
		assertGroupReferencesElement("anyGroup", "fileupload");
	}

	@Test
	public void anyGroupSingleContainsFileupload() throws Exception {
		assertGroupReferencesElement("anyGroupSingle", "fileupload");
	}

	@Test
	public void fileuploadElementIsDefined() throws Exception {
		Document xsd = loadXsd();
		String count = (String) xpath().evaluate(
				"count(/*[local-name()='schema']"
						+ "/*[local-name()='element' and @name='fileupload'])",
				xsd, XPathConstants.STRING);
		assertTrue(Integer.parseInt(count) >= 1,
				"zul.xsd must define <xs:element name='fileupload' .../>");
	}

	@Test
	public void renderFileuploadPage() {
		connect();
		waitResponse();
		assertTrue(jq(".z-fileupload, .z-upload").exists(),
				"fileupload should render inside <div> within <hbox> within <window>");
	}

	private static void assertGroupReferencesElement(String groupName, String elementName)
			throws Exception {
		Document xsd = loadXsd();
		String expr = "count(/*[local-name()='schema']"
				+ "/*[local-name()='group' and @name='" + groupName + "']"
				+ "//*[local-name()='element' and @ref='" + elementName + "'])";
		String countStr = (String) xpath().evaluate(expr, xsd, XPathConstants.STRING);
		int count = Integer.parseInt(countStr);
		assertTrue(count >= 1,
				"zul.xsd group '" + groupName + "' must reference <xs:element ref='"
						+ elementName + "' />, but no such ref was found "
						+ "(see ZK-6081). count=" + count);
	}

	private static Document loadXsd() throws Exception {
		try (InputStream xsd = B104_ZK_6081Test.class.getClassLoader()
				.getResourceAsStream(XSD_RESOURCE)) {
			assertNotNull(xsd, "zul.xsd must be on the classpath at " + XSD_RESOURCE);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(xsd);
		}
	}

	private static XPath xpath() {
		return XPathFactory.newInstance().newXPath();
	}
}
