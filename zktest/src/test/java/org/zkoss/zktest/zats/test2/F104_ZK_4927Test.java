/* F104_ZK_4927Test.java

	Purpose:

	Description:

	History:
		Wed Jun 24 14:58:47 CST 2026, Created by peakerlee

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

/**
 * ZK-4927: markdown editor (PE component {@code org.zkoss.zkex.zul.Mdeditor}). A
 * single {@code <mdeditor>} edits markdown (CodeMirror, reused from
 * {@code <codeeditor>}) with a formatting toolbar and a markdown-it+DOMPurify
 * preview; pure display is {@code mode="preview"}+{@code readonly}. Covers
 * render, preview (GFM + XSS), mode layout, the always-on full toolbar (count +
 * action), server-relative image-path resolution, keyboard, events,
 * readonly/disabled, theme, dynamic reconfig, live preview, binding and
 * app-supplied ARIA.
 */
public class F104_ZK_4927Test extends WebDriverTestCase {
	private static final String PAGE = "/test2/F104-ZK-4927.zul";

	//-------- helpers --------//
	private String contentEditable(String id) {
		return getEval("jq('$" + id + " .cm-content')[0].contentEditable");
	}

	private String editorValue(String id) {
		return getEval("zk.Widget.$('$" + id + "').getValue()");
	}

	private int toolbarButtons(String id) {
		return Integer.parseInt(getEval(
				"String(jq('$" + id + "')[0].querySelectorAll('.z-mdeditor-tbarbtn').length)"));
	}

	private boolean hasModeClass(String id, String mode) {
		return "true".equals(getEval(
				"String(jq('$" + id + "')[0].classList.contains('z-mdeditor-" + mode + "'))"));
	}

	/** Native querySelector against an element — ZK jq's {@code $id descendant}
	 * selector is unreliable for multi-level selectors (see F104_ZK_6086CodeeditorTest). */
	private boolean has(String id, String selector) {
		return "true".equals(getEval(
				"String(!!jq('$" + id + "')[0].querySelector('" + selector + "'))"));
	}

	private String previewHtml(String id) {
		return getEval("jq('$" + id + "')[0].querySelector('.z-mdeditor-pv').innerHTML");
	}

	private String paneDisplay(String id, String sub) {
		return getEval(
				"getComputedStyle(jq('$" + id + "')[0].querySelector('.z-mdeditor-" + sub + "')).display");
	}

	private void typeInto(String id, CharSequence text) {
		getActions().moveToElement(toElement(jq("$" + id + " .cm-content"))).click().sendKeys(text).perform();
	}

	// ===== render: editor mounts (PE Runtime.init passed) + preview renders =====
	@Test
	public void editorMountsAndPreviewRenders() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq(".z-mdeditor").exists(), "mdeditor wrapper missing");
		assertTrue(jq("$mdEditor .cm-editor").exists(), "CodeMirror not mounted inside mdeditor");
		// the preview-mode instance renders the source on the client
		assertTrue(has("mdView", "h1"), "h1 not rendered by markdown-it");
		assertTrue(has("mdView", "pre code"), "fenced code block not rendered");
		assertTrue(has("mdView", "table"), "GFM table not rendered");
	}

	// ===== preview: markdown features =====
	@Test
	public void previewRendersMarkdownElements() {
		connect(PAGE);
		waitResponse();
		assertTrue(has("mdView", "h2"), "h2");
		assertTrue(has("mdView", "strong"), "bold -> strong");
		assertTrue(has("mdView", "em"), "italic -> em");
		assertTrue(has("mdView", "s"), "strikethrough -> s");
		assertTrue(has("mdView", "blockquote"), "blockquote");
		assertTrue(has("mdView", "ul li"), "unordered list");
		assertTrue(has("mdView", "ol li"), "ordered list");
		assertTrue(has("mdView", "a"), "link -> a");
		assertTrue(has("mdView", "img"), "image -> img");
	}

	// ===== XSS sanitization (preview pane only; the editor holds raw source as text) =====
	@Test
	public void xssSanitization() {
		connect(PAGE);
		waitResponse();
		String lower = previewHtml("mdView").toLowerCase();
		assertFalse(lower.contains("<script"), "<script> must be stripped");
		assertFalse(lower.contains("onerror="), "inline event handler must be stripped");
		assertFalse(lower.contains("javascript:"), "javascript: URLs must be stripped");
		assertFalse(lower.contains("<iframe"), "<iframe> must be stripped");
	}

	// ===== mode: editor survives switch + correct modifier class =====
	@Test
	public void modeSwitchKeepsEditor() {
		connect(PAGE);
		waitResponse();
		for (String mode : new String[] {"source", "preview", "split"}) {
			click(jq("$mode" + mode.substring(0, 1).toUpperCase() + mode.substring(1)));
			waitResponse();
			assertTrue(jq("$mdEditor .cm-editor").exists(), "CodeMirror missing after mode=" + mode);
			assertTrue(hasModeClass("mdEditor", mode), "root missing z-mdeditor-" + mode);
		}
	}

	// ===== mode: pane layout =====
	@Test
	public void modeLayoutShowsHidesPanes() {
		connect(PAGE);
		waitResponse();
		click(jq("$modeSource"));
		waitResponse();
		assertEquals("none", paneDisplay("mdEditor", "pv"), "source: preview hidden");
		assertNotEquals("none", paneDisplay("mdEditor", "cave"), "source: editor visible");
		click(jq("$modePreview"));
		waitResponse();
		assertEquals("none", paneDisplay("mdEditor", "cave"), "preview: editor hidden");
		assertNotEquals("none", paneDisplay("mdEditor", "pv"), "preview: preview visible");
		click(jq("$modeSplit"));
		waitResponse();
		assertNotEquals("none", paneDisplay("mdEditor", "cave"), "split: editor visible");
		assertNotEquals("none", paneDisplay("mdEditor", "pv"), "split: preview visible");
	}

	// ===== toolbar: the full set is always shown =====
	@Test
	public void toolbarAlwaysFull() {
		connect(PAGE);
		waitResponse();
		assertEquals(14, toolbarButtons("mdEditor"), "the full toolbar is always shown");
	}

	// ===== image: server-relative path resolved against the webapp context =====
	@Test
	public void imageSrcResolvedToBackendPath() {
		connect(PAGE);
		waitResponse();
		// the source has ![alt](/test2/img/sun.jpg); the preview rewrites the
		// <img> src through zk.ajaxURI, prepending the desktop context path so it
		// points at the backend resource (mirrors server-side Executions.encodeURL).
		String src = getEval("jq('$mdView')[0].querySelector('img').getAttribute('src')");
		assertTrue(src.endsWith("/test2/img/sun.jpg"), "resolved src keeps the resource path: " + src);
		assertNotEquals("/test2/img/sun.jpg", src, "context path was prepended (not the bare app-relative path)");
		assertEquals(getEval("zk.ajaxURI('/test2/img/sun.jpg')"), src, "img src resolved exactly as zk.ajaxURI would");
	}

	// ===== toolbar: a button mutates the editor =====
	@Test
	public void toolbarBoldButtonInsertsMarkers() {
		connect(PAGE);
		waitResponse();
		click(jq("$mbTb button[aria-label=\"Bold\"]"));
		waitResponse();
		assertEquals("****", editorValue("mbTb"), "Bold button inserted the bold markers");
	}

	// ===== keyboard: Ctrl/Cmd+B =====
	@Test
	public void boldKeyboardShortcut() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$mbType .cm-content"))).click()
				.keyDown(Keys.CONTROL).sendKeys("b").keyUp(Keys.CONTROL).perform();
		waitResponse();
		assertEquals("****", editorValue("mbType"), "Ctrl+B inserted bold markers");
	}

	// ===== events: focus / blur =====
	@Test
	public void focusAndBlurFireEvents() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$mbFocus .cm-content"))).click().perform();
		waitResponse();
		assertEquals("focused", jq("$focusState").text(), "onFocus fired");
		getActions().moveToElement(toElement(jq("$mbOther .cm-content"))).click().perform();
		waitResponse();
		assertEquals("blurred", jq("$focusState").text(), "onBlur fired");
	}

	// ===== events: onChanging / onChange =====
	@Test
	public void typingFiresOnChanging() {
		connect(PAGE);
		waitResponse();
		typeInto("mbType", "abc");
		waitResponse();
		assertTrue(jq("$lblChanging").text().contains("abc"), "onChanging carried the typed text");
	}

	@Test
	public void blurFiresOnChange() {
		connect(PAGE);
		waitResponse();
		typeInto("mbType", "commitme");
		getActions().moveToElement(toElement(jq("$mbOther .cm-content"))).click().perform();
		waitResponse();
		assertTrue(jq("$lblChange").text().contains("commitme"), "onChange committed on blur");
	}

	// ===== readonly / disabled =====
	@Test
	public void readonlyNotEditable() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq("$mbReadonly .cm-editor").exists(), "editor mounted");
		assertEquals("false", contentEditable("mbReadonly"), "readonly editor is not editable");
	}

	@Test
	public void disabledNotEditableAndDimmed() {
		connect(PAGE);
		waitResponse();
		assertEquals("false", contentEditable("mbDisabled"), "disabled editor is not editable");
		assertTrue("true".equals(getEval(
				"String(jq('$mbDisabled')[0].classList.contains('z-mdeditor-disabled'))")),
				"disabled editor carries the dimmed modifier class");
	}

	// ===== theme =====
	@Test
	public void darkThemeDiffersFromLight() {
		connect(PAGE);
		waitResponse();
		String dark = getEval("getComputedStyle(jq('$mbDark .cm-editor')[0]).backgroundColor");
		String light = getEval("getComputedStyle(jq('$mbLight .cm-editor')[0]).backgroundColor");
		assertNotEquals(light, dark, "dark theme renders a different editor background");
	}

	// ===== dynamic reconfiguration =====
	@Test
	public void dynamicModeToggle() {
		connect(PAGE);
		waitResponse();
		assertTrue(hasModeClass("mbDyn", "source"));
		click(jq("$dynMode"));
		waitResponse();
		assertTrue(hasModeClass("mbDyn", "split"), "setMode(split) applied live");
	}

	@Test
	public void dynamicReadonlyToggle() {
		connect(PAGE);
		waitResponse();
		assertEquals("true", contentEditable("mbDyn"));
		click(jq("$dynRO"));
		waitResponse();
		assertEquals("false", contentEditable("mbDyn"), "setReadonly(true) reconfigured the live editor");
	}

	@Test
	public void programmaticSetValue() {
		connect(PAGE);
		waitResponse();
		click(jq("$dynVal"));
		waitResponse();
		assertEquals("# changed by server", editorValue("mbDyn"), "server setValue pushed to the editor");
	}

	// ===== live preview =====
	@Test
	public void previewUpdatesOnEdit() {
		connect(PAGE);
		waitResponse();
		typeInto("mdEditor", "ZZUNIQUE ");
		waitResponse();
		assertTrue(previewHtml("mdEditor").contains("ZZUNIQUE"), "preview pane re-rendered with the new text");
	}

	// ===== two-way binding =====
	@Test
	public void twoWayBindingRoundTrips() {
		connect(PAGE);
		waitResponse();
		typeInto("mbBind", "BOUNDEDIT ");
		getActions().moveToElement(toElement(jq("$mbFocus .cm-content"))).click().perform();
		waitResponse();
		assertTrue(previewHtml("mdBound").contains("BOUNDEDIT"),
				"mdeditor onChange saved to the VM and the bound preview reloaded");
	}

	// ===== app-supplied ARIA =====
	@Test
	public void appAriaLabelForwardedToEditor() {
		connect(PAGE);
		waitResponse();
		assertEquals("my markdown editor",
				getEval("jq('$mbAria .cm-content')[0].getAttribute('aria-label')"),
				"ca:aria-label is forwarded onto CodeMirror's editable content");
	}
}
