/* F104_ZK_6086CodeeditorUnitTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:05:33 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zk.ui.ext.Readonly;
import org.zkoss.zul.Codeeditor;

/**
 * Pure component-API unit coverage for {@link Codeeditor} — no servlet container
 * needed (the component is CE; {@code smartUpdate} is a no-op while detached).
 * Covers SOP matrix sections A (API round-trip), D (per-property), I (edge/null),
 * and the validation-exception paths.
 */
public class F104_ZK_6086CodeeditorUnitTest {

	// ---- value ----
	@Test
	public void valueDefaultEmpty() {
		assertEquals("", new Codeeditor().getValue());
	}

	@Test
	public void valueSetGet() {
		Codeeditor c = new Codeeditor();
		c.setValue("hello");
		assertEquals("hello", c.getValue());
	}

	@Test
	public void valueNullBecomesEmpty() {
		Codeeditor c = new Codeeditor();
		c.setValue("x");
		c.setValue(null);
		assertEquals("", c.getValue());
	}

	@Test
	public void valueKeepsMultilineAndUnicode() {
		Codeeditor c = new Codeeditor();
		String v = "line1\nline2\thé世界🚀";
		c.setValue(v);
		assertEquals(v, c.getValue());
	}

	@Test
	public void valueConstructor() {
		assertEquals("seed", new Codeeditor("seed").getValue());
	}

	@Test
	public void valueConstructorNull() {
		assertEquals("", new Codeeditor(null).getValue());
	}

	// ---- language: default + one method per supported token (round-trip) ----
	@Test
	public void languageDefaultPlain() {
		assertEquals("plain", new Codeeditor().getLanguage());
	}

	@Test
	public void languagePlain() {
		assertEquals("plain", set("plain").getLanguage());
	}

	@Test
	public void languageHtml() {
		assertEquals("html", set("html").getLanguage());
	}

	@Test
	public void languageXml() {
		assertEquals("xml", set("xml").getLanguage());
	}

	@Test
	public void languageJava() {
		assertEquals("java", set("java").getLanguage());
	}

	@Test
	public void languageJavascript() {
		assertEquals("javascript", set("javascript").getLanguage());
	}

	@Test
	public void languageCss() {
		assertEquals("css", set("css").getLanguage());
	}

	@Test
	public void languageJson() {
		assertEquals("json", set("json").getLanguage());
	}

	@Test
	public void languageSql() {
		assertEquals("sql", set("sql").getLanguage());
	}

	@Test
	public void languageMarkdown() {
		assertEquals("markdown", set("markdown").getLanguage());
	}

	@Test
	public void languageNullResetsToPlain() {
		Codeeditor c = set("java");
		c.setLanguage(null);
		assertEquals("plain", c.getLanguage());
	}

	// ---- language: invalid tokens rejected ----
	@Test
	public void languageRejectsWrongCase() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setLanguage("Java"));
	}

	@Test
	public void languageRejectsDash() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setLanguage("c-sharp"));
	}

	@Test
	public void languageRejectsUnknown() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setLanguage("kotlin"));
	}

	@Test
	public void languageRejectsEmpty() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setLanguage(""));
	}

	@Test
	public void languageRejectsBlank() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setLanguage(" "));
	}

	// ---- readonly ----
	@Test
	public void readonlyDefaultFalse() {
		assertFalse(new Codeeditor().isReadonly());
	}

	@Test
	public void readonlySetTrue() {
		Codeeditor c = new Codeeditor();
		c.setReadonly(true);
		assertTrue(c.isReadonly());
	}

	@Test
	public void readonlySetBackToFalse() {
		Codeeditor c = new Codeeditor();
		c.setReadonly(true);
		c.setReadonly(false);
		assertFalse(c.isReadonly());
	}

	@Test
	public void readonlyImplementsMarkerInterface() {
		assertTrue(new Codeeditor() instanceof Readonly, "should use the framework Readonly facility");
	}

	// ---- disabled ----
	@Test
	public void disabledDefaultFalse() {
		assertFalse(new Codeeditor().isDisabled());
	}

	@Test
	public void disabledSetTrue() {
		Codeeditor c = new Codeeditor();
		c.setDisabled(true);
		assertTrue(c.isDisabled());
	}

	@Test
	public void disabledSetBackToFalse() {
		Codeeditor c = new Codeeditor();
		c.setDisabled(true);
		c.setDisabled(false);
		assertFalse(c.isDisabled());
	}

	@Test
	public void disabledImplementsMarkerInterface() {
		assertTrue(new Codeeditor() instanceof Disable, "should use the framework Disable facility");
	}

	// ---- lineNumbers ----
	@Test
	public void lineNumbersDefaultTrue() {
		assertTrue(new Codeeditor().isLineNumbers());
	}

	@Test
	public void lineNumbersSetFalse() {
		Codeeditor c = new Codeeditor();
		c.setLineNumbers(false);
		assertFalse(c.isLineNumbers());
	}

	@Test
	public void lineNumbersSetBackTrue() {
		Codeeditor c = new Codeeditor();
		c.setLineNumbers(false);
		c.setLineNumbers(true);
		assertTrue(c.isLineNumbers());
	}

	// ---- theme ----
	@Test
	public void themeDefaultNull() {
		assertNull(new Codeeditor().getTheme(), "unset → client derives from zk.themeName");
	}

	@Test
	public void themeLight() {
		Codeeditor c = new Codeeditor();
		c.setTheme("light");
		assertEquals("light", c.getTheme());
	}

	@Test
	public void themeDark() {
		Codeeditor c = new Codeeditor();
		c.setTheme("dark");
		assertEquals("dark", c.getTheme());
	}

	@Test
	public void themeNullClearsToDerived() {
		Codeeditor c = new Codeeditor();
		c.setTheme("dark");
		c.setTheme(null);
		assertNull(c.getTheme());
	}

	@Test
	public void themeRejectsWrongCase() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setTheme("Dark"));
	}

	@Test
	public void themeRejectsUnknown() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setTheme("solarized"));
	}

	@Test
	public void themeRejectsEmpty() {
		assertThrows(WrongValueException.class, () -> new Codeeditor().setTheme(""));
	}

	private static Codeeditor set(String language) {
		Codeeditor c = new Codeeditor();
		c.setLanguage(language);
		return c;
	}
}
