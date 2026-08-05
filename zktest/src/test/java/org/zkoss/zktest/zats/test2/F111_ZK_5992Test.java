/* F111_ZK_5992Test.java

        Purpose:

        Description:

        History:
                Mon May 04 16:51:54 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F111_ZK_5992Test extends WebDriverTestCase {

	private static final String LIB_PROP = "org.zkoss.zkmax.zul.tbeditor.plugins";
	private static final String PLUGIN_PATH_PROP = "org.zkoss.zkmax.zul.tbeditor.pluginPath";
	private static final String CUSTOM_PLUGIN_PATH = "/test2/F111-ZK-5992-plugins/";

	/** Keep in sync with TBEDITOR_PLUGIN_MANIFEST in zkmax's tbeditor/Tbeditor.ts and F111-ZK-5992-AllPlugins.zul. */
	private static final List<String> ALL_PLUGINS = Arrays.asList(
			"base64", "cleanpaste", "colors", "emoji",
			"fontfamily", "fontsize", "history", "indent",
			"insertaudio", "lineheight", "pasteimage", "preformatted",
			"ruby", "specialchars", "table", "template"
	);

	private static final List<String> PLUGINS_WITH_CSS = Arrays.asList(
			"colors", "emoji", "specialchars", "table"
	);

	/** Mirrors the {@code btns} of TBEDITOR_PLUGIN_MANIFEST. */
	private static final Map<String, List<String>> PLUGIN_BUTTONS = new LinkedHashMap<>();
	static {
		PLUGIN_BUTTONS.put("base64", Arrays.asList("base64"));
		PLUGIN_BUTTONS.put("colors", Arrays.asList("foreColor", "backColor"));
		PLUGIN_BUTTONS.put("emoji", Arrays.asList("emoji"));
		PLUGIN_BUTTONS.put("fontfamily", Arrays.asList("fontfamily"));
		PLUGIN_BUTTONS.put("fontsize", Arrays.asList("fontsize"));
		PLUGIN_BUTTONS.put("history", Arrays.asList("historyUndo", "historyRedo"));
		PLUGIN_BUTTONS.put("indent", Arrays.asList("indent", "outdent"));
		PLUGIN_BUTTONS.put("insertaudio", Arrays.asList("insertAudio"));
		PLUGIN_BUTTONS.put("lineheight", Arrays.asList("lineheight"));
		PLUGIN_BUTTONS.put("preformatted", Arrays.asList("preformatted"));
		PLUGIN_BUTTONS.put("ruby", Arrays.asList("ruby"));
		PLUGIN_BUTTONS.put("specialchars", Arrays.asList("specialChars"));
		PLUGIN_BUTTONS.put("table", Arrays.asList("table"));
		// 'template' adds its button only when config.plugins.templates is set, as the AllPlugins page does
		PLUGIN_BUTTONS.put("template", Arrays.asList("template"));
	}

	/** Trumbowyg {@code hasIcon: false} buttons: ZK ships no icon for them. */
	private static final List<String> TEXTUAL_BUTTONS = Arrays.asList(
			"fontfamily", "fontsize", "lineheight", "template"
	);

	private static final Map<String, String[]> CSS_PLUGIN_DROPDOWNS = new LinkedHashMap<>();
	static {
		CSS_PLUGIN_DROPDOWNS.put("colors", new String[] { "foreColor", "button", "trumbowyg.colors.css" });
		CSS_PLUGIN_DROPDOWNS.put("emoji", new String[] { "emoji", "button", "trumbowyg.emoji.css" });
		CSS_PLUGIN_DROPDOWNS.put("specialchars",
				new String[] { "specialChars", "button", "trumbowyg.specialchars.css" });
		CSS_PLUGIN_DROPDOWNS.put("table", new String[] { "table", "td", "trumbowyg.table.css" });
	}

	private static final Map<String, String> DROPDOWN_PLUGINS = new LinkedHashMap<>();
	static {
		DROPDOWN_PLUGINS.put("fontfamily", "fontfamily");
		DROPDOWN_PLUGINS.put("fontsize", "fontsize");
		DROPDOWN_PLUGINS.put("lineheight", "lineheight");
		DROPDOWN_PLUGINS.put("template", "template");
	}

	private static final Map<String, String[]> MODAL_PLUGINS = new LinkedHashMap<>();
	static {
		MODAL_PLUGINS.put("base64", new String[] { "base64", "file" });
		MODAL_PLUGINS.put("insertaudio", new String[] { "insertAudio", "src" });
		MODAL_PLUGINS.put("ruby", new String[] { "ruby", "rubyText" });
	}

	@Test
	public void perComponentAndInheritance() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992.zul");
		waitResponse();

		waitForEditors(6);

		assertEquals("1", scriptCount("trumbowyg.colors.js"),
				"per-component plugins= must load colors plugin script");
		assertEquals("1", scriptCount("trumbowyg.fontsize.js"),
				"per-component plugins= must load fontsize plugin script");

		assertEquals("1", scriptCount("trumbowyg.emoji.js"),
				"inherited custom-attribute must load emoji plugin");
		assertEquals("emoji", resolvedPlugins("$tbInherited"),
				"an editor with no plugins= inherits the nearest custom-attribute");

		assertEquals("colors", resolvedPlugins("$tbOverride"),
				"component plugins= must override (not merge with) the inherited custom-attribute");

		assertEquals("", resolvedPlugins("$tbClear"),
				"explicit empty plugins= must clear inherited plugins (override to none)");

		assertEquals("history", resolvedPlugins("$tbList"),
				"a Collection custom-attribute must resolve to its plugin names");

		assertEquals("0", scriptCount("trumbowyg.z_does_not_exist.js"),
				"failed plugin script element should not linger in <head>");

		// colors is also requested by tbOverride, so the count of 1 above proves dedupe
	}

	/** The late editors are built after every plugin is registered, so a leak would be deterministic. */
	@Test
	public void pluginsDoNotLeakAcrossEditors() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992.zul");
		waitResponse();
		waitForEditors(6);

		assertEquals("color,fontsize", startedPlugins("tbColors"));
		assertEquals("emoji", startedPlugins("tbInherited"));
		assertEquals("color", startedPlugins("tbOverride"));
		assertEquals("history", startedPlugins("tbList"));

		click(jq("$addLate"));
		waitResponse();
		waitForEditors(8);
		assertEquals("color", startedPlugins("tbLateColors"),
				"an editor must start only the plugins it requested");
		assertEquals("", startedPlugins("tbLateNone"),
				"plugins=\"\" must start no plugin even when others are registered");
	}

	@Test
	public void mvvm() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-MVVM.zul");
		waitResponse();
		waitForEditors(2);

		assertEquals("colors", resolvedPlugins("$tbList"), "@load of a List must reach setPlugins(List)");
		assertEquals("fontsize,emoji", resolvedPlugins("$tbCsv"), "@load of a String must reach setPlugins(String)");
		assertEquals("color", startedPlugins("tbList"));
		assertEquals("emoji,fontsize", startedPlugins("tbCsv"));

		click(jq("$switch"));
		waitResponse();
		waitForEditors(2);
		assertEquals("history", resolvedPlugins("$tbList"), "@NotifyChange must re-render with the new List");
		assertEquals("", resolvedPlugins("$tbCsv"), "@NotifyChange must re-render with the new String");
		assertEquals("history", startedPlugins("tbList"));
		assertEquals("", startedPlugins("tbCsv"));
	}

	@Test
	public void customPluginKeyStaysInItsEditor() {
		try {
			connectCustomPluginPage();
			assertEquals("zk5992CustomKey", startedPlugins("tbCustom"));
			click(jq("$addLate"));
			waitResponse();
			waitForEditors(4);
			assertEquals("", startedPlugins("tbLateNone"),
					"a plugin registered under a key other than its file name must start only where it is named");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void pluginThatRegistersNothingIsReported() {
		try {
			connectCustomPluginPage();
			assertTrue(getEval("jq('.z-error').text()").contains("zk5992noreg"),
					"a plugin script that registers no Trumbowyg plugin must be reported in the error box");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void pluginThatRegistersNothingIsRetried() {
		try {
			connectCustomPluginPage();
			click(jq("$addLate"));
			waitResponse();
			waitForEditors(4);
			assertEquals("2", getEval("window.zk5992NoRegRuns"),
					"a load that registered nothing must not be cached as a success");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void libraryProperty() {
		Library.setProperty(LIB_PROP, "table");
		try {
			connect("/test2/F111-ZK-5992-Lib.zul");
			waitResponse();
			waitForEditors(1);
			assertEquals("1", scriptCount("trumbowyg.table.js"),
					"library-property plugin must load for the bare editor");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void overrideLibraryProperty() {
		Library.setProperty(LIB_PROP, "table");
		try {
			connect("/test2/F111-ZK-5992.zul");
			waitResponse();
			waitForEditors(6);

			assertEquals("0", scriptCount("trumbowyg.table.js"),
					"a closer scope must override (not merge with) the library property");

			assertEquals("colors,fontsize", resolvedPlugins("$tbColors"),
					"component plugins= must fully replace the library property");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void pluginPathOverride() {
		final String customPath = "/web/js/zkmax/tbeditor/ext/plugins-override/";
		Library.setProperty(LIB_PROP, "base64");
		Library.setProperty(PLUGIN_PATH_PROP, customPath);
		try {
			connect("/test2/F111-ZK-5992-Lib.zul");
			waitResponse();

			// not waitForEditors: the custom path 404s and onerror removes the script; _pluginPath is set at bind
			waitForWidget("$tb");

			String pluginPath = getEval("zk.Widget.$('$tb').getPluginPath()");
			assertEquals(customPath, pluginPath,
					"widget should receive the custom pluginPath verbatim");

			assertEquals("0",
					getEval("jq('script[src*=\"/ext/plugins/\"]').length"),
					"default plugin path must not be used when override is set");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void noPluginsNoRequests() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-Lib.zul");
		waitResponse();
		waitForEditors(1);

		assertEquals("0",
				getEval("jq('script[src*=\"/ext/plugins/\"]').length"),
				"a bare editor with no configured plugins must not request any plugin assets");
	}

	@TestFactory
	Stream<DynamicTest> eachPluginLoads() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		Stream<DynamicTest> perPlugin = ALL_PLUGINS.stream().map(name -> DynamicTest.dynamicTest(
				"loads:" + name, () -> {
					assertEquals("1", scriptCount("trumbowyg." + name + ".js"),
							"plugin '" + name + "' must load its js exactly once");
					String expectedCss = PLUGINS_WITH_CSS.contains(name) ? "1" : "0";
					assertEquals(expectedCss, linkCount("trumbowyg." + name + ".css"),
							"plugin '" + name + "' css presence must match its TBEDITOR_PLUGIN_MANIFEST entry");
				}));

		DynamicTest registry = DynamicTest.dynamicTest("registry-populated", () -> {
			int registered = Integer.parseInt(getEval("Object.keys(jq.trumbowyg.plugins).length"));
			assertTrue(registered >= ALL_PLUGINS.size(),
					"jq.trumbowyg.plugins should hold an entry for every loaded plugin, saw "
							+ registered + " for " + ALL_PLUGINS.size() + " plugins");
		});

		// class-path assets are cached for a year, so an unversioned URL serves stale files after an upgrade
		Stream<DynamicTest> versioned = Stream.of("trumbowyg.colors.js", "trumbowyg.colors.css")
				.map(file -> DynamicTest.dynamicTest("versioned:" + file, () ->
						assertTrue(getEval("jq('script[src*=\"" + file + "\"],link[href*=\"" + file + "\"]')"
										+ ".map(function(){return this.src||this.href;}).get().join()").contains("/web/_zv"),
								file + " must be requested through a versioned URL")));
		DynamicTest versionedSprite = DynamicTest.dynamicTest("versioned:icons.svg", () ->
				assertTrue(getEval("jq.trumbowyg.svgPath").contains("/web/_zv"),
						"the icon sprite must be requested through a versioned URL"));

		// a malformed asset URL (e.g. the ;jsessionid= bug) shows up only as a console error
		DynamicTest noError = DynamicTest.dynamicTest("no-console-errors", this::assertNoJSError);

		return Stream.concat(Stream.concat(perPlugin, versioned), Stream.of(registry, versionedSprite, noError));
	}

	@TestFactory
	Stream<DynamicTest> eachPluginAddsToolbarButton() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		return PLUGIN_BUTTONS.entrySet().stream().map(e -> DynamicTest.dynamicTest(
				"button:" + e.getKey(), () -> {
					String editorId = "tb_" + e.getKey();
					for (String btn : e.getValue())
						assertEquals("1", buttonCountIn(editorId, btn),
								"plugin '" + e.getKey() + "' must auto-add its '" + btn
										+ "' toolbar button to its editor");
				}));
	}

	@TestFactory
	Stream<DynamicTest> pluginButtonsRenderIconOrLabel() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());
		waitForIconSprite();

		return PLUGIN_BUTTONS.entrySet().stream().flatMap(e -> e.getValue().stream().map(btn -> {
			String editorId = "tb_" + e.getKey();
			return TEXTUAL_BUTTONS.contains(btn)
					? DynamicTest.dynamicTest("label:" + btn,
							() -> assertEquals("ok", labelStatus(editorId, btn),
									"textual button '" + btn + "' must render its label, not an empty box"))
					: DynamicTest.dynamicTest("icon:" + btn,
							() -> assertEquals("ok", iconStatus(editorId, btn),
									"button '" + btn + "' must paint an icon from ext/icons.svg"));
		}));
	}

	@Test
	public void autoPluginButtonsCanBeDisabled() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-Buttons.zul");
		waitResponse();
		waitForEditors(3);

		assertEquals("1", scriptCount("trumbowyg.table.js"),
				"both editors request the table plugin (deduped to one load)");

		assertEquals("1", buttonCountIn("tbAuto", "table"),
				"auto-buttons on: the table toolbar button must appear");

		assertEquals("0", buttonCountIn("tbNoAuto", "table"),
				"autoPluginButtons=false must suppress the auto-inserted table button");

		assertNoJSError();
	}

	/** The plugin group is appended last, so its buttons often end a toolbar row near the editor's right edge. */
	@Test
	public void dropdownStaysInsideANarrowEditor() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		eval("zk.Widget.$('$tb_emoji').setWidth('460px')");
		waitResponse();
		openDropdown("tb_emoji", "emoji");
		assertEquals("inside", getEval("(function(){var n=zk.Widget.$('$tb_emoji').$n(),"
				+ "b=n.querySelector('.z-tbeditor-box').getBoundingClientRect(),"
				+ "d=n.querySelector('[data-z-tbeditor-dropdown=emoji]').getBoundingClientRect();"
				+ "return d.left>=b.left-1&&d.right<=b.right+1?'inside'"
				+ ":'dropdown '+Math.round(d.left)+'..'+Math.round(d.right)+' vs box '+Math.round(b.left)+'..'+Math.round(b.right);})()"),
				"an open dropdown must stay within the editor's width");
	}

	@Test
	public void pluginButtonInsideDropdownIsNotRepeated() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-Buttons.zul");
		waitResponse();
		waitForEditors(3);

		assertEquals("1", getEval("jq(zk.Widget.$('$tbInDropdown').$n())"
				+ ".find('[data-z-tbeditor-dropdown=image] .z-tbeditor-base64-dropdown-button').length"),
				"the application's image dropdown must still offer base64");
		assertEquals("0", buttonCountIn("tbInDropdown", "base64"),
				"a plugin button already inside a btnsDef dropdown must not be added again");
	}

	/** Fails if the vendored plugin CSS loses its {@code .trumbowyg-} to {@code .z-tbeditor-} re-prefix. */
	@TestFactory
	Stream<DynamicTest> cssPluginDropdownsAreLaidOutAsGrids() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		return CSS_PLUGIN_DROPDOWNS.entrySet().stream().map(e -> DynamicTest.dynamicTest(
				"layout:" + e.getKey(), () -> {
					String plugin = e.getKey(), btn = e.getValue()[0],
							child = e.getValue()[1], css = e.getValue()[2];
					String editorId = "tb_" + plugin;

					openDropdown(editorId, btn);
					String styled = dropdownLayout(editorId, btn, child);
					assertEquals("grid", styled, "plugin '" + plugin + "' must lay its dropdown "
							+ child + "s out as a grid of small squares, which only happens when "
							+ css + " applies to the z-tbeditor- prefix");

					assertEquals("true", setStylesheetDisabled(css, true),
							"could not disable " + css + "; the discrimination check would be meaningless");
					String unstyled;
					try {
						unstyled = dropdownLayout(editorId, btn, child);
					} finally {
						setStylesheetDisabled(css, false);
					}
					assertNotEquals("grid", unstyled, "the grid assertion has no teeth: it still reports"
							+ " 'grid' for '" + plugin + "' with " + css + " disabled");

					closeDropdowns();
				}));
	}

	/** {@code trumbowyg.history.js} is patched to use {@code prefix + 'disable'} instead of a hardcoded class. */
	@Test
	public void historyButtonsGreyOutAndActuallyUndo() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		final String ed = "tb_history";
		assertEquals("1", buttonCountIn(ed, "historyUndo"), "the Undo button must exist to be tested");
		assertEquals("1", buttonCountIn(ed, "historyRedo"), "the Redo button must exist to be tested");
		// Chrome is Blink, which shows the default toolbar's own Undo / Redo
		assertEquals("0", buttonCountIn(ed, "undo"), "history's Undo replaces the default one");
		assertEquals("0", buttonCountIn(ed, "redo"), "history's Redo replaces the default one");

		waitFor("Undo of " + ed + " to be marked disabled", disabledFlag(ed, "historyUndo"));
		assertEquals("true", isDisabled(ed, "historyUndo"), "a fresh editor has nothing to undo");
		assertEquals("true", isDisabled(ed, "historyRedo"), "a fresh editor has nothing to redo");

		String pristine = editorText(ed);
		click(button(ed, "historyUndo"));
		waitResponse();
		assertEquals(pristine, editorText(ed), "a disabled Undo must not alter the content");

		click(jq("$" + ed).find(".z-tbeditor-editor"));
		getActions().sendKeys("ZK5992undo").perform();
		waitResponse();
		waitFor("Undo of " + ed + " to become enabled", enabledFlag(ed, "historyUndo"));
		String typed = editorText(ed);
		assertTrue(typed.contains("ZK5992undo"), "typing must reach the editor, saw '" + typed + "'");

		click(button(ed, "historyUndo"));
		waitResponse();
		waitFor("Redo of " + ed + " to become enabled", enabledFlag(ed, "historyRedo"));
		assertNotEquals(typed, editorText(ed), "Undo must revert the typed text");

		for (int i = 0; i < 20 && "false".equals(isDisabled(ed, "historyUndo")); i++) {
			click(button(ed, "historyUndo"));
			waitResponse();
		}
		assertEquals("true", isDisabled(ed, "historyUndo"),
				"Undo must grey out again at the bottom of the history stack");
		assertEquals(pristine, editorText(ed), "undoing everything must restore the original content");
		assertEquals("false", isDisabled(ed, "historyRedo"),
				"Redo must stay available while undone states remain");
	}

	/** A re-render must not take the toolbar the previous bind built for the application's own {@code btns}. */
	@Test
	public void clientSetPluginsRebuildsTheToolbar() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		final String ed = "tb_history";
		assertEquals("1", buttonCountIn(ed, "historyUndo"), "history's Undo must be there before the switch");
		eval("zk.Widget.$('$" + ed + "').setPlugins(['table'])");
		waitFor("the table button of " + ed, editorScope(ed) + ".find('.z-tbeditor-table-button').length>0?1:0");
		assertEquals("0", buttonCountIn(ed, "historyUndo"), "history is no longer named, so its Undo must go");
		// Chrome is Blink, which shows the default toolbar's own Undo / Redo
		assertEquals("1", buttonCountIn(ed, "undo"), "the default Undo must come back");
		assertEquals("1", buttonCountIn(ed, "redo"), "the default Redo must come back");
	}

	@Test
	public void historyThatFailedToLoadKeepsTheDefaultUndo() {
		clearLibraryProperties();
		Library.setProperty(LIB_PROP, "history");
		// the custom path hosts no history plugin, so its script 404s
		Library.setProperty(PLUGIN_PATH_PROP, CUSTOM_PLUGIN_PATH);
		try {
			connect("/test2/F111-ZK-5992-Lib.zul");
			waitResponse();
			waitForEditors(1);
			assertEquals("1", buttonCountIn("tb", "undo"), "a history plugin that failed to load must leave the default Undo");
			assertEquals("1", buttonCountIn("tb", "redo"), "a history plugin that failed to load must leave the default Redo");
		} finally {
			clearLibraryProperties();
		}
	}

	@Test
	public void applicationPluginRegisteredDuringALoadStaysPageWide() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-Lib.zul");
		waitResponse();
		waitForEditors(1);
		// setPlugins re-renders in a microtask; the next one runs after colors.js is requested but before it loads,
		// as the application's own plugin script would while colors.js downloads
		eval("function(){zk.Widget.$('$tb').setPlugins(['colors']);"
				+ "queueMicrotask(function(){jq.extend(true, jq.trumbowyg, {plugins: {zk5992app: {init: function(){}}}});});}()");
		waitFor("the colors button of tb", editorScope("tb") + ".find('.z-tbeditor-foreColor-button').length>0?1:0");
		eval("zk.Widget.$('$tb').setPlugins([])");
		waitFor("tb to drop the colors button", editorScope("tb") + ".find('.z-tbeditor-foreColor-button').length===0?1:0");
		assertEquals("zk5992app", startedPlugins("tb"),
				"a plugin the application registered itself must start in every editor");
	}

	@Test
	public void stylesheetIsRequestedAgainFromAWorkingPluginPath() {
		clearLibraryProperties();
		Library.setProperty(LIB_PROP, "colors");
		// the custom path hosts no colors plugin, so its script and stylesheet 404
		Library.setProperty(PLUGIN_PATH_PROP, CUSTOM_PLUGIN_PATH);
		try {
			connect("/test2/F111-ZK-5992-Lib.zul");
			waitResponse();
			waitForEditors(1);
			eval("zk.Widget.$('$tb').setPluginPath('')");
			waitFor("the colors button of tb", editorScope("tb") + ".find('.z-tbeditor-foreColor-button').length>0?1:0");
			assertEquals("1", linkCount("/ext/plugins/colors/trumbowyg.colors.css"),
					"the stylesheet must be requested again from the bundled path");
		} finally {
			clearLibraryProperties();
		}
	}

	@TestFactory
	Stream<DynamicTest> eachPluginPerformsItsAction() {
		clearLibraryProperties();
		connect("/test2/F111-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		List<DynamicTest> cases = new ArrayList<>();
		for (Map.Entry<String, String> e : DROPDOWN_PLUGINS.entrySet())
			cases.add(DynamicTest.dynamicTest("acts:" + e.getKey(),
					() -> assertDropdownOffersEntries(e.getKey(), e.getValue())));
		for (Map.Entry<String, String[]> e : MODAL_PLUGINS.entrySet())
			cases.add(DynamicTest.dynamicTest("acts:" + e.getKey(),
					() -> assertModalOpensWithField(e.getKey(), e.getValue()[0], e.getValue()[1])));
		cases.add(DynamicTest.dynamicTest("acts:indent", this::assertIndentMovesTheParagraph));
		cases.add(DynamicTest.dynamicTest("acts:preformatted", this::assertPreformattedWrapsSelection));
		cases.add(DynamicTest.dynamicTest("acts:cleanpaste", this::assertCleanpasteCleansWordHtml));
		cases.add(DynamicTest.dynamicTest("acts:cleanpaste-sanitizes", this::assertCleanpasteDropsScript));
		cases.add(DynamicTest.dynamicTest("acts:pasteimage",
				() -> assertPasteHandlerRegistered("pasteimage", "readAsDataURL")));
		return cases.stream();
	}

	private void assertDropdownOffersEntries(String plugin, String btnName) {
		String editorId = "tb_" + plugin;
		dismissModal();
		openDropdown(editorId, btnName);
		int entries = parseInt(dropdownEntryCount(editorId, btnName));
		assertTrue(entries > 0, "plugin '" + plugin + "' must offer at least one dropdown entry");
		if ("template".equals(plugin)) {
			String text = dropdownText(editorId, btnName);
			assertTrue(text.contains("Greeting"),
					"the template dropdown must list the page's 'Greeting' template, saw: " + text);
		}
		closeDropdowns();
	}

	private void assertModalOpensWithField(String plugin, String btnName, String fieldName) {
		String editorId = "tb_" + plugin;
		closeDropdowns();
		dismissModal();
		click(button(editorId, btnName));
		waitResponse();
		waitFor("the '" + plugin + "' modal to open", "jq('.z-tbeditor-modal-box').length>0?1:0");
		assertEquals("1", getEval("jq('.z-tbeditor-modal-box :input[name=" + fieldName + "]').length"),
				"the '" + plugin + "' modal must carry its '" + fieldName + "' field");
		dismissModal();
	}

	/** Measured by position: a markup check passes on a wrapper the browser does not indent. */
	private void assertIndentMovesTheParagraph() {
		final String ed = "tb_indent";
		closeDropdowns();
		dismissModal();
		click(jq("$" + ed).find(".z-tbeditor-editor"));
		getActions().sendKeys("indent me").perform();
		waitResponse();

		int flush = firstBlockLeft(ed);
		click(button(ed, "indent"));
		waitResponse();
		int indented = firstBlockLeft(ed);
		assertTrue(indented >= flush + 10, "indent must push the paragraph right, was "
				+ flush + "px and is " + indented + "px");

		click(button(ed, "outdent"));
		waitResponse();
		int outdented = firstBlockLeft(ed);
		assertTrue(outdented <= indented - 10, "outdent must pull the paragraph back left, was "
				+ indented + "px and is " + outdented + "px");
	}

	private void assertPreformattedWrapsSelection() {
		final String ed = "tb_preformatted";
		final String word = "preformatme";
		closeDropdowns();
		dismissModal();
		assertEquals("0", preCount(ed), "the editor must start with no <pre>");

		click(jq("$" + ed).find(".z-tbeditor-editor"));
		getActions().sendKeys(word).perform();
		waitResponse();

		// arrow keys (37-40) are ignored by the editor's keyup handler, so the selection survives
		Actions select = getActions().keyDown(Keys.SHIFT);
		for (int i = 0; i < word.length(); i++)
			select = select.sendKeys(Keys.ARROW_LEFT);
		select.keyUp(Keys.SHIFT).perform();

		click(button(ed, "preformatted"));
		waitResponse();
		assertEquals("1", preCount(ed), "preformatted must wrap the selection in a <pre>");
		assertTrue(preText(ed).contains(word),
				"the <pre> must hold the selected text, saw '" + preText(ed) + "'");
	}

	/** The marker must be a property or DOM API name: the shipped plugin is uglified, so locals are renamed. */
	/** Browsers empty clipboardData once the paste event ends, so the data is cleared right after dispatching. */
	private void assertCleanpasteCleansWordHtml() {
		final String ed = "tb_cleanpaste";
		closeDropdowns();
		dismissModal();
		click(jq("$" + ed).find(".z-tbeditor-editor"));
		waitResponse();
		eval("function(){var e=jq(zk.Widget.$('$" + ed + "').$n()).find('.z-tbeditor-editor')[0],d=new DataTransfer();"
				+ "d.setData('text/html','<p class=\"MsoNormal\" style=\"color:red\"><b>Word</b> paste<o:p></o:p></p>');"
				+ "d.setData('text/plain','Word paste');"
				+ "e.dispatchEvent(new ClipboardEvent('paste',{clipboardData:d,bubbles:true,cancelable:true}));"
				+ "d.clearData();}()");
		waitResponse();
		String html = getEval("zk.Widget.$('$" + ed + "')._editor.trumbowyg('html')");
		assertTrue(html.contains("<strong>Word</strong> paste"),
				"cleanpaste must insert the pasted HTML while the paste event runs, got " + html);
		assertTrue(!html.contains("MsoNormal") && !html.contains("o:p") && !html.contains("style="),
				"cleanpaste must drop Word classes, inline styles and Word-only tags, got " + html);
	}

	/** cleanIt keeps an attribute not preceded by a space, so the HTML must be sanitized before insertHTML runs it. */
	private void assertCleanpasteDropsScript() {
		final String ed = "tb_cleanpaste";
		closeDropdowns();
		dismissModal();
		click(jq("$" + ed).find(".z-tbeditor-editor"));
		waitResponse();
		eval("function(){var e=jq(zk.Widget.$('$" + ed + "').$n()).find('.z-tbeditor-editor')[0],d=new DataTransfer();"
				+ "d.setData('text/html','<p>xss<img src=\"zk5992-none.png\"/onerror=\"window.zk5992PasteXss=1\">"
				+ "<img\\tsrc=\"zk5992-none.png\"\\tonerror=\"window.zk5992PasteXss=2\"></p>');"
				+ "e.dispatchEvent(new ClipboardEvent('paste',{clipboardData:d,bubbles:true,cancelable:true}));"
				+ "d.clearData();}()");
		waitResponse();
		// the handler of a broken image runs once its load fails, after the paste itself
		sleep(1000);
		assertEquals("undefined", getEval("String(window.zk5992PasteXss)"),
				"a handler in pasted HTML must not run");
		String html = getEval("zk.Widget.$('$" + ed + "')._editor.trumbowyg('html')");
		assertTrue(html.contains("xss") && !html.contains("onerror"),
				"cleanpaste must keep the text and drop the handler, got " + html);
	}

	private void assertPasteHandlerRegistered(String plugin, String sourceMarker) {
		assertEquals("1", pasteHandlerCount("tb_" + plugin, sourceMarker),
				"plugin '" + plugin + "' must register exactly its own paste handler"
						+ " (a function whose body mentions '" + sourceMarker + "')");
	}

	private void connectCustomPluginPage() {
		clearLibraryProperties();
		Library.setProperty(PLUGIN_PATH_PROP, CUSTOM_PLUGIN_PATH);
		connect("/test2/F111-ZK-5992-Custom.zul");
		waitResponse();
		waitForEditors(2);
	}

	private void clearLibraryProperties() {
		// Library properties are JVM-wide and outlive each test
		Library.setProperty(LIB_PROP, null);
		Library.setProperty(PLUGIN_PATH_PROP, null);
	}

	private String scriptCount(String fileName) {
		return getEval("jq('script[src*=\"" + fileName + "\"]').length");
	}

	private String linkCount(String fileName) {
		return getEval("jq('link[href*=\"" + fileName + "\"]').length");
	}

	private String resolvedPlugins(String selector) {
		return getEval("(zk.Widget.$('" + selector + "')._plugins || []).join(',')");
	}

	private String startedPlugins(String editorId) {
		return getEval("(function(){"
				+ "var t=zk.Widget.$('$" + editorId + "')._editor.data('trumbowyg'),r=jq.trumbowyg.plugins;"
				+ "return Object.keys(r).filter(function(k){return t.loadedPlugins.indexOf(r[k])>=0;})"
				+ ".sort().join(',');})()");
	}

	private String buttonCountIn(String editorId, String btnName) {
		return getEval("jq(zk.Widget.$('$" + editorId + "').$n()).find('.z-tbeditor-"
				+ btnName + "-button').length");
	}

	/** Measures the {@code <use>}, not the {@code <svg>}: CSS sizes the svg to 17px even when the symbol is missing. */
	private String iconStatus(String editorId, String btnName) {
		return getEval("(function(){"
				+ "var b=jq(zk.Widget.$('$" + editorId + "').$n()).find('.z-tbeditor-" + btnName + "-button')[0];"
				+ "if(!b)return 'no-button';"
				+ "var u=b.querySelector('svg use');"
				+ "if(!u)return 'no-svg-use';"
				+ "var h=u.getAttribute('xlink:href')||u.getAttribute('href')||'';"
				+ "var i=h.indexOf('#');"
				+ "if(i<0)return 'no-fragment:'+h;"
				+ "var id=h.substring(i+1);"
				+ "if(!document.getElementById(id))return 'missing-symbol:'+id;"
				+ "var r=u.getBoundingClientRect();"
				+ "if(!(r.width>0&&r.height>0))return 'blank-icon:'+id;"
				+ "return 'ok';})()");
	}

	private String labelStatus(String editorId, String btnName) {
		return getEval("(function(){"
				+ "var b=jq(zk.Widget.$('$" + editorId + "').$n()).find('.z-tbeditor-" + btnName + "-button')[0];"
				+ "if(!b)return 'no-button';"
				+ "if(!jq(b).hasClass('z-tbeditor-textual-button'))return 'not-textual';"
				+ "if(b.querySelector('svg'))return 'unexpected-svg';"
				+ "return (b.textContent||'').trim().length>0?'ok':'empty-label';})()");
	}

	/** {@code .z-tbeditor-editor} appears only once the editor is built, i.e. after its plugins loaded. */
	private void waitForEditors(int expected) {
		String expr = "jq('.z-tbeditor-editor').length";
		String last = "?";
		for (int i = 0; i < 50; i++) {
			last = getEval(expr);
			if (String.valueOf(expected).equals(last))
				return;
			sleep(100);
		}
		fail("expected " + expected + " editors after 5s, found " + last);
	}

	private void waitForIconSprite() {
		String expr = "jq('#z-tbeditor-icons symbol').length > 0 ? 1 : 0";
		for (int i = 0; i < 50; i++) {
			if ("1".equals(getEval(expr)))
				return;
			sleep(100);
		}
		fail("icons.svg sprite was not injected into #z-tbeditor-icons after 5s");
	}

	private void waitForWidget(String selector) {
		String expr = "zk.Widget.$('" + selector + "') ? 1 : 0";
		String last = "?";
		for (int i = 0; i < 50; i++) {
			last = getEval(expr);
			if ("1".equals(last)) return;
			sleep(100);
		}
		fail("widget " + selector + " not present after 5s");
	}

	private void openDropdown(String editorId, String btnName) {
		closeDropdowns();
		click(button(editorId, btnName));
		waitResponse();
		waitFor("the '" + btnName + "' dropdown of " + editorId + " to open",
				editorScope(editorId) + ".find('" + dropdownSelector(btnName) + ":visible').length>0?1:0");
	}

	private void closeDropdowns() {
		eval("function(){jq('.z-tbeditor-dropdown').hide();"
				+ "jq('.z-tbeditor-button-pane .z-tbeditor-active').removeClass('z-tbeditor-active');}()");
	}

	/** Modals are appended to {@code <body>}, so one left open blocks the next case's clicks. */
	private void dismissModal() {
		if ("0".equals(getEval("jq('.z-tbeditor-modal-box').length")))
			return;
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitFor("the open modal to close", "jq('.z-tbeditor-modal-box').length===0?1:0");
	}

	/** Aspect ratio is not checked: the table picker's cells shrink to fit. */
	private String dropdownLayout(String editorId, String btnName, String childSelector) {
		return getEval("(function(){"
				+ "var dd=" + editorScope(editorId) + ".find('" + dropdownSelector(btnName) + "')[0];"
				+ "if(!dd)return 'no-dropdown';"
				+ "if(dd.getBoundingClientRect().height<=0)return 'dropdown-hidden';"
				+ "var k=[].slice.call(dd.querySelectorAll('" + childSelector + "')).filter(function(e){"
				+ "var r=e.getBoundingClientRect();return r.width>0&&r.height>0;});"
				+ "if(k.length<2)return 'fewer-than-2-visible-children:'+k.length;"
				+ "var a=k[0].getBoundingClientRect(),b=k[1].getBoundingClientRect(),"
				+ "sameRow=Math.abs(a.top-b.top)<=1,"
				+ "swatch=a.width>=15&&a.width<=30&&a.height>=15&&a.height<=30;"
				+ "if(sameRow&&swatch)return 'grid';"
				+ "return 'not-grid[sameRow='+sameRow+',w='+Math.round(a.width)+',h='+Math.round(a.height)"
				+ "+',rowDelta='+Math.round(b.top-a.top)+']';})()");
	}

	private String setStylesheetDisabled(String cssFileName, boolean disabled) {
		return getEval("(function(){"
				+ "var l=document.querySelector('link[href*=\"" + cssFileName + "\"]');"
				+ "if(!l)return 'no-link';"
				+ "l.disabled=" + disabled + ";"
				// force a style recalc so the next measurement sees the change
				+ "document.body.getBoundingClientRect();"
				+ "return String(l.disabled);})()");
	}

	private String dropdownEntryCount(String editorId, String btnName) {
		return getEval(editorScope(editorId) + ".find('" + dropdownSelector(btnName) + " button').length");
	}

	private String dropdownText(String editorId, String btnName) {
		return getEval(editorScope(editorId) + ".find('" + dropdownSelector(btnName) + "').text()");
	}

	private String isDisabled(String editorId, String btnName) {
		return getEval("String(" + editorScope(editorId) + ".find('.z-tbeditor-" + btnName
				+ "-button').hasClass('z-tbeditor-disable'))");
	}

	private String disabledFlag(String editorId, String btnName) {
		return editorScope(editorId) + ".find('.z-tbeditor-" + btnName
				+ "-button').hasClass('z-tbeditor-disable')?1:0";
	}

	private String enabledFlag(String editorId, String btnName) {
		return editorScope(editorId) + ".find('.z-tbeditor-" + btnName
				+ "-button').hasClass('z-tbeditor-disable')?0:1";
	}

	private String editorText(String editorId) {
		return getEval(editorScope(editorId) + ".find('.z-tbeditor-editor').text()");
	}

	private String preCount(String editorId) {
		return getEval(editorScope(editorId) + ".find('.z-tbeditor-editor pre').length");
	}

	private String preText(String editorId) {
		return getEval(editorScope(editorId) + ".find('.z-tbeditor-editor pre').text()");
	}

	private int firstBlockLeft(String editorId) {
		return parseInt(getEval("(function(){"
				+ "var ed=" + editorScope(editorId) + ".find('.z-tbeditor-editor')[0];"
				+ "var n=ed.firstElementChild||ed;"
				+ "return String(Math.round(n.getBoundingClientRect().left));})()"));
	}

	private String pasteHandlerCount(String editorId, String sourceMarker) {
		return getEval("(function(){"
				+ "var w=zk.Widget.$('$" + editorId + "'),"
				+ "t=w&&w._editor&&w._editor.data('trumbowyg');"
				+ "if(!t)return 'no-editor-instance';"
				+ "return String(t.pasteHandlers.filter(function(h){"
				+ "return String(h).indexOf('" + sourceMarker + "')>=0;}).length);})()");
	}

	private JQuery button(String editorId, String btnName) {
		return jq("$" + editorId).find(".z-tbeditor-" + btnName + "-button");
	}

	private String editorScope(String editorId) {
		return "jq(zk.Widget.$('$" + editorId + "').$n())";
	}

	private String dropdownSelector(String btnName) {
		return "[data-z-tbeditor-dropdown=" + btnName + "]";
	}

	private void waitFor(String what, String flagExpression) {
		for (int i = 0; i < 50; i++) {
			if ("1".equals(getEval(flagExpression)))
				return;
			sleep(100);
		}
		fail("timed out after 5s waiting for " + what);
	}
}
