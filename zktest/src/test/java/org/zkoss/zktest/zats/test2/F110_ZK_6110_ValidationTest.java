/* F110_ZK_6110_ValidationTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Jul 23 14:50:33 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;

/**
 * F110_ZK_6110_ValidationTest
 * setResponsive rejects invalid values (case-sensitive)
 */
public class F110_ZK_6110_ValidationTest {
	@Test
	public void setResponsive_rejectsUpperCase() {
		assertThrows(WrongValueException.class, () -> new Listbox().setResponsive("STACKING"));
	}

	@Test
	public void setResponsive_rejectsTableLiteral() {
		assertThrows(WrongValueException.class, () -> new Listbox().setResponsive("table"));
	}

	@Test
	public void setResponsive_rejectsRandomString() {
		assertThrows(WrongValueException.class, () -> new Listbox().setResponsive("flex"));
	}

	@Test
	public void setResponsive_acceptsStacking() {
		Listbox lb = new Listbox();
		lb.setResponsive("stacking");
		assertEquals("stacking", lb.getResponsive());
	}

	@Test
	public void setResponsive_acceptsNone() {
		Listbox lb = new Listbox();
		lb.setResponsive("none");
		assertEquals("none", lb.getResponsive());
	}

	@Test
	public void setResponsive_emptyNormalizesToNull() {
		Listbox lb = new Listbox();
		lb.setResponsive("stacking");
		lb.setResponsive("");
		assertNull(lb.getResponsive(), "empty string should normalize to null");
	}

	@Test
	public void setResponsive_defaultIsNull() {
		assertNull(new Listbox().getResponsive());
	}

	@Test
	public void setResponsiveColumns_storedVerbatim() {
		Listbox lb = new Listbox();
		lb.setResponsiveColumns("sm-2 md-none lg-3");
		assertEquals("sm-2 md-none lg-3", lb.getResponsiveColumns());
	}

	@Test
	public void setResponsiveColumns_acceptsAnyStringNoServerException() {
		// Server does NOT parse — invalid tokens are dropped on the client.
		Listbox lb = new Listbox();
		lb.setResponsiveColumns("garbage tokens here");
		assertEquals("garbage tokens here", lb.getResponsiveColumns());
	}

	@Test
	public void setResponsiveColumns_emptyNormalizesToNull() {
		Listbox lb = new Listbox();
		lb.setResponsiveColumns("sm-1");
		lb.setResponsiveColumns("");
		assertNull(lb.getResponsiveColumns());
	}

	@Test
	public void setResponsiveColumns_defaultIsNull() {
		assertNull(new Listbox().getResponsiveColumns());
	}

	// Listheader.responsiveVisible defaults true
	@Test
	public void listheader_responsiveVisibleDefaultsTrue() {
		assertTrue(new Listheader().isResponsiveVisible());
	}

	@Test
	public void listheader_setResponsiveVisibleFalse() {
		Listheader h = new Listheader();
		h.setResponsiveVisible(false);
		assertTrue(!h.isResponsiveVisible());
	}

	// ========================================================================
	// zul.xsd — the responsive attributes must accept EL / data binding, the way
	// every other constrained ZUL attribute does (union with annotationType).
	// ========================================================================

	private static final String NS = "http://www.zkoss.org/2005/zul";
	private static Schema _schema;

	/** Tolerates the pre-existing duplicate onOK/onCancel declarations in zul.xsd
	 * so the schema still compiles; only document validation must be strict. */
	private static class LenientSchemaErrorHandler implements ErrorHandler {
		@Override
		public void warning(SAXParseException e) { /* ignore */ }

		@Override
		public void error(SAXParseException e) { /* ignore schema-level errors */ }

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}
	}

	@BeforeAll
	static void initSchema() throws Exception {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setErrorHandler(new LenientSchemaErrorHandler());
		try (InputStream is = F110_ZK_6110_ValidationTest.class.getResourceAsStream("/metainfo/xml/zul.xsd")) {
			_schema = factory.newSchema(new StreamSource(is));
		}
	}

	private static void validate(String body) throws Exception {
		Validator validator = _schema.newValidator();
		validator.validate(new StreamSource(new StringReader(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<zk xmlns=\"" + NS + "\">\n" + body + "\n</zk>\n")));
	}

	@Test
	public void xsd_responsiveAcceptsLiteral() {
		assertDoesNotThrow(() -> validate("<listbox responsive=\"stacking\"/>"));
	}

	@Test
	public void xsd_responsiveAcceptsEl() {
		assertDoesNotThrow(() -> validate("<listbox responsive=\"${vm.mode}\"/>"),
				"responsive must accept an EL expression");
	}

	@Test
	public void xsd_responsiveAcceptsBindingAnnotation() {
		assertDoesNotThrow(() -> validate("<listbox responsive=\"@load(vm.mode)\"/>"),
				"responsive must accept a data-binding annotation");
	}

	@Test
	public void xsd_responsiveColumnsAcceptsLiteral() {
		assertDoesNotThrow(() -> validate("<listbox responsiveColumns=\"sm-1 md-none\"/>"));
	}

	@Test
	public void xsd_responsiveColumnsAcceptsEl() {
		assertDoesNotThrow(() -> validate("<listbox responsiveColumns=\"${vm.cols}\"/>"),
				"responsiveColumns must accept an EL expression");
	}

	@Test
	public void xsd_responsiveColumnsAcceptsBindingAnnotation() {
		assertDoesNotThrow(() -> validate("<listbox responsiveColumns=\"@load(vm.cols)\"/>"),
				"responsiveColumns must accept a data-binding annotation");
	}

	@Test
	public void xsd_gridResponsiveAcceptsBindingAnnotation() {
		assertDoesNotThrow(() -> validate("<grid responsive=\"@load(vm.mode)\"/>"),
				"grid responsive must accept a data-binding annotation");
	}

	@Test
	public void xsd_gridResponsiveColumnsAcceptsBindingAnnotation() {
		assertDoesNotThrow(() -> validate("<grid responsiveColumns=\"@load(vm.cols)\"/>"),
				"grid responsiveColumns must accept a data-binding annotation");
	}
}
