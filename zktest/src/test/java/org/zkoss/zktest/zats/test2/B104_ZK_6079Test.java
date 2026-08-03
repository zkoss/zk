/* B104_ZK_6079Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 01 10:44:33 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class B104_ZK_6079Test {

	private static final String NS = "http://www.zkoss.org/2005/zul";
	private static Schema _schema;

	/**
	 * ErrorHandler that collects schema-level warnings/errors during schema compilation
	 * but does NOT throw, allowing the schema to be created despite harmless issues
	 * like duplicate attribute declarations (e.g. onOK in inputElementAttrGroup).
	 */
	private static class LenientSchemaErrorHandler implements ErrorHandler {
		@Override
		public void warning(SAXParseException e) { /* ignore */ }
		@Override
		public void error(SAXParseException e) { /* ignore schema-level errors */ }
		@Override
		public void fatalError(SAXParseException e) throws SAXException { throw e; }
	}

	/**
	 * ErrorHandler that collects validation errors when validating a document.
	 * Throws on the first error to give clear test failure messages.
	 */
	private static class StrictValidationErrorHandler implements ErrorHandler {
		final List<SAXParseException> errors = new ArrayList<>();
		@Override
		public void warning(SAXParseException e) { /* ignore */ }
		@Override
		public void error(SAXParseException e) throws SAXException { throw e; }
		@Override
		public void fatalError(SAXParseException e) throws SAXException { throw e; }
	}

	@BeforeAll
	static void initSchema() throws Exception {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Use lenient handler during schema compilation to tolerate
		// pre-existing XSD issues (e.g. duplicate onOK attribute)
		factory.setErrorHandler(new LenientSchemaErrorHandler());
		try (InputStream is = B104_ZK_6079Test.class.getResourceAsStream("/metainfo/xml/zul.xsd")) {
			_schema = factory.newSchema(new StreamSource(is));
		}
	}

	private static void validate(String zulContent) throws Exception {
		Validator validator = _schema.newValidator();
		// Use strict handler during document validation
		validator.setErrorHandler(new StrictValidationErrorHandler());
		validator.validate(new StreamSource(new StringReader(zulContent)));
	}

	private static String zul(String body) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<zk xmlns=\"" + NS + "\">\n"
				+ body + "\n"
				+ "</zk>\n";
	}

	// ========================================================================
	// anyGroup containers — should accept MULTIPLE children
	// ========================================================================

	@Nested
	class ToolbarTests {
		@Test
		public void toolbarWithMultipleToolbarbuttons() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar>\n"
					+ "  <toolbarbutton label=\"A\"/>\n"
					+ "  <toolbarbutton label=\"B\"/>\n"
					+ "  <toolbarbutton label=\"C\"/>\n"
					+ "</toolbar>"
			)), "toolbar should allow multiple toolbarbutton children");
		}

		@Test
		public void toolbarWithMixedChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar>\n"
					+ "  <toolbarbutton label=\"Save\"/>\n"
					+ "  <separator/>\n"
					+ "  <toolbarbutton label=\"Cancel\"/>\n"
					+ "  <space/>\n"
					+ "  <label value=\"Status\"/>\n"
					+ "</toolbar>"
			)), "toolbar should allow mixed child types");
		}

		@Test
		public void toolbarEmpty() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar/>"
			)), "toolbar should allow zero children");
		}

		@Test
		public void toolbarWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar>\n"
					+ "  <toolbarbutton label=\"Only\"/>\n"
					+ "</toolbar>"
			)), "toolbar should allow single child");
		}

		@Test
		public void toolbarWithBaseGroupChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar>\n"
					+ "  <custom-attributes myAttr=\"val\"/>\n"
					+ "  <toolbarbutton label=\"A\"/>\n"
					+ "  <toolbarbutton label=\"B\"/>\n"
					+ "</toolbar>"
			)), "toolbar should allow baseGroup + component children");
		}
	}

	// ========================================================================
	// Other anyGroup containers — should accept MULTIPLE children
	// ========================================================================

	@Nested
	class AnyGroupContainerTests {
		@Test
		public void divWithMultipleChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<div>\n"
					+ "  <label value=\"A\"/>\n"
					+ "  <label value=\"B\"/>\n"
					+ "  <button label=\"C\"/>\n"
					+ "</div>"
			)), "div should allow multiple children");
		}

		@Test
		public void windowWithMultipleChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<window>\n"
					+ "  <label value=\"Title\"/>\n"
					+ "  <textbox/>\n"
					+ "  <button label=\"OK\"/>\n"
					+ "</window>"
			)), "window should allow multiple children");
		}

		@Test
		public void hlayoutWithMultipleChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<hlayout>\n"
					+ "  <button label=\"A\"/>\n"
					+ "  <button label=\"B\"/>\n"
					+ "  <button label=\"C\"/>\n"
					+ "</hlayout>"
			)), "hlayout should allow multiple children");
		}

		@Test
		public void vlayoutWithMultipleChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<vlayout>\n"
					+ "  <label value=\"Row1\"/>\n"
					+ "  <label value=\"Row2\"/>\n"
					+ "</vlayout>"
			)), "vlayout should allow multiple children");
		}

		@Test
		public void groupboxWithMultipleChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<groupbox>\n"
					+ "  <caption label=\"Info\"/>\n"
					+ "  <label value=\"A\"/>\n"
					+ "  <label value=\"B\"/>\n"
					+ "</groupbox>"
			)), "groupbox should allow multiple children");
		}

		@Test
		public void spanWithMultipleChildren() {
			assertDoesNotThrow(() -> validate(zul(
					"<span>\n"
					+ "  <label value=\"A\"/>\n"
					+ "  <label value=\"B\"/>\n"
					+ "</span>"
			)), "span should allow multiple children");
		}
	}

	// ========================================================================
	// anyGroupSingle containers — should accept AT MOST ONE main component
	// ========================================================================

	@Nested
	class AnyGroupSingleTests {

		// --- borderlayout center (centerType) ---

		@Test
		public void centerWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <center>\n"
					+ "    <div>content</div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "center should allow one main child");
		}

		@Test
		public void centerWithTwoChildrenShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<borderlayout>\n"
					+ "  <center>\n"
					+ "    <div>first</div>\n"
					+ "    <div>second</div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "center should reject two main children (anyGroupSingle maxOccurs=1)");
		}

		@Test
		public void centerEmpty() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <center/>\n"
					+ "</borderlayout>"
			)), "center should allow zero children");
		}

		@Test
		public void centerWithCaptionAndOneChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <center>\n"
					+ "    <caption label=\"Title\"/>\n"
					+ "    <div>content</div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "center should allow caption + one main child");
		}

		@Test
		public void centerWithBaseGroupAndOneChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <center>\n"
					+ "    <custom-attributes myAttr=\"val\"/>\n"
					+ "    <div>content</div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "center should allow baseGroup + one main child");
		}

		@Test
		public void centerWithMultipleBaseGroupAndOneChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <center>\n"
					+ "    <custom-attributes a=\"1\"/>\n"
					+ "    <custom-attributes b=\"2\"/>\n"
					+ "    <div>content</div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "center should allow multiple baseGroup + one main child");
		}

		// --- borderlayout east/west/north/south (layoutRegionType) ---

		@Test
		public void eastWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <east>\n"
					+ "    <label value=\"sidebar\"/>\n"
					+ "  </east>\n"
					+ "</borderlayout>"
			)), "east should allow one main child");
		}

		@Test
		public void eastWithTwoChildrenShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<borderlayout>\n"
					+ "  <east>\n"
					+ "    <label value=\"A\"/>\n"
					+ "    <label value=\"B\"/>\n"
					+ "  </east>\n"
					+ "</borderlayout>"
			)), "east should reject two main children (layoutRegionType anyGroupSingle maxOccurs=1)");
		}

		@Test
		public void westWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <west>\n"
					+ "    <tree/>\n"
					+ "  </west>\n"
					+ "</borderlayout>"
			)), "west should allow one main child");
		}

		@Test
		public void westWithTwoChildrenShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<borderlayout>\n"
					+ "  <west>\n"
					+ "    <tree/>\n"
					+ "    <listbox/>\n"
					+ "  </west>\n"
					+ "</borderlayout>"
			)), "west should reject two main children");
		}

		@Test
		public void northWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <north>\n"
					+ "    <toolbar>\n"
					+ "      <toolbarbutton label=\"X\"/>\n"
					+ "    </toolbar>\n"
					+ "  </north>\n"
					+ "</borderlayout>"
			)), "north should allow one main child");
		}

		@Test
		public void northWithTwoChildrenShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<borderlayout>\n"
					+ "  <north>\n"
					+ "    <toolbar/>\n"
					+ "    <menubar/>\n"
					+ "  </north>\n"
					+ "</borderlayout>"
			)), "north should reject two main children");
		}

		@Test
		public void southWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <south>\n"
					+ "    <label value=\"footer\"/>\n"
					+ "  </south>\n"
					+ "</borderlayout>"
			)), "south should allow one main child");
		}

		@Test
		public void southWithTwoChildrenShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<borderlayout>\n"
					+ "  <south>\n"
					+ "    <label value=\"A\"/>\n"
					+ "    <label value=\"B\"/>\n"
					+ "  </south>\n"
					+ "</borderlayout>"
			)), "south should reject two main children");
		}

		// --- panelchildren (panelchildrenType) ---

		@Test
		public void panelchildrenWithSingleChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<panel>\n"
					+ "  <panelchildren>\n"
					+ "    <div>content</div>\n"
					+ "  </panelchildren>\n"
					+ "</panel>"
			)), "panelchildren should allow one main child");
		}

		@Test
		public void panelchildrenWithTwoChildrenShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<panel>\n"
					+ "  <panelchildren>\n"
					+ "    <div>first</div>\n"
					+ "    <div>second</div>\n"
					+ "  </panelchildren>\n"
					+ "</panel>"
			)), "panelchildren should reject two main children (anyGroupSingle maxOccurs=1)");
		}

		@Test
		public void panelchildrenEmpty() {
			assertDoesNotThrow(() -> validate(zul(
					"<panel>\n"
					+ "  <panelchildren/>\n"
					+ "</panel>"
			)), "panelchildren should allow zero children");
		}

		@Test
		public void panelchildrenWithBaseGroupAndOneChild() {
			assertDoesNotThrow(() -> validate(zul(
					"<panel>\n"
					+ "  <panelchildren>\n"
					+ "    <custom-attributes foo=\"bar\"/>\n"
					+ "    <div>content</div>\n"
					+ "  </panelchildren>\n"
					+ "</panel>"
			)), "panelchildren should allow baseGroup + one main child");
		}
	}

	// ========================================================================
	// baseGroup — structural/meta elements should be allowed everywhere
	// ========================================================================

	@Nested
	class BaseGroupTests {
		@Test
		public void customAttributesInToolbar() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar>\n"
					+ "  <custom-attributes a=\"1\"/>\n"
					+ "  <custom-attributes b=\"2\"/>\n"
					+ "</toolbar>"
			)), "baseGroup elements should be allowed in toolbar");
		}

		@Test
		public void templateInDiv() {
			assertDoesNotThrow(() -> validate(zul(
					"<div>\n"
					+ "  <template name=\"model\">\n"
					+ "    <label/>\n"
					+ "  </template>\n"
					+ "  <label value=\"content\"/>\n"
					+ "</div>"
			)), "baseGroup elements should be allowed in div");
		}

		@Test
		public void variablesInWindow() {
			assertDoesNotThrow(() -> validate(zul(
					"<window>\n"
					+ "  <variables myVar=\"hello\"/>\n"
					+ "  <label value=\"content\"/>\n"
					+ "</window>"
			)), "baseGroup elements should be allowed in window");
		}

		@Test
		public void multipleBaseGroupInCenter() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <center>\n"
					+ "    <custom-attributes a=\"1\"/>\n"
					+ "    <custom-attributes b=\"2\"/>\n"
					+ "    <custom-attributes c=\"3\"/>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "anyGroupSingle containers should still allow unlimited baseGroup");
		}
	}

	// ========================================================================
	// Edge cases & regression guards
	// ========================================================================

	@Nested
	class EdgeCaseTests {

		@Test
		public void toolbarInsideBorderlayoutNorthWithMultipleButtons() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <north>\n"
					+ "    <toolbar>\n"
					+ "      <toolbarbutton label=\"New\"/>\n"
					+ "      <toolbarbutton label=\"Open\"/>\n"
					+ "      <toolbarbutton label=\"Save\"/>\n"
					+ "      <separator/>\n"
					+ "      <toolbarbutton label=\"Undo\"/>\n"
					+ "      <toolbarbutton label=\"Redo\"/>\n"
					+ "    </toolbar>\n"
					+ "  </north>\n"
					+ "  <center>\n"
					+ "    <div>Main content</div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "real-world: toolbar with many buttons inside borderlayout north");
		}

		@Test
		public void nestedToolbarsInDiv() {
			assertDoesNotThrow(() -> validate(zul(
					"<div>\n"
					+ "  <toolbar>\n"
					+ "    <toolbarbutton label=\"A\"/>\n"
					+ "    <toolbarbutton label=\"B\"/>\n"
					+ "  </toolbar>\n"
					+ "  <toolbar>\n"
					+ "    <toolbarbutton label=\"C\"/>\n"
					+ "    <toolbarbutton label=\"D\"/>\n"
					+ "  </toolbar>\n"
					+ "</div>"
			)), "multiple toolbars with multiple buttons inside div");
		}

		@Test
		public void fullBorderlayoutWithAllRegions() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <north>\n"
					+ "    <toolbar>\n"
					+ "      <toolbarbutton label=\"A\"/>\n"
					+ "      <toolbarbutton label=\"B\"/>\n"
					+ "    </toolbar>\n"
					+ "  </north>\n"
					+ "  <south>\n"
					+ "    <label value=\"footer\"/>\n"
					+ "  </south>\n"
					+ "  <east>\n"
					+ "    <tree/>\n"
					+ "  </east>\n"
					+ "  <west>\n"
					+ "    <listbox/>\n"
					+ "  </west>\n"
					+ "  <center>\n"
					+ "    <div>\n"
					+ "      <label value=\"A\"/>\n"
					+ "      <label value=\"B\"/>\n"
					+ "    </div>\n"
					+ "  </center>\n"
					+ "</borderlayout>"
			)), "complete borderlayout with all five regions");
		}

		@Test
		public void unknownElementShouldFail() {
			assertThrows(SAXException.class, () -> validate(zul(
					"<toolbar>\n"
					+ "  <nonExistentElement/>\n"
					+ "</toolbar>"
			)), "unknown element should be rejected by XSD");
		}

		@Test
		public void toolbarWithNestedComponents() {
			assertDoesNotThrow(() -> validate(zul(
					"<toolbar>\n"
					+ "  <combobutton label=\"Menu\">\n"
					+ "    <popup>\n"
					+ "      <label value=\"Item1\"/>\n"
					+ "      <label value=\"Item2\"/>\n"
					+ "    </popup>\n"
					+ "  </combobutton>\n"
					+ "  <toolbarbutton label=\"Action\"/>\n"
					+ "</toolbar>"
			)), "toolbar with combobutton+popup and toolbarbutton");
		}
	}
}
