/* F111_ZK_4927Test.java

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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-4927: markdown editor (PE component {@code org.zkoss.zkex.zul.Mdeditor}). A
 * single {@code <mdeditor>} edits markdown (CodeMirror, reused from
 * {@code <codeeditor>}) with a formatting toolbar and a markdown-it+DOMPurify
 * preview; pure display is {@code mode="preview"}+{@code readonly}. Covers
 * render, preview (GFM + XSS), mode layout, the always-on full toolbar (count +
 * action), server-relative image-path resolution, keyboard, events,
 * readonly/disabled, colorScheme, dynamic reconfig, live preview, binding and
 * app-supplied ARIA. The methods tagged {@code IceBlueOnly} assert the IceBlue
 * styles of zkThemeTemplate's mdeditor.less.
 */
public class F111_ZK_4927Test extends WebDriverTestCase {
	private static final String PAGE = "/test2/F111-ZK-4927.zul";

	//-------- helpers --------//
	private String contentEditable(String id) {
		return getEval("jq('$" + id + " .cm-content')[0].contentEditable");
	}

	private String editorValue(String id) {
		return getEval("zk.Widget.$('$" + id + "').getValue()");
	}

	private int toolbarButtons(String id) {
		return Integer.parseInt(getEval(
				"String(jq('$" + id + "')[0].querySelectorAll('.z-mdeditor-toolbar-button').length)"));
	}

	private boolean hasModeClass(String id, String mode) {
		return "true".equals(getEval(
				"String(jq('$" + id + "')[0].classList.contains('z-mdeditor-" + mode + "'))"));
	}

	/** Native querySelector against an element — ZK jq's {@code $id descendant}
	 * selector is unreliable for multi-level selectors (see F110_ZK_6086CodeeditorTest). */
	private boolean has(String id, String selector) {
		return "true".equals(getEval(
				"String(!!jq('$" + id + "')[0].querySelector('" + selector + "'))"));
	}

	/** The toolbar button carrying the given icon. Selected by icon, not by label:
	 * the button's aria-label/title is localized from msgzul. */
	private WebElement toolbarButton(String id, String icon) {
		return (WebElement) ((JavascriptExecutor) driver).executeScript(
				"return jq('$" + id + "')[0].querySelector('.z-icon-" + icon + "').closest('button')");
	}

	private String previewHtml(String id) {
		return getEval("jq('$" + id + "')[0].querySelector('.z-mdeditor-preview-pane').innerHTML");
	}

	/** sRGB bytes of a CSS colour, normalized through a canvas so any colour syntax compares. */
	private int[] rgb(String cssColor) {
		String[] c = getEval("(function () { var x = document.createElement('canvas').getContext('2d');"
				+ "x.fillStyle = '" + cssColor + "'; x.fillRect(0, 0, 1, 1);"
				+ "return Array.prototype.slice.call(x.getImageData(0, 0, 1, 1).data, 0, 3).join(','); })()").split(",");
		return new int[] {Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])};
	}

	private String token(String name) {
		return getEval("getComputedStyle(document.documentElement).getPropertyValue('" + name + "').trim()");
	}

	private void emulateForcedColors(String value) {
		Map<String, Object> feature = new HashMap<>();
		feature.put("name", "forced-colors");
		feature.put("value", value);
		Map<String, Object> media = new HashMap<>();
		media.put("features", List.of(feature));
		((ChromeDriver) getWebDriver()).executeCdpCommand("Emulation.setEmulatedMedia", media);
	}

	private String paneDisplay(String id, String sub) {
		return getEval(
				"getComputedStyle(jq('$" + id + "')[0].querySelector('.z-mdeditor-" + sub + "')).display");
	}

	/** The modifier the widget accepts for bold/italic: Cmd on macOS, Ctrl elsewhere.
	 * Read from `zk.mac` so the test tracks the widget instead of guessing the host. */
	private Keys boldModifier() {
		return "true".equals(getEval("String(!!zk.mac)")) ? Keys.COMMAND : Keys.CONTROL;
	}

	/** The modifier the widget must ignore — on macOS Ctrl-b/Ctrl-i are CodeMirror's
	 * caret moves, so accepting them would move the caret and then wrap. */
	private Keys ignoredModifier() {
		return boldModifier() == Keys.COMMAND ? Keys.CONTROL : Keys.COMMAND;
	}

	private void typeInto(String id, CharSequence text) {
		getActions().moveToElement(toElement(jq("$" + id + " .cm-content"))).click().sendKeys(text).perform();
	}

	private void selectToolbarText(String value) {
		((JavascriptExecutor) driver).executeScript(
				"var w = zk.Widget.$('$mbTb'); w.setMode('split'); w.setValue(arguments[0]);"
				+ "w._cm.dispatch({selection: {anchor: 0, head: arguments[0].length}}); w._cm.focus();", value);
	}

	// ===== render: editor mounts (PE Runtime.init passed) + preview renders =====
	@Test
	public void editorMountsAndPreviewRenders() {
		connect(PAGE);
		waitResponse();
		assertTrue(jq(".z-mdeditor").exists(), "mdeditor wrapper missing");
		assertTrue(jq("$mdEditor .cm-editor").exists(), "CodeMirror not mounted inside mdeditor");
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
		assertTrue(has("mdView", "li > label > input[type=checkbox]:not([checked])"), "open task -> unchecked box");
		assertTrue(has("mdView", "li > label > input[type=checkbox][checked]"), "done task -> checked box");
	}

	// ===== raw HTML is escaped (preview pane only; the editor holds raw source as text) =====
	@Test
	public void rawHtmlEscaped() {
		connect(PAGE);
		waitResponse();
		assertFalse(has("mdView", "script"), "<script> must not become an element");
		assertFalse(has("mdView", "[onerror]"), "inline event handler must not survive");
		assertFalse(has("mdView", "a[href^=javascript]"), "javascript: URLs must not survive");
		assertFalse(has("mdView", "iframe"), "<iframe> must not become an element");
		assertFalse(has("mdView", ".z-mdeditor-preview-pane style, .z-mdeditor-preview-pane [style]"), "<style> and style= must not restyle the page");
		assertTrue(previewHtml("mdView").contains("&lt;style&gt;"), "raw HTML is shown as text");
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
		assertEquals("none", paneDisplay("mdEditor", "preview-pane"), "source: preview hidden");
		assertNotEquals("none", paneDisplay("mdEditor", "cave"), "source: editor visible");
		click(jq("$modePreview"));
		waitResponse();
		assertEquals("none", paneDisplay("mdEditor", "cave"), "preview: editor hidden");
		assertNotEquals("none", paneDisplay("mdEditor", "preview-pane"), "preview: preview visible");
		click(jq("$modeSplit"));
		waitResponse();
		assertNotEquals("none", paneDisplay("mdEditor", "cave"), "split: editor visible");
		assertNotEquals("none", paneDisplay("mdEditor", "preview-pane"), "split: preview visible");
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
		String src = getEval("jq('$mdView')[0].querySelector('img').getAttribute('src')");
		assertTrue(src.endsWith("/test2/img/sun.jpg"), "resolved src keeps the resource path: " + src);
		assertNotEquals("/test2/img/sun.jpg", src, "context path was prepended (not the bare app-relative path)");
		assertEquals(getEval("zk.ajaxURI('/test2/img/sun.jpg')"), src, "img src resolved exactly as zk.ajaxURI would");
	}

	@Test
	public void linkHrefResolvedToBackendPath() {
		connect(PAGE);
		waitResponse();
		String links = "jq('$mbLinks')[0].querySelectorAll('a')";
		assertEquals(getEval("zk.ajaxURI('/test2/a.zul')"), getEval(links + "[0].getAttribute('href')"), "context-absolute href gets the context path");
		assertEquals(getEval("zk.ajaxResourceURI('/zul/a.pdf')"), getEval(links + "[1].getAttribute('href')"), "~./ href resolves as a class-web resource");
		assertEquals("https://www.zkoss.org", getEval(links + "[2].getAttribute('href')"), "external href left as authored");
	}

	@Test
	public void sessionIdGoesBeforeTheFragment() {
		connect(PAGE);
		waitResponse();
		String[] r = getEval("(function () { var w = zk.Widget.$('$mbLinks'), d = w.desktop, ctx = d.contextURI, res = d.resourceURI,"
				+ "p = document.createElement('div');"
				+ "p.innerHTML = '<a href=\"/test2/a.zul#sec\"></a><a href=\"~./zul/a.pdf#page=2\"></a>';"
				+ "d.contextURI = ctx + ';jsessionid=X'; d.resourceURI = res + ';jsessionid=X';"
				+ "try { w._resolveResourceURLs(p); } finally { d.contextURI = ctx; d.resourceURI = res; }"
				+ "return [ctx, p.children[0].getAttribute('href'), p.children[1].getAttribute('href')].join(' '); })()").split(" ");
		assertEquals(r[0] + "/test2/a.zul;jsessionid=X#sec", r[1], "a URL-rewritten session id goes before the fragment, as encodeURL puts it");
		assertTrue(r[2].endsWith("/zul/a.pdf;jsessionid=X#page=2"), r[2]);
	}

	// ===== toolbar: a button mutates the editor =====
	@Test
	public void toolbarBoldButtonInsertsMarkers() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toolbarButton("mbTb", "bold")).click().perform();
		waitResponse();
		assertEquals("****", editorValue("mbTb"), "Bold button inserted the bold markers");
	}

	// ===== keyboard: Ctrl/Cmd+B =====
	@Test
	public void boldKeyboardShortcut() {
		connect(PAGE);
		waitResponse();
		Keys mod = boldModifier();
		getActions().moveToElement(toElement(jq("$mbType .cm-content"))).click()
				.keyDown(mod).sendKeys("b").keyUp(mod).perform();
		waitResponse();
		assertEquals("****", editorValue("mbType"), mod + "+B inserted bold markers");
	}

	@Test
	public void boldShortcutIgnoresTheOtherModifier() {
		connect(PAGE);
		waitResponse();
		Keys mod = ignoredModifier();
		getActions().moveToElement(toElement(jq("$mbType .cm-content"))).click()
				.keyDown(mod).sendKeys("b").keyUp(mod).perform();
		waitResponse();
		assertFalse(editorValue("mbType").contains("*"), mod + "+B must not wrap, got " + editorValue("mbType"));
	}

	@Test
	public void boldShortcutLeftToBrowserWhenReadonly() {
		connect(PAGE);
		waitResponse();
		assertEquals("false", getEval("(function () {"
				+ "var e = new KeyboardEvent('keydown', {key: 'b', code: 'KeyB', ctrlKey: !zk.mac, metaKey: !!zk.mac, bubbles: true, cancelable: true});"
				+ "jq('$mbReadonly .cm-content')[0].dispatchEvent(e);"
				+ "return String(e.defaultPrevented);})()"),
				"a readonly editor must not swallow the browser's own shortcut");
	}

	// ===== keyboard: Ctrl/Cmd+I wraps at the caret, not CodeMirror's Mod-i selectParentSyntax =====
	@Test
	public void italicShortcutWrapsAtCaret() {
		connect(PAGE);
		waitResponse();
		Keys mod = boldModifier();
		typeInto("mbType", "one two");
		getActions().sendKeys(Keys.LEFT, Keys.LEFT).keyDown(mod).sendKeys("i").keyUp(mod).perform();
		waitResponse();
		assertEquals("one t**wo", editorValue("mbType"), mod + "+I inserted the markers at the caret");
	}

	// ===== toolbar: a line prefix applies to every selected line; a heading does not stack =====
	@Test
	public void prefixAppliesToEverySelectedLine() {
		connect(PAGE);
		waitResponse();
		typeInto("mbTb", "a" + Keys.ENTER + "b");
		getEval("(zk.Widget.$('$mbTb')._cm.dispatch({selection: {anchor: 0, head: 3}}), 'done')");
		getActions().moveToElement(toolbarButton("mbTb", "list-ul")).click().perform();
		waitResponse();
		assertEquals("- a\n- b", editorValue("mbTb"), "both selected lines got the list marker");
	}

	@Test
	public void headingReplacesExistingMarker() {
		connect(PAGE);
		waitResponse();
		typeInto("mbTb", "Title");
		WebElement heading = toolbarButton("mbTb", "header");
		getActions().moveToElement(heading).click().click().perform();
		waitResponse();
		assertEquals("## Title", editorValue("mbTb"), "a second Heading click does not stack markers");
	}

	@Test
	public void prefixAtLineStartPutsCaretAfterMarker() {
		connect(PAGE);
		waitResponse();
		getActions().moveToElement(toElement(jq("$mbTb .cm-content"))).click().perform();
		getActions().moveToElement(toolbarButton("mbTb", "list-ul")).click().sendKeys("item").perform();
		waitResponse();
		assertEquals("- item", editorValue("mbTb"), "text typed after the prefix lands behind the marker");
	}

	@Test
	public void headingAtLineStartPutsCaretAfterReplacedMarker() {
		connect(PAGE);
		waitResponse();
		typeInto("mbTb", "# Title");
		getEval("(zk.Widget.$('$mbTb')._cm.dispatch({selection: {anchor: 0}}), 'done')");
		getActions().moveToElement(toolbarButton("mbTb", "header")).click().sendKeys("x").perform();
		waitResponse();
		assertEquals("## xTitle", editorValue("mbTb"), "the caret moves behind the replaced marker");
	}

	// ===== toolbar: a block (fence, rule, table) sits on lines of its own =====
	@Test
	public void codeBlockFencesOnOwnLines() {
		connect(PAGE);
		waitResponse();
		typeInto("mbTb", "Before code after");
		getEval("(zk.Widget.$('$mbTb')._cm.dispatch({selection: {anchor: 7, head: 11}}), 'done')");
		getActions().moveToElement(toolbarButton("mbTb", "file-code")).click().perform();
		waitResponse();
		assertEquals("Before \n\n```\ncode\n```\n\n after", editorValue("mbTb"), "the fences do not share a line with the prose");
	}

	@Test
	public void horizontalRuleAfterParagraph() {
		connect(PAGE);
		waitResponse();
		getEval("(zk.Widget.$('$mbTb').setMode('split'), 'done')");
		typeInto("mbTb", "Paragraph");
		getActions().moveToElement(toolbarButton("mbTb", "minus")).click().sendKeys("Next").perform();
		waitResponse();
		assertEquals("Paragraph\n\n---\n\nNext", editorValue("mbTb"),
				"a blank line keeps the rule from turning the paragraph into a heading, and the text typed next from turning the rule into text");
		assertTrue(has("mbTb", ".z-mdeditor-preview-pane hr + p"), "the typed text is a paragraph below the rule");
	}

	@Test
	public void tableKeepsFollowingTextOut() {
		connect(PAGE);
		waitResponse();
		getEval("(zk.Widget.$('$mbTb').setMode('split'), 'done')");
		typeInto("mbTb", "x" + Keys.ENTER + "y");
		getEval("(zk.Widget.$('$mbTb')._cm.dispatch({selection: {anchor: 1}}), 'done')");
		getActions().moveToElement(toolbarButton("mbTb", "table")).click().sendKeys("Next").perform();
		waitResponse();
		assertEquals("x\n\n| Column | Column |\n| --- | --- |\n| Cell | Cell |\n\nNext\n\ny", editorValue("mbTb"),
				"blank lines keep the typed text and the next line from becoming table rows");
		assertEquals("2", getEval("String(jq('$mbTb')[0].querySelectorAll('.z-mdeditor-preview-pane tr').length)"));
		assertTrue(has("mbTb", ".z-mdeditor-preview-pane table + p + p"), "the typed text and the next line are paragraphs below the table");
	}

	@Test
	public void formattingKeepsBoundaryWhitespaceOutsideMarkers() {
		connect(PAGE);
		waitResponse();
		String[][] cases = {{"bold", "**", "strong"}, {"italic", "*", "em"}, {"strikethrough", "~~", "s"}};
		for (String[] c : cases) {
			selectToolbarText(" hello ");
			getActions().moveToElement(toolbarButton("mbTb", c[0])).click().perform();
			waitResponse();
			assertEquals(" " + c[1] + "hello" + c[1] + " ", editorValue("mbTb"));
			assertTrue(has("mbTb", ".z-mdeditor-preview-pane " + c[2]), "the selected text is formatted");
			assertEquals("hello", getEval("zk.Widget.$('$mbTb')._cm.state.sliceDoc("
					+ "zk.Widget.$('$mbTb')._cm.state.selection.main.from, zk.Widget.$('$mbTb')._cm.state.selection.main.to)"));
		}
	}

	@Test
	public void formattingWhitespaceOnlySelectionLeavesItUnchanged() {
		connect(PAGE);
		waitResponse();
		selectToolbarText(" \t ");
		getActions().moveToElement(toolbarButton("mbTb", "bold")).click().perform();
		waitResponse();
		assertEquals(" \t ", editorValue("mbTb"));
	}

	@Test
	public void inlineCodePreservesBackticksAndBoundarySpaces() {
		connect(PAGE);
		waitResponse();
		for (String source : new String[] {"console.log(`x`)", "`edge`", "a``b", " a ", "  "}) {
			selectToolbarText(source);
			getActions().moveToElement(toolbarButton("mbTb", "code")).click().perform();
			waitResponse();
			assertEquals(source, getEval("jq('$mbTb')[0].querySelector('.z-mdeditor-preview-pane code').textContent"));
			assertEquals("1", getEval("String(jq('$mbTb')[0].querySelectorAll('.z-mdeditor-preview-pane code').length)"));
		}
	}

	@Test
	public void codeBlockPreservesEmbeddedFences() {
		connect(PAGE);
		waitResponse();
		String source = "```js\nconsole.log('hello');\n```";
		selectToolbarText(source);
		getActions().moveToElement(toolbarButton("mbTb", "file-code")).click().perform();
		waitResponse();
		assertEquals(source + "\n", getEval("jq('$mbTb')[0].querySelector('.z-mdeditor-preview-pane pre code').textContent"));
		assertEquals("1", getEval("String(jq('$mbTb')[0].querySelectorAll('.z-mdeditor-preview-pane pre').length)"));
	}

	@Test
	public void taskMarkersSupportWhitespaceAndReferenceDefinitions() {
		connect(PAGE);
		waitResponse();
		String source = "- [x]\t**done**\n- [ ]\n  pending\n- [x] linked marker\n"
				+ "- \\[x] literal marker\n\n[x]: /reference\n\n[x] ordinary reference";
		((JavascriptExecutor) driver).executeScript("zk.Widget.$('$mdView').setValue(arguments[0])", source);
		waitResponse();
		String pv = "jq('$mdView')[0].querySelector('.z-mdeditor-preview-pane')";
		assertEquals("3", getEval("String(" + pv + ".querySelectorAll('input[type=checkbox]').length)"));
		assertEquals("2", getEval("String(" + pv + ".querySelectorAll('input[checked]').length)"));
		assertEquals("done", getEval(pv + ".querySelector('li label strong').textContent"));
		assertEquals("[x] literal marker", getEval(pv + ".querySelectorAll('li')[3].textContent"));
		assertEquals("1", getEval("String(" + pv + ".querySelectorAll('a').length)"));
	}

	// ===== WCAG: names, keyboard access, focus ring, contrast =====
	@Test
	public void taskCheckboxIsLabelledByItemText() {
		connect(PAGE);
		waitResponse();
		assertEquals("open task", getEval("jq('$mdView')[0].querySelector('li > label > input:not([checked])').parentNode.textContent.trim()"),
				"the disabled checkbox gets its accessible name from the item text");
	}

	@Test
	public void previewPaneIsKeyboardScrollable() {
		connect(PAGE);
		waitResponse();
		assertEquals("0", getEval("String(jq('$mdView')[0].querySelector('.z-mdeditor-preview-pane').tabIndex)"),
				"the scrollable preview is reachable by keyboard");
	}

	@Test
	public void toolbarFocusRingIsVisible() {
		connect(PAGE);
		waitResponse();
		WebElement bold = toolbarButton("mbTb", "bold");
		assertEquals(true, ((JavascriptExecutor) driver).executeScript("arguments[0].focus(); return arguments[0].matches(':focus-visible')", bold));
		assertEquals("solid", ((JavascriptExecutor) driver).executeScript("return getComputedStyle(arguments[0]).outlineStyle", bold));
		assertNotEquals("rgba(0, 0, 0, 0)", ((JavascriptExecutor) driver).executeScript("return getComputedStyle(arguments[0]).outlineColor", bold),
				"the default theme's transparent active border must not be the ring colour");
	}

	@Test
	public void editorFocusRingIsPaintedOnEverySide() throws IOException {
		connect(PAGE);
		waitResponse();
		Map<String, String> rings = Map.of("mbFocus", "--zk-codeeditor-border-color-focus", "mbDark", "--zk-codeeditor-dark-border-color-focus");
		for (Map.Entry<String, String> e : rings.entrySet()) {
			String id = e.getKey();
			getActions().moveToElement(toElement(jq("$" + id + " .cm-content"))).click().perform();
			waitResponse();
			int[] ring = rgb(token(e.getValue()));
			getEval("(jq('$" + id + "')[0].scrollIntoView({block: 'center'}), 'done')");
			String[] b = getEval("(function () { var r = jq('$" + id + "')[0].getBoundingClientRect();"
					+ "return [r.left, r.top, r.right, r.bottom, devicePixelRatio].join(','); })()").split(",");
			double dpr = Double.parseDouble(b[4]);
			int left = (int) Math.round(Double.parseDouble(b[0]) * dpr), top = (int) Math.round(Double.parseDouble(b[1]) * dpr),
					right = (int) Math.round(Double.parseDouble(b[2]) * dpr) - 1, bottom = (int) Math.round(Double.parseDouble(b[3]) * dpr) - 1,
					mx = (left + right) / 2, my = (top + bottom) / 2;
			BufferedImage shot = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
			// {x, y, step x, step y}: walk in from each edge; the 1px border plus the 1px ring show the colour twice, the border alone once
			int[][] sides = {{mx, top - 1, 0, 1}, {mx, bottom + 1, 0, -1}, {left - 1, my, 1, 0}, {right + 1, my, -1, 0}};
			for (int[] side : sides) {
				int hits = 0;
				for (int i = 0; i < 6; i++) {
					int x = side[0] + i * side[2], y = side[1] + i * side[3];
					if (x < 0 || y < 0 || x >= shot.getWidth() || y >= shot.getHeight())
						continue;
					int px = shot.getRGB(x, y);
					// 8 per channel: the screenshot's colour encoding rounds
					if (Math.abs(((px >> 16) & 0xff) - ring[0]) <= 8 && Math.abs(((px >> 8) & 0xff) - ring[1]) <= 8 && Math.abs((px & 0xff) - ring[2]) <= 8)
						hits++;
				}
				assertTrue(hits >= 2 * dpr, id + ": the focus ring is covered on the side walked from " + side[0] + "," + side[1] + " (" + hits + " ring pixels)");
			}
		}
	}

	@Test
	public void editorFocusOutlinedInForcedColors() {
		connect(PAGE);
		waitResponse();
		Assumptions.assumeTrue(getWebDriver() instanceof ChromeDriver, "forced-colors emulation needs CDP");
		emulateForcedColors("active");
		try {
			getActions().moveToElement(toElement(jq("$mbFocus .cm-content"))).click().perform();
			waitResponse();
			assertEquals("solid", getEval("getComputedStyle(jq('$mbFocus')[0]).outlineStyle"),
					"forced colours drop the box-shadow ring, so the editor needs an outline");
			assertEquals("2px", getEval("getComputedStyle(jq('$mbFocus')[0]).outlineWidth"));
		} finally {
			emulateForcedColors("none");
		}
	}

	// IceBlue-only: the link colour comes from the template's mdeditor.less; Marble uses --zk-color-inverse-primary.
	@Test
	@Tag("IceBlueOnly")
	public void darkPreviewLinkKeepsContrast() {
		connect(PAGE);
		waitResponse();
		assertEquals("rgb(86, 156, 214)", getEval("getComputedStyle(jq('$mbDarkLink')[0].querySelector('.z-mdeditor-preview-pane a')).color"),
				"a dark preview link uses the dark palette, not the browser's #0000EE (1.8:1)");
	}

	// IceBlue-only: the toolbar fill comes from the template's mdeditor.less; Marble leaves it transparent over the dark root.
	@Test
	@Tag("IceBlueOnly")
	public void darkToolbarTakesTheDarkSurface() {
		connect(PAGE);
		waitResponse();
		assertEquals(getEval("(function () { var p = document.createElement('i'); p.style.backgroundColor = 'var(--zk-codeeditor-background)';"
				+ "document.body.appendChild(p); var c = getComputedStyle(p).backgroundColor; p.remove(); return c; })()"),
				getEval("getComputedStyle(jq('$mbDark')[0].querySelector('.z-mdeditor-toolbar')).backgroundColor"),
				"colorScheme=\"dark\" puts the toolbar on the editor's dark surface, as IMdeditor#withColorScheme documents");
	}

	// IceBlue-only: the focus border comes from the template's mdeditor.less; Marble paints its own focus ring.
	@Test
	@Tag("IceBlueOnly")
	public void editorFocusRecoloursTheBorder() {
		connect(PAGE);
		waitResponse();
		String border = "getComputedStyle(jq('$mbFocus')[0]).borderTopColor";
		String rest = getEval(border);
		getActions().moveToElement(toElement(jq("$mbFocus .cm-content"))).click().perform();
		waitResponse();
		assertEquals(getEval("(function () { var p = document.createElement('i'); p.style.color = 'var(--zk-input-focus-border-color)';"
				+ "document.body.appendChild(p); var c = getComputedStyle(p).color; p.remove(); return c; })()"), getEval(border),
				"CodeMirror's own focus outline is clipped, so the border must show the focus");
		assertNotEquals(rest, getEval(border));
	}

	@Test
	public void previewLinksAreUnderlined() {
		connect(PAGE);
		waitResponse();
		for (String id : new String[] {"mdView", "mbDarkLink"})
			assertEquals("underline", getEval("getComputedStyle(jq('$" + id + "')[0].querySelector('.z-mdeditor-preview-pane a')).textDecorationLine"),
					id + ": a link in text cannot rely on colour alone");
	}

	// ===== the toolbar labels are localized through msgzul, with or without za11y =====
	@Test
	public void toolbarMessagesExported() {
		connect(PAGE);
		waitResponse();
		assertEquals("Bold", getEval("String(msgzul.MDEDITOR_BOLD)"), "MDEDITOR_* reaches msgzul");
		assertEquals("Horizontal rule", getEval("String(msgzul.MDEDITOR_HORIZONTAL_RULE)"));
	}

	@Test
	public void toolbarLabelsComeFromMsgzul() {
		connect(PAGE);
		waitResponse();
		getEval("(msgzul.MDEDITOR_BOLD = 'probe', zk.Widget.$('$mbTb').rerender(), 'done')");
		waitResponse();
		WebElement bold = toolbarButton("mbTb", "bold");
		assertEquals("probe", bold.getDomAttribute("aria-label"), "the bundle, not the widget's English default, names the button");
		assertEquals("probe", bold.getDomAttribute("title"));
	}

	// ===== setSclass rewrites className: the dark/disabled modifiers must survive =====
	@Test
	public void sclassKeepsDarkAndDisabled() {
		connect(PAGE);
		waitResponse();
		click(jq("$sclassBtn"));
		waitResponse();
		assertTrue(jq("$mbDark").hasClass("z-mdeditor-dark"), "dark modifier kept");
		assertTrue(jq("$mbDisabled").hasClass("z-mdeditor-disabled"), "disabled modifier kept");
		assertTrue(jq("$mbDark").hasClass("x"), "sclass applied");
		assertTrue(jq("$ceDark").hasClass("z-codeeditor-dark"), "a plain codeeditor keeps its dark modifier");
		assertTrue(jq("$ceDisabled").hasClass("z-codeeditor-disabled"), "a plain codeeditor keeps its disabled modifier");
		assertTrue(jq("$ceDark").hasClass("x"), "codeeditor sclass applied");
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

	@Test
	public void focusInPreviewModeLandsOnPreviewPane() {
		connect(PAGE);
		waitResponse();
		click(jq("$focusView"));
		waitResponse();
		assertEquals("true", getEval("String(document.activeElement === jq('$mdView')[0].querySelector('.z-mdeditor-preview-pane'))"),
				"server focus() reaches the visible preview pane");
	}

	@Test
	public void previewModeFiresFocusAndBlur() {
		connect(PAGE);
		waitResponse();
		click(jq("$focusView"));
		waitResponse();
		assertEquals("focused", jq("$viewFocus").text(), "onFocus fired from the preview pane");
		getActions().moveToElement(toElement(jq("$mbOther .cm-content"))).click().perform();
		waitResponse();
		assertEquals("blurred", jq("$viewFocus").text(), "onBlur fired from the preview pane");
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

	@Test
	public void disabledPreviewPaneNotFocusable() {
		connect(PAGE);
		waitResponse();
		String pane = "jq('$mbDisabledSplit')[0].querySelector('.z-mdeditor-preview-pane')";
		assertEquals("-1", getEval("String(" + pane + ".tabIndex)"), "disabled: the preview pane leaves the tab order");
		assertEquals("none", getEval("getComputedStyle(" + pane + ").pointerEvents"), "disabled: the preview links take no clicks");
		getEval("(zk.Widget.$('$mbDisabledSplit').setDisabled(false), 'done')");
		assertEquals("0", getEval("String(" + pane + ".tabIndex)"), "re-enabled: the preview pane is reachable again");
	}

	@Test
	public void disabledPreviewLinksStayOutOfKeyboardNavigation() {
		connect(PAGE);
		waitResponse();
		click(jq("$beforeDisabledPreview"));
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("true", getEval("String(document.activeElement === jq('$afterDisabledPreview')[0])"));

		getEval("(zk.Widget.$('$mbDisabledSplit').setDisabled(false), 'done')");
		getEval("(jq('$mbDisabledSplit')[0].querySelector('.z-mdeditor-preview-pane').focus(), 'done')");
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("true", getEval("String(document.activeElement === jq('$mbDisabledSplit')[0].querySelector('a'))"));

		getEval("(zk.Widget.$('$mbDisabledSplit').setDisabled(true),"
				+ "zk.Widget.$('$mbDisabledSplit').setValue('[new link](#new-target)'), 'done')");
		waitResponse();
		click(jq("$beforeDisabledPreview"));
		getActions().sendKeys(Keys.TAB).perform();
		waitResponse();
		assertEquals("true", getEval("String(document.activeElement === jq('$afterDisabledPreview')[0])"),
				"a preview refresh must keep new links disabled too");
	}

	@Test
	public void disabledPreviewStaysPerceivable() {
		connect(PAGE);
		waitResponse();
		String pane = "jq('$mbDisabledSplit')[0].querySelector('.z-mdeditor-preview-pane')";
		assertEquals("false", getEval("String(!!" + pane + ".closest('[inert], [aria-hidden=\"true\"]'))"),
				"the dimmed preview is still shown, so assistive technology must still read it");
		assertEquals("-1", getEval("String(" + pane + ".querySelector('a').tabIndex)"), "its links leave the tab order instead");
		getEval("(zk.Widget.$('$mbDisabledSplit').setDisabled(false), 'done')");
		assertEquals("0", getEval("String(" + pane + ".querySelector('a').tabIndex)"), "re-enabled: the links are tabbable again");
	}

	// ===== readonly / disabled: the toolbar must not mutate the document =====
	@Test
	public void toolbarDisabledWhenLocked() {
		connect(PAGE);
		waitResponse();
		for (String id : new String[] { "mbReadonly", "mbDisabled" })
			assertFalse(toolbarButton(id, "bold").isEnabled(), id + ": toolbar button must be disabled");
	}

	@Test
	public void toolbarFollowsLiveLockChanges() {
		connect(PAGE);
		waitResponse();
		getEval("(zk.Widget.$('$mbReadonly').setReadonly(false), zk.Widget.$('$mbDisabled').setDisabled(false), 'done')");
		for (String id : new String[] { "mbReadonly", "mbDisabled" })
			assertTrue(toolbarButton(id, "bold").isEnabled(), id + ": clearing the lock re-enables the toolbar");
		getEval("(zk.Widget.$('$mbTb').setDisabled(true), 'done')");
		assertFalse(toolbarButton("mbTb", "bold").isEnabled(), "setDisabled(true) locks the toolbar");
	}

	@Test
	public void disabledToolbarIsDimmedOnce() {
		connect(PAGE);
		waitResponse();
		assertEquals("1", getEval("getComputedStyle(jq('$mbDisabled')[0].querySelector('.z-mdeditor-toolbar-button')).opacity"),
				"the disabled root already dims the toolbar");
	}

	// the editor is hidden in preview mode, so a toolbar edit could never commit an onChange
	@Test
	public void toolbarDisabledInPreviewMode() {
		connect(PAGE);
		waitResponse();
		click(jq("$modePreview"));
		waitResponse();
		assertFalse(toolbarButton("mdEditor", "bold").isEnabled(), "preview: toolbar button must be disabled");
		click(jq("$modeSplit"));
		waitResponse();
		assertTrue(toolbarButton("mdEditor", "bold").isEnabled(), "split: toolbar button enabled again");
	}

	@Test
	public void toolbarCannotMutateLockedEditor() {
		connect(PAGE);
		waitResponse();
		// readOnly blocks only CodeMirror's commands, not a direct dispatch, and a disabled button can't be clicked: call the mutator
		for (String id : new String[] { "mbReadonly", "mbDisabled" }) {
			String before = editorValue(id);
			getEval("(zk.Widget.$('$" + id + "')._wrap('**','**'), 'done')");
			waitResponse();
			assertEquals(before, editorValue(id), id + ": _wrap must not mutate a locked editor");
		}
	}

	// ===== colorScheme =====
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
		assertTrue(toolbarButton("mbDyn", "bold").isEnabled());
		click(jq("$dynRO"));
		waitResponse();
		assertEquals("false", contentEditable("mbDyn"), "setReadonly(true) reconfigured the live editor");
		assertFalse(toolbarButton("mbDyn", "bold").isEnabled(), "setReadonly(true) locked the toolbar");
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

	@Test
	public void appAriaLabelNamesPreviewPane() {
		connect(PAGE);
		waitResponse();
		String pane = "jq('$mdView')[0].querySelector('.z-mdeditor-preview-pane')";
		assertEquals("region", getEval(pane + ".getAttribute('role')"));
		assertEquals("markdown display", getEval(pane + ".getAttribute('aria-label')"),
				"preview mode focuses the pane, so the page's name has to reach it");
	}

	@Test
	public void previewHeadingLinesDoNotOverlap() {
		connect(PAGE);
		waitResponse();
		String rects = getEval("(function () { var r = document.createRange();"
				+ "r.selectNodeContents(jq('$mbHeading')[0].querySelector('.z-mdeditor-preview-pane h1'));"
				+ "return Array.prototype.map.call(r.getClientRects(), function (c) { return c.top + ',' + c.bottom; }).join(';'); })()");
		String[] lines = rects.split(";");
		assertTrue(lines.length > 1, "the heading wraps: " + rects);
		for (int i = 1; i < lines.length; i++)
			// 0.5px: sub-pixel line positions
			assertTrue(Double.parseDouble(lines[i].split(",")[0]) >= Double.parseDouble(lines[i - 1].split(",")[1]) - 0.5,
					"wrapped heading lines overlap: " + rects);
	}

	@Test
	public void formattingShortcutDoesNotFireAncestorCtrlKeys() {
		connect(PAGE);
		waitResponse();
		// ChromeDriver on macOS sends keyCode 0 for a Cmd chord, which ctrlKeys never matches: take the Ctrl path on every host
		getEval("(zk.mac = false, 'done')");
		getActions().moveToElement(toElement(jq("$mbCtrlKeys .cm-content"))).click()
				.keyDown(Keys.CONTROL).sendKeys("b").keyUp(Keys.CONTROL).perform();
		waitResponse();
		assertEquals("****", editorValue("mbCtrlKeys"));
		assertEquals("", jq("$ctrlKeysLog").text(), "the ancestor's ctrlKeys fired for a key the editor consumed");
	}
}
