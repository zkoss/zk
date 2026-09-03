/* B110_ZK_6081Test.java

        Purpose:

        Description:

        History:
                Fri May 08 17:42:06 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * ZK-6081: fileupload missing from anyGroup in zul.xsd.
 *
 * <p>Together with fileupload, the elements below were declared in zul.xsd but
 * referenced by no content model at all, so ZUL such as
 * {@code <div><fileupload/></div>} was rejected by the schema. They are
 * verified here both structurally (the anyGroup / anyGroupSingle choice groups
 * reference them) and end to end (a ZUL using all of them validates).
 */
public class B110_ZK_6081Test {

	private static final String XSD_RESOURCE = "metainfo/xml/zul.xsd";

	/** Elements promoted into anyGroup / anyGroupSingle by ZK-6081. */
	private static final String[] PROMOTED = { "fileupload", "cropper", "goldenlayout", "jasperreport",
			"rating" };

	@Test
	public void anyGroupContainsPromotedElements() throws Exception {
		for (String element : PROMOTED)
			assertGroupReferencesElement("anyGroup", element);
	}

	@Test
	public void anyGroupSingleContainsPromotedElements() throws Exception {
		for (String element : PROMOTED)
			assertGroupReferencesElement("anyGroupSingle", element);
	}

	/**
	 * The promoted elements become IDE autocomplete candidates everywhere, so
	 * their attribute tables must not advertise attributes that have no setter.
	 */
	@Test
	public void promotedTypesDropAttributesWithoutSetter() throws Exception {
		Document xsd = loadXsd();
		assertAttribute(xsd, "cropperType", "minWidth", "minWidths");
		assertAttribute(xsd, "cropperType", "minHeight", "minHeights");
		assertAttribute(xsd, "cropperType", "maxWidth", "maxWidths");
		assertAttribute(xsd, "cropperType", "maxHeight", "maxHeights");
		assertAttribute(xsd, "jasperreportType", "src", "source");
		assertAttribute(xsd, "ratingType", "iconSclass", "symbol");
	}

	@Test
	public void promotedElementsValidateInsideLayoutContainers() throws Exception {
		String zul = "<zk xmlns=\"http://www.zkoss.org/2005/zul\">"
				+ "<window><hbox><div>"
				+ "<fileupload label=\"Upload\"/>"
				+ "<cropper w=\"100\" h=\"100\" minWidth=\"10\" maxWidth=\"300\"/>"
				+ "<goldenlayout/>"
				+ "<jasperreport src=\"/report.jasper\"/>"
				+ "<rating max=\"5\" iconSclass=\"z-icon-star\"/>"
				+ "</div></hbox></window></zk>";
		assertEquals(List.of(), validate(zul),
				"ZUL using the ZK-6081 elements inside layout containers must satisfy zul.xsd");
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

	private static void assertAttribute(Document xsd, String typeName, String expected, String removed)
			throws Exception {
		assertEquals(1, countAttribute(xsd, typeName, expected),
				"zul.xsd '" + typeName + "' must declare the attribute backed by a setter: " + expected);
		assertEquals(0, countAttribute(xsd, typeName, removed),
				"zul.xsd '" + typeName + "' must not declare '" + removed + "': it has no setter, so "
						+ "following IDE autocomplete ends in PropertyNotFoundException");
	}

	private static int countAttribute(Document xsd, String typeName, String attrName) throws Exception {
		String expr = "count(/*[local-name()='schema']"
				+ "/*[local-name()='complexType' and @name='" + typeName + "']"
				+ "/*[local-name()='attribute' and @name='" + attrName + "'])";
		return Integer.parseInt((String) xpath().evaluate(expr, xsd, XPathConstants.STRING));
	}

	/**
	 * Schema-level errors are collected instead of thrown, so an unrelated
	 * zul.xsd regression fails B110_ZK_6060Test rather than this one.
	 */
	private static List<String> validate(String zul) throws Exception {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setErrorHandler(collectInto(new ArrayList<>()));
		Schema schema;
		try (InputStream xsd = openXsd()) {
			schema = factory.newSchema(new StreamSource(xsd));
		}
		List<String> errors = new ArrayList<>();
		Validator validator = schema.newValidator();
		validator.setErrorHandler(collectInto(errors));
		validator.validate(new StreamSource(
				new ByteArrayInputStream(zul.getBytes(StandardCharsets.UTF_8))));
		return errors;
	}

	private static ErrorHandler collectInto(List<String> sink) {
		return new ErrorHandler() {
			public void warning(SAXParseException e) {
			}

			public void error(SAXParseException e) {
				sink.add(e.getMessage());
			}

			public void fatalError(SAXParseException e) {
				sink.add(e.getMessage());
			}
		};
	}

	private static Document loadXsd() throws Exception {
		try (InputStream xsd = openXsd()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(xsd);
		}
	}

	private static InputStream openXsd() {
		InputStream xsd = B110_ZK_6081Test.class.getClassLoader().getResourceAsStream(XSD_RESOURCE);
		assertNotNull(xsd, "zul.xsd must be on the classpath at " + XSD_RESOURCE);
		return xsd;
	}

	private static XPath xpath() {
		return XPathFactory.newInstance().newXPath();
	}
}
