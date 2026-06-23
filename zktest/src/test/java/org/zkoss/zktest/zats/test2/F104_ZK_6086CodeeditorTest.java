/* F104_ZK_6086CodeeditorTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:05:25 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/*
 * Browser coverage for ZK-6086 Codeeditor (CE basic), SOP matrix sections:
 *   B per-language syntax highlighting, C theme/appearance, D per-property behavior,
 *   E combinations, G lifecycle. Server-side API/validation (A,D,I + exceptions)
 *   lives in F104_ZK_6086CodeeditorUnitTest; CSP (D4) in F104_ZK_6086CodeeditorCspTest.
 *
 * Token counting uses native querySelectorAll('span[class]') — ZK jq's descendant
 * selectors do not match CodeMirror's unicode-named highlight classes.
 */
public class F104_ZK_6086CodeeditorTest extends WebDriverTestCase {
	private static final String PAGE = "/test2/F104-ZK-6086-Codeeditor.zul";

	private int tokenSpans(String zid) {
		// `?? 0` so a not-yet-mounted editor yields a clean 0 (and a clean assertion
		// failure) instead of a NumberFormatException on the mount race.
		return Integer.parseInt(getEval(
				"String(jq('" + zid + " .cm-content')[0]?.querySelectorAll('span[class]').length ?? 0)"));
	}

	private String contentEditable(String zid) {
		return getEval("jq('" + zid + " .cm-content')[0].contentEditable");
	}

	/** native, editor-scoped gutter check — ZK jq descendant selectors are unreliable against CM DOM. */
	private boolean hasGutter(String zid) {
		return "true".equals(getEval("String(!!jq('" + zid + "')[0].querySelector('.cm-gutters'))"));
	}

	/** computed CSS color of the highlight-token span whose text is `kw`, '' if none (native querySelectorAll, like tokenSpans). */
	private String tokenColor(String zid, String kw) {
		return getEval("(function(){var c=jq('" + zid + " .cm-content')[0];if(!c)return '';"
				+ "var s=c.querySelectorAll('span[class]');"
				+ "for(var i=0;i<s.length;i++)if(s[i].textContent==='" + kw + "')return getComputedStyle(s[i]).color;"
				+ "return '';})()");
	}

	/** the editor's current document text via the public widget API. */
	private String docOf(String zid) {
		return getEval("zk.Widget.$('" + zid + "').getValue()");
	}

	/** CodeMirror's effective tab display width (the EditorState.tabSize facet). */
	private String tabSizeOf(String zid) {
		return getEval("String(zk.Widget.$('" + zid + "')._cm.state.tabSize)");
	}

	/** whether the editor's editable content node currently holds focus. */
	private boolean activeIsContent(String zid) {
		return "true".equals(getEval("String(document.activeElement === jq('" + zid + " .cm-content')[0])"));
	}

	// ===== B: per-language syntax highlighting (one test per grammar) =====
	private void assertHighlights(String editor) {
		connect(PAGE);
		waitResponse();
		assertTrue(jq("$" + editor + " .cm-editor").exists(), editor + " mounted");
		assertTrue(tokenSpans("$" + editor) > 0, editor + " produced highlight token spans");
	}

	@Test
	public void highlightJava() {
		assertHighlights("ceLangJava");
	}

	@Test
	public void highlightXml() {
		assertHighlights("ceLangXml");
	}

	@Test
	public void highlightHtml() {
		assertHighlights("ceLangHtml");
	}

	@Test
	public void highlightCss() {
		assertHighlights("ceLangCss");
	}

	@Test
	public void highlightJavascript() {
		assertHighlights("ceLangJavascript");
	}

	@Test
	public void highlightJson() {
		assertHighlights("ceLangJson");
	}

	@Test
	public void highlightSql() {
		assertHighlights("ceLangSql");
	}

	@Test
	public void highlightMarkdown() {
		assertHighlights("ceLangMarkdown");
	}

	@Test
	public void plainLanguageHasNoHighlightTokens() {
		connect(PAGE);
		waitResponse();
		assertEquals(0, tokenSpans("$ceLangPlain"), "plain text is not tokenized");
	}

	// ===== A/D: mount + value =====
	@Test
	public void everyEditorMounts() {
		connect(PAGE);
		waitResponse();
		// 25 <codeeditor> on the page each yield exactly one CodeMirror instance
		assertEquals(25, jq(".cm-editor").length(), "one .cm-editor per <codeeditor>");
		assertNoAnyError();
	}

	@Test
	public void valueLoadedIntoEditor() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq("$ceLangJava .cm-content").text().contains("public class A"));
	}

	@Test
	public void unicodeValuePreserved() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq("$ceUnicode .cm-content").text().contains("世界"), "unicode round-trips to the DOM");
	}

	// ===== D: readonly =====
	@Test
	public void readonlyEditorNotEditable() {
		connect(PAGE);
		waitResponse();
		assertEquals("false", contentEditable("$ceRO"));
	}

	@Test
	public void editableEditorIsEditable() {
		connect(PAGE);
		waitResponse();
		assertEquals("true", contentEditable("$ceEditable"));
	}

	@Test
	public void readonlyBlocksTyping() {
		connect(PAGE);
		waitResponse();
		String before = jq("$ceRO .cm-content").text();
		getActions().moveToElement(toElement(jq("$ceRO .cm-content"))).click().sendKeys("XYZ").perform();
		getActions().moveToElement(toElement(jq("$ceEditable .cm-content"))).click().perform();
		waitResponse();
		assertEquals(before, jq("$ceRO .cm-content").text(), "read-only content unchanged after typing");
	}

	@Test
	public void dynamicReadonlyToggle() {
		connect(PAGE);
		waitResponse();
		assertEquals("true", contentEditable("$ceDyn"));
		click(jq("$btnRO"));
		waitResponse();
		assertEquals("false", contentEditable("$ceDyn"), "setReadonly(true) reconfigured the live editor");
	}

	// ===== D: disabled =====
	@Test
	public void disabledEditorNotEditableAndDimmed() {
		connect(PAGE);
		waitResponse();
		assertEquals("false", contentEditable("$ceDisabled"), "disabled editor is not editable");
		assertEquals("true", getEval("String(jq('$ceDisabled')[0].classList.contains('z-codeeditor-disabled'))"),
				"disabled editor carries the dimmed modifier class");
	}

	@Test
	public void dynamicDisableToggle() {
		connect(PAGE);
		waitResponse();
		assertEquals("true", contentEditable("$ceDyn"));
		click(jq("$btnDisable"));
		waitResponse();
		assertEquals("false", contentEditable("$ceDyn"), "setDisabled(true) reconfigured the live editor");
		assertEquals("true", getEval("String(jq('$ceDyn')[0].classList.contains('z-codeeditor-disabled'))"));
	}

	// ===== D: focus / blur events =====
	@Test
	public void focusAndBlurFireEvents() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceFocus .cm-content"))).click().perform();
		waitResponse();
		assertEquals("focused", jq("$focusState").text(), "onFocus fired");
		getActions().moveToElement(toElement(jq("$ceEditable .cm-content"))).click().perform();
		waitResponse();
		assertEquals("blurred", jq("$focusState").text(), "onBlur fired");
	}

	// ===== D: line numbers =====
	@Test
	public void lineNumbersGutterShownByDefault() {
		connect(PAGE);
		waitResponse();
		assertTrue(hasGutter("$ceLn"));
	}

	@Test
	public void lineNumbersGutterHiddenWhenOff() {
		connect(PAGE);
		waitResponse();
		// guard first: hasGutter() reads the root div, which exists even before the
		// editor mounts, so without this a never-mounted editor would pass silently.
		assertTrue(jq("$ceNoLn .cm-editor").exists(), "editor mounted");
		assertFalse(hasGutter("$ceNoLn"), "lineNumbers=false renders no gutter");
	}

	@Test
	public void dynamicLineNumbersToggle() {
		connect(PAGE);
		waitResponse();
		assertTrue(hasGutter("$ceDyn"));
		click(jq("$btnNoLn"));
		waitResponse();
		assertFalse(hasGutter("$ceDyn"), "setLineNumbers(false) removed the gutter");
	}

	// ===== C: theme / appearance =====
	@Test
	public void darkThemeHasDifferentSurfaceThanLight() {
		connect(PAGE);
		waitResponse();
		String dark = getEval("getComputedStyle(jq('$ceDark .cm-editor')[0]).backgroundColor");
		String light = getEval("getComputedStyle(jq('$ceLight .cm-editor')[0]).backgroundColor");
		assertNotEquals(light, dark, "dark theme renders a different editor background");
	}

	@Test
	public void darkThemeUsesDarkSyntaxPalette() {
		connect(PAGE);
		waitResponse();
		// Regression guard: dark mode used to theme only the surface (background/
		// gutter) but kept CodeMirror's light defaultHighlightStyle for tokens, so
		// keywords stayed a dark, low-contrast color on the dark surface. Same
		// language + content in a light vs dark editor must now color the 'var'
		// keyword differently (proving the dark syntax palette is applied).
		String light = tokenColor("$ceLight", "var");
		String dark = tokenColor("$ceDark", "var");
		assertFalse(light.isEmpty(), "light keyword token not found");
		assertFalse(dark.isEmpty(), "dark keyword token not found");
		assertNotEquals(light, dark, "dark editor must use a dark syntax palette, not the light default");
	}

	@Test
	public void dynamicThemeToggleAppliesDark() {
		connect(PAGE);
		waitResponse();
		String before = getEval("getComputedStyle(jq('$ceDyn .cm-editor')[0]).backgroundColor");
		click(jq("$btnTheme"));
		waitResponse();
		String after = getEval("getComputedStyle(jq('$ceDyn .cm-editor')[0]).backgroundColor");
		assertNotEquals(before, after, "setTheme(dark) changed the editor surface live");
	}

	// ===== C: Tab-key accessibility (Escape toggles indent mode; default never traps) =====
	@Test
	public void tabMovesFocusByDefaultNoIndent() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceType .cm-content"))).click().perform();
		assertTrue(activeIsContent("$ceType"), "editor focused after click");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", docOf("$ceType"), "Tab without Escape must not indent");
		assertFalse(activeIsContent("$ceType"), "Tab moved focus out of the editor (no keyboard trap)");
	}

	@Test
	public void escapeThenTabIndentsTabChar() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceType .cm-content"))).click()
				.sendKeys(Keys.ESCAPE).sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("\t", docOf("$ceType"), "Escape then Tab inserts a tab character");
	}

	@Test
	public void tabSizeAttributeSetsDisplayWidth() {
		connect(PAGE);
		waitResponse();
		// the indent character stays a tab; tabSize only changes how wide it renders.
		assertEquals("4", tabSizeOf("$ceType"), "default tabSize is 4");
		assertEquals("2", tabSizeOf("$ceTabSize2"), "tabSize=\"2\" reaches CodeMirror's tabSize facet");
	}

	@Test
	public void escapeTwiceRestoresFocusNavigation() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceType .cm-content"))).click()
				.sendKeys(Keys.ESCAPE).sendKeys(Keys.ESCAPE).sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("", docOf("$ceType"), "Escape twice returns to focus navigation; Tab does not indent");
		assertFalse(activeIsContent("$ceType"), "Tab moves focus out after returning to navigation mode");
	}

	@Test
	public void widthHeightRenderedAsInlineStyle() {
		connect(PAGE);
		waitResponse();
		String w = getEval("jq('$ceWH')[0].style.width");
		String h = getEval("jq('$ceWH')[0].style.height");
		assertEquals("333px", w);
		assertEquals("111px", h);
	}

	// ===== D: dynamic language =====
	@Test
	public void dynamicLanguageReconfiguresAndHighlights() {
		connect(PAGE);
		waitResponse();
		assertEquals(0, tokenSpans("$ceDyn"), "plain starts untokenized");
		click(jq("$btnLang"));
		waitResponse();
		assertEquals("sql", getEval("zk.Widget.$('$ceDyn').getLanguage()"));
		assertTrue(tokenSpans("$ceDyn") > 0, "switching to sql produced highlight tokens");
	}

	// ===== D: events / typing =====
	@Test
	public void typingFiresOnChanging() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceType .cm-content"))).click().sendKeys("abc").perform();
		waitResponse();
		assertTrue(jq("$lblChanging").text().contains("abc"), "onChanging carried the typed text");
	}

	@Test
	public void blurFiresOnChangeCommit() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceType .cm-content"))).click().sendKeys("commitme").perform();
		getActions().moveToElement(toElement(jq("$ceEditable .cm-content"))).click().perform();
		waitResponse();
		assertTrue(jq("$lblChange").text().contains("commitme"), "onChange committed on blur");
	}

	@Test
	public void programmaticSetValueUpdatesEditor() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnSetVal")); // server-side ceDyn.setValue(...)
		waitResponse();
		assertEquals("changed by server", getEval("zk.Widget.$('$ceDyn').getValue()"),
				"server setValue pushed to the live editor");
		assertTrue(jq("$ceDyn .cm-content").text().contains("changed by server"), "new doc rendered");
		assertNoAnyError();
	}

	// ===== E: two-way binding =====
	@Test
	public void twoWayBindingInitialLoad() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq("$bound").text().contains("line 1"));
	}

	@Test
	public void twoWayBindingSavesOnBlur() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$ceBind .cm-content"))).click().sendKeys("BOUND6086").perform();
		getActions().moveToElement(toElement(jq("$ceEditable .cm-content"))).click().perform();
		waitResponse();
		assertTrue(jq("$bound").text().contains("BOUND6086"), "onChange saved into the view model");
	}

	// ===== G: lifecycle =====
	@Test
	public void rerenderKeepsSingleEditorAndValue() {
		connect(PAGE);
		waitResponse();
		assertEquals(1, jq("$host .cm-editor").length());
		click(jq("$btnRerender"));
		waitResponse();
		assertEquals(1, jq("$host .cm-editor").length(), "no orphaned editor after rerender");
		assertEquals("int n = 7;", getEval("zk.Widget.$('$ceRe').getValue()"), "value preserved");
		assertNoAnyError();
	}

	@Test
	public void detachReattachPreservesValueAndInstance() {
		connect(PAGE);
		waitResponse();
		click(jq("$btnDetach"));
		waitResponse();
		assertEquals(1, jq("$host .cm-editor").length(), "exactly one editor after detach/reattach");
		assertEquals("int n = 7;", getEval("zk.Widget.$('$ceRe').getValue()"));
		assertNoAnyError();
	}

	@Test
	public void multipleEditorsAreIndependent() {
		connect(PAGE);
		waitResponse();
		// editing one editor must not change another
		getActions().moveToElement(toElement(jq("$ceEditable .cm-content"))).click().sendKeys("solo").perform();
		getActions().moveToElement(toElement(jq("$ceType .cm-content"))).click().perform();
		waitResponse();
		// positive side first, so this can't pass vacuously when nothing was typed
		assertTrue(jq("$ceEditable .cm-content").text().contains("solo"), "the edited editor received the text");
		assertFalse(jq("$ceUnicode .cm-content").text().contains("solo"), "editors do not share state");
	}
}
