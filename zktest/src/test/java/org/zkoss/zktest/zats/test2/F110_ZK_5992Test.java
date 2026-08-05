/* F110_ZK_5992Test.java

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Verifies the ZK-5992 acceptance criteria for {@code <tbeditor/>}'s pluggable
 * Trumbowyg plugin loading.
 *
 * <p>The contract observable to a ZK app developer is "did my plugin's script
 * file get loaded for this editor"; we assert via {@code <script>} tags rather
 * than upstream-internal {@code jq.trumbowyg.plugins[key]} entries because
 * Trumbowyg uses inconsistent keys (e.g. {@code colors} file →
 * {@code color} key, {@code indent} file → {@code paragraph} key) that
 * shouldn't leak into the test contract.
 *
 * <p>Tests covered:</p>
 * <ul>
 *   <li>AC1 — per-component {@code plugins=} attribute.</li>
 *   <li>AC2 — library property {@code org.zkoss.zkmax.zul.tbeditor.plugins}.</li>
 *   <li>AC3 — closest-scope override: a component / custom-attribute value
 *       replaces (not merges with) broader scopes; an explicit empty value clears.</li>
 *   <li>AC4 — inherited custom-attribute walks up the component tree.</li>
 *   <li>AC5 — unknown plugin name is tolerated (page still renders).</li>
 *   <li>AC6 — multiple editors requesting the same plugin trigger one network load.</li>
 *   <li>AC7 — library property {@code …tbeditor.pluginPath} overrides the asset URL prefix.</li>
 *   <li>AC8 — a bare editor on a page with no plugin config makes zero requests
 *       under {@code /ext/plugins/}.</li>
 *   <li>AC9 — every plugin in the bundled manifest can be loaded individually,
 *       including CSS where the manifest declares one.</li>
 *   <li>Auto-buttons (usage surface) — each button-contributing plugin auto-inserts
 *       its toolbar button into its editor ({@link #eachPluginAddsToolbarButton()});
 *       {@code config.autoPluginButtons=false} suppresses it while the plugin still
 *       loads ({@link #autoPluginButtonsCanBeDisabled()}).</li>
 *   <li>Icon integrity — an auto-inserted button actually paints: its icon resolves
 *       to a symbol in {@code ext/icons.svg}, or (for the textual dropdowns) it shows
 *       its label ({@link #pluginButtonsRenderIconOrLabel()}).</li>
 *   <li>CSS integrity — the four plugins that ship a stylesheet lay their dropdown
 *       out as a grid, which is only true once the vendored CSS was re-prefixed from
 *       {@code .trumbowyg-} to {@code .z-tbeditor-}
 *       ({@link #cssPluginDropdownsAreLaidOutAsGrids()}).</li>
 *   <li>History — Undo / Redo grey out and light up through the core's own
 *       {@code prefix + 'disable'} class, and really move through the stack
 *       ({@link #historyButtonsGreyOutAndActuallyUndo()}).</li>
 *   <li>Per-plugin behaviour — every remaining plugin performs its own action
 *       ({@link #eachPluginPerformsItsAction()}).</li>
 * </ul>
 */
public class F110_ZK_5992Test extends WebDriverTestCase {

	private static final String LIB_PROP = "org.zkoss.zkmax.zul.tbeditor.plugins";
	private static final String PLUGIN_PATH_PROP = "org.zkoss.zkmax.zul.tbeditor.pluginPath";

	/**
	 * Names of every plugin shipped in
	 * {@code zkmax/.../tbeditor/ext/plugins/manifest.json}.
	 * Keep in alphabetical order; keep in sync with the manifest and with the
	 * editors in {@code F110-ZK-5992-AllPlugins.zul}.
	 */
	private static final List<String> ALL_PLUGINS = Arrays.asList(
			"base64", "cleanpaste", "colors", "emoji",
			"fontfamily", "fontsize", "history", "indent",
			"insertaudio", "lineheight", "pasteimage", "preformatted",
			"ruby", "specialchars", "table", "template"
	);

	/**
	 * Subset of {@link #ALL_PLUGINS} whose manifest entry declares a CSS file.
	 * Each one's stylesheet is loaded via {@code zk.loadCSS} alongside its JS,
	 * registered under the cache key {@code tbeditor-plugin-<name>}.
	 */
	private static final List<String> PLUGINS_WITH_CSS = Arrays.asList(
			"colors", "emoji", "specialchars", "table"
	);

	/**
	 * The toolbar button(s) each plugin contributes, keyed by plugin name and
	 * mirroring the {@code btns} field of {@code ext/plugins/manifest.json} (and
	 * {@code TBEDITOR_PLUGIN_MANIFEST} in {@code Tbeditor.ts}). These are the
	 * Trumbowyg button names; the rendered DOM class is {@code z-tbeditor-<btn>-button}.
	 * Plugins absent here are behaviour-only (paste/drag handlers) and add no button —
	 * their usability is covered by {@link #eachPluginLoads()} (script loaded and
	 * registered into {@code jq.trumbowyg.plugins}).
	 */
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
		// 'template' declares shouldInit = o.plugins.hasOwnProperty('templates'), so it
		// only registers its button when the app supplies a 'templates' config — which
		// F110-ZK-5992-AllPlugins.zul does for tb_template.
		PLUGIN_BUTTONS.put("template", Arrays.asList("template"));
	}

	/**
	 * The toolbar buttons that are deliberately textual (Trumbowyg
	 * {@code hasIcon: false}) because ZK ships no icon for them. Every other
	 * button in {@link #PLUGIN_BUTTONS} must paint an icon; keeping the two
	 * categories in one list is what lets
	 * {@link #pluginButtonsRenderIconOrLabel()} pin them apart.
	 */
	private static final List<String> TEXTUAL_BUTTONS = Arrays.asList(
			"fontfamily", "fontsize", "lineheight", "template"
	);

	/** Where {@link #cssPluginDropdownsAreLaidOutAsGrids()} drops its diagnostic PNGs. */
	private static final String SHOT_DIR = "build/zk5992-shots";

	/**
	 * The four plugins whose manifest declares a stylesheet, mapped to the toolbar
	 * button that opens their dropdown, the child element whose geometry proves the
	 * stylesheet applied, and the stylesheet's file name.
	 */
	private static final Map<String, String[]> CSS_PLUGIN_DROPDOWNS = new LinkedHashMap<>();
	static {
		CSS_PLUGIN_DROPDOWNS.put("colors", new String[] { "foreColor", "button", "trumbowyg.colors.css" });
		CSS_PLUGIN_DROPDOWNS.put("emoji", new String[] { "emoji", "button", "trumbowyg.emoji.css" });
		CSS_PLUGIN_DROPDOWNS.put("specialchars",
				new String[] { "specialChars", "button", "trumbowyg.specialchars.css" });
		CSS_PLUGIN_DROPDOWNS.put("table", new String[] { "table", "td", "trumbowyg.table.css" });
	}

	/** Plugins whose whole contribution is a dropdown, keyed to their button name. */
	private static final Map<String, String> DROPDOWN_PLUGINS = new LinkedHashMap<>();
	static {
		DROPDOWN_PLUGINS.put("fontfamily", "fontfamily");
		DROPDOWN_PLUGINS.put("fontsize", "fontsize");
		DROPDOWN_PLUGINS.put("lineheight", "lineheight");
		DROPDOWN_PLUGINS.put("template", "template");
	}

	/**
	 * Plugins that open a modal, mapped to their toolbar button and the input name
	 * their own source declares.
	 */
	private static final Map<String, String[]> MODAL_PLUGINS = new LinkedHashMap<>();
	static {
		MODAL_PLUGINS.put("base64", new String[] { "base64", "file" });
		MODAL_PLUGINS.put("insertaudio", new String[] { "insertAudio", "src" });
		MODAL_PLUGINS.put("ruby", new String[] { "ruby", "rubyText" });
	}

	/** AC1, AC3 (override + clear), AC4, AC5 (unknown plugin), AC6 (dedupe). */
	@Test
	public void perComponentAndInheritance() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992.zul");
		waitResponse();

		// Wait for all 5 editors on the page to finish wiring up. _initEditor
		// runs after Promise.all of plugin loads in Tbeditor.ts, so seeing
		// .z-tbeditor-editor proves their plugin scripts finished loading.
		waitForEditors(5);

		// AC1 — tbColors requested colors+fontsize: both scripts injected once.
		assertEquals("1", scriptCount("trumbowyg.colors.js"),
				"per-component plugins= must load colors plugin script");
		assertEquals("1", scriptCount("trumbowyg.fontsize.js"),
				"per-component plugins= must load fontsize plugin script");

		// AC4 — tbInherited (no plugins= attribute) inherited "emoji" from the
		// surrounding <window>'s custom-attribute, so emoji.js loaded.
		assertEquals("1", scriptCount("trumbowyg.emoji.js"),
				"inherited custom-attribute must load emoji plugin");
		assertEquals("emoji", resolvedPlugins("$tbInherited"),
				"an editor with no plugins= inherits the nearest custom-attribute");

		// AC3 (override) — tbOverride's own plugins="colors" REPLACES the inherited
		// 'emoji' (no merge): it resolves to colors only. (emoji.js above is still 1
		// because tbInherited loads it; tbOverride does not add it.)
		assertEquals("colors", resolvedPlugins("$tbOverride"),
				"component plugins= must override (not merge with) the inherited custom-attribute");

		// AC3 (override to none) — tbClear's explicit plugins="" clears the inherited
		// 'emoji' for that editor: it resolves to no plugins (nothing rendered, so the
		// widget's _plugins is empty/unset).
		assertEquals("", resolvedPlugins("$tbClear"),
				"explicit empty plugins= must clear inherited plugins (override to none)");

		// AC5 — unknown plugin produced a 404'd request but did not break
		// anything. The editor count of 5 above already confirms all
		// instances rendered. The failing <script> element is removed by the
		// loader's onerror so a future retry can re-try; assert that here.
		assertEquals("0", scriptCount("trumbowyg.z_does_not_exist.js"),
				"failed plugin script element should not linger in <head>");

		// AC6 — colors is requested by both tbColors (own attr) and tbOverride.
		// Dedupe means just one script tag, which the AC1 assertion above pins to 1.
	}

	/** AC2 — library property selects plugins app-wide. */
	@Test
	public void libraryProperty() {
		Library.setProperty(LIB_PROP, "table");
		try {
			connect("/test2/F110-ZK-5992-Lib.zul");
			waitResponse();
			waitForEditors(1);
			assertEquals("1", scriptCount("trumbowyg.table.js"),
					"library-property plugin must load for the bare editor");
		} finally {
			clearLibraryProperties();
		}
	}

	/** AC3 (override) — a component value REPLACES the library property; it does
	 * not merge with it. */
	@Test
	public void overrideLibraryProperty() {
		Library.setProperty(LIB_PROP, "table");
		try {
			connect("/test2/F110-ZK-5992.zul");
			waitResponse();
			waitForEditors(5);

			// Every editor on this page sets a closer scope (component plugins= or
			// the window's custom-attribute), so the library property "table" is
			// overridden everywhere — its script is never loaded.
			assertEquals("0", scriptCount("trumbowyg.table.js"),
					"a closer scope must override (not merge with) the library property");

			// tbColors resolves to exactly its own plugins, with no "table" merged in.
			assertEquals("colors,fontsize", resolvedPlugins("$tbColors"),
					"component plugins= must fully replace the library property");
		} finally {
			clearLibraryProperties();
		}
	}

	/**
	 * AC7 — {@value #PLUGIN_PATH_PROP} library property overrides the URL
	 * prefix used for plugin assets. The widget's {@code _pluginPath} field
	 * is the most reliable observation point: the actual {@code <script>}
	 * tag gets removed by the loader's {@code onerror} when the custom URL
	 * 404s, which would race with this assertion.
	 */
	@Test
	public void pluginPathOverride() {
		final String customPath = "/web/js/zkmax/tbeditor/ext/plugins-override/";
		Library.setProperty(LIB_PROP, "base64");
		Library.setProperty(PLUGIN_PATH_PROP, customPath);
		try {
			connect("/test2/F110-ZK-5992-Lib.zul");
			waitResponse();

			// Don't wait for the editor to wire up — the script will 404 and
			// the editor still initialises, but the order is racy. The
			// widget receives _pluginPath synchronously at bind time, so we
			// can read it as soon as the widget exists.
			waitForWidget("$tb");

			String pluginPath = getEval("zk.Widget.$('$tb')._pluginPath");
			assertEquals(customPath, pluginPath,
					"widget should receive the custom pluginPath verbatim");

			// And no plugin script should have been requested from the
			// default location — the override is honoured, not appended.
			assertEquals("0",
					getEval("jq('script[src*=\"/ext/plugins/trumbowyg.\"]').length"),
					"default plugin path must not be used when override is set");
		} finally {
			clearLibraryProperties();
		}
	}

	/** AC8 — no plugin requests at all for a bare page when library prop is unset. */
	@Test
	public void noPluginsNoRequests() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-Lib.zul");
		waitResponse();
		waitForEditors(1);

		// No <script> tag pointing at the plugins directory should exist.
		assertEquals("0",
				getEval("jq('script[src*=\"/ext/plugins/\"]').length"),
				"a bare editor with no configured plugins must not request any plugin assets");
	}

	/**
	 * AC9 — every plugin shipped in the manifest is loadable individually. Emitted
	 * as one dynamic test <em>per plugin</em> (16 cases) plus a registry check, so
	 * each plugin is reported and passes/fails on its own — one broken plugin no
	 * longer masks the rest (the loop's fast-fail did). The page (one editor per
	 * plugin) loads once; each case asserts that plugin's JS loaded exactly once and
	 * its CSS presence matches its manifest entry.
	 *
	 * <p>This also catches the most common drift: a plugin directory + JSON entry
	 * added but not mirrored into {@code TBEDITOR_PLUGIN_MANIFEST} in
	 * {@code Tbeditor.ts}, or vice versa.</p>
	 */
	@TestFactory
	Stream<DynamicTest> eachPluginLoads() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		Stream<DynamicTest> perPlugin = ALL_PLUGINS.stream().map(name -> DynamicTest.dynamicTest(
				"loads:" + name, () -> {
					assertEquals("1", scriptCount("trumbowyg." + name + ".js"),
							"plugin '" + name + "' must load its js exactly once");
					// CSS must appear iff the manifest declares one (guards against an
					// over-eager loader that probes every directory).
					String expectedCss = PLUGINS_WITH_CSS.contains(name) ? "1" : "0";
					assertEquals(expectedCss, linkCount("trumbowyg." + name + ".css"),
							"plugin '" + name + "' css presence must match its manifest entry");
				}));

		// One more case: Trumbowyg actually registered every plugin (the script
		// could load but registration silently fail, leaving the plugin inert).
		DynamicTest registry = DynamicTest.dynamicTest("registry-populated", () -> {
			int registered = Integer.parseInt(getEval("Object.keys(jq.trumbowyg.plugins).length"));
			assertTrue(registered >= ALL_PLUGINS.size(),
					"jq.trumbowyg.plugins should hold an entry for every loaded plugin, saw "
							+ registered + " for " + ALL_PLUGINS.size() + " plugins");
		});

		// No client-side JS errors on a page of only valid plugins. This is the case
		// that catches a broken asset URL (e.g. the ";jsessionid=" path bug where every
		// plugin/CSS request 404'd) — a malformed URL surfaces here as a console error.
		DynamicTest noError = DynamicTest.dynamicTest("no-console-errors", this::assertNoJSError);

		return Stream.concat(perPlugin, Stream.of(registry, noError));
	}

	/**
	 * Auto-buttons + per-plugin usage surface — each button-contributing plugin must
	 * auto-insert its toolbar button(s) into <em>its own</em> editor (the observable
	 * "the plugin is usable" contract for ZK). Emitted as one dynamic test per plugin
	 * (14 cases) so each plugin's usability is reported independently. Behaviour-only
	 * plugins add no button and are covered by {@link #eachPluginLoads()}; that the
	 * button is not blank is covered by {@link #pluginButtonsRenderIconOrLabel()}.
	 * Deeper per-plugin behaviour is Trumbowyg's own concern.
	 *
	 * <p>Reuses {@code F110-ZK-5992-AllPlugins.zul} (one editor per plugin), so each
	 * button is asserted scoped to that plugin's editor — proving it landed in the
	 * right toolbar, not merely somewhere on the page.</p>
	 */
	@TestFactory
	Stream<DynamicTest> eachPluginAddsToolbarButton() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-AllPlugins.zul");
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

	/**
	 * Icon integrity — the case {@link #eachPluginAddsToolbarButton()} cannot make.
	 * A button whose {@code <use>} points at a symbol that is not in
	 * {@code ext/icons.svg} still renders a {@code <button>} holding a 17px-wide
	 * {@code <svg>}, so a presence check stays green while the toolbar shows a blank
	 * box. Each icon-bearing button is therefore measured at the {@code <use>}
	 * element: its href fragment must resolve to a symbol in the injected sprite, and
	 * its client rect must be non-zero. Measuring the outer {@code <svg>} instead
	 * would be worthless — {@code .z-tbeditor-box svg} is CSS-sized to 17px wide and
	 * reports that size whether or not the reference resolves; the {@code <use>}
	 * measures 0x0 both for a missing symbol and for one that exists but holds no
	 * shapes.
	 *
	 * <p>The textual buttons ({@link #TEXTUAL_BUTTONS}) are asserted in the same run
	 * so the two categories are pinned apart: they must carry no {@code <svg>} at all
	 * and show their label text.</p>
	 */
	@TestFactory
	Stream<DynamicTest> pluginButtonsRenderIconOrLabel() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-AllPlugins.zul");
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

	/**
	 * Auto-buttons opt-out — {@code config.autoPluginButtons=false} must suppress
	 * the auto-inserted button while the plugin itself still loads (the page sets
	 * the flag on one of two table editors). Proves the feature is opt-out-able and
	 * that loading and button-insertion are independent.
	 */
	@Test
	public void autoPluginButtonsCanBeDisabled() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-Buttons.zul");
		waitResponse();
		waitForEditors(2);

		// Both editors enable the table plugin; its script loads once (deduped).
		assertEquals("1", scriptCount("trumbowyg.table.js"),
				"both editors request the table plugin (deduped to one load)");

		// Default (auto on): the table button is inserted.
		assertEquals("1", buttonCountIn("tbAuto", "table"),
				"auto-buttons on: the table toolbar button must appear");

		// Opt-out: plugin loaded, but no button inserted.
		assertEquals("0", buttonCountIn("tbNoAuto", "table"),
				"autoPluginButtons=false must suppress the auto-inserted table button");

		// The valid table plugin's assets must load without client errors.
		assertNoJSError();
	}


	/**
	 * CSS integrity — the four plugins that ship a stylesheet. Their vendored CSS
	 * targeted upstream's {@code .trumbowyg-*} classes, which this runtime never
	 * emits (Tbeditor forces {@code config.prefix = 'z-tbeditor-'}), so those rules
	 * matched nothing: the dropdowns still opened and every existence / icon
	 * assertion above still passed while the swatch grids rendered as a column of
	 * full-width 35px rows. The selectors were rewritten to {@code .z-tbeditor-*};
	 * this is the case that notices if that rewrite is undone or re-vendored away.
	 *
	 * <p>Each case opens the plugin's dropdown and measures its first two visible
	 * swatch / cell children: they must share a row and be small squares (15–30px,
	 * width == height). Only the plugin stylesheet produces that — the core rule
	 * {@code .z-tbeditor-dropdown button} is {@code display:block; width:100%;
	 * height:35px}, and the table plugin's cells collapse to 0x0 without theirs.
	 * The same measurement is then repeated with the plugin's {@code <link>}
	 * switched off and must <em>not</em> report a grid, so the assertion is proven
	 * to discriminate rather than merely being green.</p>
	 *
	 * <p>A PNG of each open dropdown is written to {@value #SHOT_DIR} as a
	 * diagnostic; the case fails on the geometry, never on the image.</p>
	 */
	@TestFactory
	Stream<DynamicTest> cssPluginDropdownsAreLaidOutAsGrids() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		return CSS_PLUGIN_DROPDOWNS.entrySet().stream().map(e -> DynamicTest.dynamicTest(
				"layout:" + e.getKey(), () -> {
					String plugin = e.getKey(), btn = e.getValue()[0],
							child = e.getValue()[1], css = e.getValue()[2];
					String editorId = "tb_" + plugin;

					openDropdown(editorId, btn);
					String styled = dropdownLayout(editorId, btn, child);
					savePluginShot(plugin);
					assertEquals("grid", styled, "plugin '" + plugin + "' must lay its dropdown "
							+ child + "s out as a grid of small squares, which only happens when "
							+ css + " applies to the z-tbeditor- prefix");

					// Teeth check — the very same measurement with the plugin
					// stylesheet switched off must NOT report a grid. Without this
					// the assertion above could be green for a stylesheet that
					// never matched a single element.
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

	/**
	 * The {@code history} disable-class fix. {@code trumbowyg.history.js} hardcoded
	 * {@code trumbowyg-disable} while the core and {@code tbeditor.less} use
	 * {@code prefix + 'disable'}, so Undo / Redo never greyed out — the class was
	 * being written onto the button under a name nothing styles. The class is now
	 * built from {@code t.o.prefix}.
	 *
	 * <p>Asserted through the class the core itself uses <em>and</em> through the
	 * behaviour behind it (a disabled Undo changes nothing; an enabled one really
	 * reverts the text and lights up Redo), so renaming a class cannot make this
	 * pass vacuously.</p>
	 */
	@Test
	public void historyButtonsGreyOutAndActuallyUndo() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-AllPlugins.zul");
		waitResponse();
		waitForEditors(ALL_PLUGINS.size());

		final String ed = "tb_history";
		assertEquals("1", buttonCountIn(ed, "historyUndo"), "the Undo button must exist to be tested");
		assertEquals("1", buttonCountIn(ed, "historyRedo"), "the Redo button must exist to be tested");

		// Fresh editor: one state on the stack, so neither direction is available.
		waitFor("Undo of " + ed + " to be marked disabled", disabledFlag(ed, "historyUndo"));
		assertEquals("true", isDisabled(ed, "historyUndo"), "a fresh editor has nothing to undo");
		assertEquals("true", isDisabled(ed, "historyRedo"), "a fresh editor has nothing to redo");

		// …and it is inert, not merely styled: clicking it must change nothing.
		String pristine = editorText(ed);
		click(button(ed, "historyUndo"));
		waitResponse();
		assertEquals(pristine, editorText(ed), "a disabled Undo must not alter the content");

		// Typing pushes a state: Undo becomes available.
		click(jq("$" + ed).find(".z-tbeditor-editor"));
		getActions().sendKeys("ZK5992undo").perform();
		waitResponse();
		waitFor("Undo of " + ed + " to become enabled", enabledFlag(ed, "historyUndo"));
		String typed = editorText(ed);
		assertTrue(typed.contains("ZK5992undo"), "typing must reach the editor, saw '" + typed + "'");

		// Undoing reverts the text and lights up Redo.
		click(button(ed, "historyUndo"));
		waitResponse();
		waitFor("Redo of " + ed + " to become enabled", enabledFlag(ed, "historyRedo"));
		assertNotEquals(typed, editorText(ed), "Undo must revert the typed text");

		// Walking the stack back to the bottom re-disables Undo and restores the
		// original content — the round trip, not just one hop.
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

	/**
	 * The remaining eleven plugins — one case each asserting the plugin <em>does
	 * its job</em>, not that its button exists (that is
	 * {@link #eachPluginAddsToolbarButton()}). Derived from each plugin's own
	 * source: dropdown plugins must offer entries, modal plugins must open a modal
	 * carrying their field, content plugins must change the editor's markup, and
	 * the two button-less paste plugins must have pushed <em>their</em> handler
	 * onto the instance's {@code pasteHandlers}.
	 *
	 * <p>All cases share one page load and each starts by dismissing whatever the
	 * previous one left open, so a failure does not cascade.</p>
	 */
	@TestFactory
	Stream<DynamicTest> eachPluginPerformsItsAction() {
		clearLibraryProperties();
		connect("/test2/F110-ZK-5992-AllPlugins.zul");
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
		cases.add(DynamicTest.dynamicTest("acts:cleanpaste",
				() -> assertPasteHandlerRegistered("cleanpaste", "saveRange")));
		cases.add(DynamicTest.dynamicTest("acts:pasteimage",
				() -> assertPasteHandlerRegistered("pasteimage", "readAsDataURL")));
		return cases.stream();
	}

	/**
	 * A dropdown plugin is usable when opening its button yields entries. The
	 * {@code template} plugin additionally has to show the page's own "Greeting"
	 * template, proving it read {@code config.plugins.templates} rather than
	 * rendering an empty shell.
	 */
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

	/**
	 * A modal plugin is usable when its button opens the Trumbowyg modal carrying
	 * the field its own source declares (base64 → {@code file}, insertaudio →
	 * {@code src}, ruby → {@code rubyText}).
	 */
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

	/**
	 * {@code indent} / {@code outdent} are measured where the user sees them: the
	 * paragraph's left edge moves right, then back. A markup check would pass on a
	 * wrapper the browser inserts but does not visually indent.
	 */
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

	/**
	 * {@code preformatted} only acts on a non-empty selection (its {@code fn}
	 * bails on {@code getRangeText()} being blank), so the word is selected with
	 * shift+arrow rather than faked — then the editor must hold a {@code <pre>}
	 * that did not exist before.
	 */
	private void assertPreformattedWrapsSelection() {
		final String ed = "tb_preformatted";
		final String word = "preformatme";
		closeDropdowns();
		dismissModal();
		assertEquals("0", preCount(ed), "the editor must start with no <pre>");

		click(jq("$" + ed).find(".z-tbeditor-editor"));
		getActions().sendKeys(word).perform();
		waitResponse();

		// Select the word backwards from the caret. Arrow keys are ignored by the
		// editor's keyup handler (37–40), so the selection survives to the click.
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

	/**
	 * {@code cleanpaste} and {@code pasteimage} contribute no button — they push a
	 * handler onto the instance's {@code pasteHandlers}. Matched on a token unique
	 * to each plugin's own handler body so "some handler exists" cannot stand in
	 * for "this plugin's handler exists"; synthesising a clipboard event would
	 * test the browser, not the plugin wiring.
	 *
	 * <p>The token must be a property or DOM API name, never a local identifier:
	 * the shipped plugin is uglified, which renames locals away. Matching on
	 * cleanpaste's own {@code cleanIt} function passed against the unminified
	 * source and failed against the build output.</p>
	 */
	private void assertPasteHandlerRegistered(String plugin, String sourceMarker) {
		assertEquals("1", pasteHandlerCount("tb_" + plugin, sourceMarker),
				"plugin '" + plugin + "' must register exactly its own paste handler"
						+ " (a function whose body mentions '" + sourceMarker + "')");
	}

	private void clearLibraryProperties() {
		// Ensure no leakage from a previously-run test in the same JVM.
		Library.setProperty(LIB_PROP, null);
		Library.setProperty(PLUGIN_PATH_PROP, null);
	}

	private String scriptCount(String fileName) {
		return getEval("jq('script[src*=\"" + fileName + "\"]').length");
	}

	private String linkCount(String fileName) {
		return getEval("jq('link[href*=\"" + fileName + "\"]').length");
	}

	/**
	 * The server-resolved plugin list the widget received (its {@code _plugins}),
	 * comma-joined. This is {@code Tbeditor.getPlugins()}'s result pushed to the
	 * client, so it is the precise observation point for scope-override: it shows
	 * the single winning level, not a merge. An editor that resolved to no plugins
	 * has no {@code _plugins}, which reads as the empty string.
	 */
	private String resolvedPlugins(String selector) {
		return getEval("(zk.Widget.$('" + selector + "')._plugins || []).join(',')");
	}

	/**
	 * Counts the Trumbowyg toolbar buttons of the given name inside the named
	 * editor (by widget id). The rendered button class is
	 * {@code z-tbeditor-<btnName>-button} (Trumbowyg's {@code prefix + name +
	 * '-button'}, with the prefix defaulted to {@code z-tbeditor-}).
	 */
	private String buttonCountIn(String editorId, String btnName) {
		return getEval("jq(zk.Widget.$('$" + editorId + "').$n()).find('.z-tbeditor-"
				+ btnName + "-button').length");
	}

	/**
	 * Diagnoses one icon-bearing toolbar button, returning {@code "ok"} or a reason
	 * naming what is wrong. Reads the {@code <use>}'s href fragment, checks the
	 * injected sprite really holds that symbol, then measures the {@code <use>}:
	 * a 0x0 rect means nothing was painted.
	 */
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

	/**
	 * Diagnoses one textual (Trumbowyg {@code hasIcon: false}) toolbar button,
	 * returning {@code "ok"} or a reason: it must be marked textual, carry no
	 * {@code <svg>} at all, and show non-empty label text.
	 */
	private String labelStatus(String editorId, String btnName) {
		return getEval("(function(){"
				+ "var b=jq(zk.Widget.$('$" + editorId + "').$n()).find('.z-tbeditor-" + btnName + "-button')[0];"
				+ "if(!b)return 'no-button';"
				+ "if(!jq(b).hasClass('z-tbeditor-textual-button'))return 'not-textual';"
				+ "if(b.querySelector('svg'))return 'unexpected-svg';"
				+ "return (b.textContent||'').trim().length>0?'ok':'empty-label';})()");
	}

	/**
	 * Polls until the page has the expected number of fully-initialised
	 * tbeditor instances — i.e. each one has had {@code .trumbowyg(opts)}
	 * called on it, which only happens after every requested plugin's
	 * script has loaded. The selector {@code .z-tbeditor-editor} is the
	 * inner contenteditable created by the trumbowyg core.
	 */
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

	/**
	 * Polls until the named widget exists. Used by tests that observe widget
	 * state set at bind time (e.g. {@code _pluginPath}) without waiting for
	 * the underlying editor to wire up — useful when the editor's wire-up
	 * is intentionally allowed to fail.
	 */
	/**
	 * Polls until {@code ext/icons.svg} has been fetched and inlined into the
	 * hidden {@code #z-tbeditor-icons} sprite container. Trumbowyg injects it
	 * asynchronously, so an icon assertion made too early would fail on timing
	 * rather than on a genuinely missing symbol.
	 */
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

	/** Opens the named dropdown of the named editor, after closing any other. */
	private void openDropdown(String editorId, String btnName) {
		closeDropdowns();
		click(button(editorId, btnName));
		waitResponse();
		waitFor("the '" + btnName + "' dropdown of " + editorId + " to open",
				editorScope(editorId) + ".find('" + dropdownSelector(btnName) + ":visible').length>0?1:0");
	}

	/** Hides every open dropdown on the page and clears the buttons' active state. */
	private void closeDropdowns() {
		eval("function(){jq('.z-tbeditor-dropdown').hide();"
				+ "jq('.z-tbeditor-button-pane .z-tbeditor-active').removeClass('z-tbeditor-active');}()");
	}

	/**
	 * Dismisses the Trumbowyg modal with ESC (the core binds the key on the
	 * document body) if one is open. Modals are appended to {@code <body>}, not to
	 * the editor, so one left open would block the next case's clicks.
	 */
	private void dismissModal() {
		if ("0".equals(getEval("jq('.z-tbeditor-modal-box').length")))
			return;
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitFor("the open modal to close", "jq('.z-tbeditor-modal-box').length===0?1:0");
	}

	/**
	 * Classifies the layout of an open dropdown's children, returning
	 * {@code "grid"} or a reason carrying the measurements. A grid means the first
	 * two visible children share a row and are small boxes — the shape the plugin
	 * stylesheets produce. The core's own rule gives full-width 35px rows instead,
	 * and an unstyled table cell collapses away entirely, so neither can be
	 * mistaken for a grid. Aspect ratio is not asserted: the table picker's cells
	 * are shrink-to-fit, so their width tracks the room left of the button.
	 */
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

	/**
	 * Switches one plugin stylesheet off / on at runtime and returns the resulting
	 * {@code link.disabled}, so a caller can tell a real toggle from a missing
	 * {@code <link>}. Used to prove {@link #dropdownLayout} discriminates.
	 */
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

	/** Left edge of the editor's first block element, rounded to whole pixels. */
	private int firstBlockLeft(String editorId) {
		return parseInt(getEval("(function(){"
				+ "var ed=" + editorScope(editorId) + ".find('.z-tbeditor-editor')[0];"
				+ "var n=ed.firstElementChild||ed;"
				+ "return String(Math.round(n.getBoundingClientRect().left));})()"));
	}

	/**
	 * How many of the editor instance's paste handlers come from the given plugin,
	 * identified by a token in the handler's own source.
	 */
	private String pasteHandlerCount(String editorId, String sourceMarker) {
		return getEval("(function(){"
				+ "var w=zk.Widget.$('$" + editorId + "'),"
				+ "t=w&&w._editor&&w._editor.data('trumbowyg');"
				+ "if(!t)return 'no-editor-instance';"
				+ "return String(t.pasteHandlers.filter(function(h){"
				+ "return String(h).indexOf('" + sourceMarker + "')>=0;}).length);})()");
	}

	/** The toolbar button of the named editor, as a click target. */
	private JQuery button(String editorId, String btnName) {
		return jq("$" + editorId).find(".z-tbeditor-" + btnName + "-button");
	}

	/** JS expression for a jq wrapper around one editor's whole rendered box. */
	private String editorScope(String editorId) {
		return "jq(zk.Widget.$('$" + editorId + "').$n())";
	}

	/** The dropdown Trumbowyg builds for a button is tagged with its name. */
	private String dropdownSelector(String btnName) {
		return "[data-z-tbeditor-dropdown=" + btnName + "]";
	}

	/** Polls a JS expression yielding 1 / 0 until it is 1, or fails after 5s. */
	private void waitFor(String what, String flagExpression) {
		for (int i = 0; i < 50; i++) {
			if ("1".equals(getEval(flagExpression)))
				return;
			sleep(100);
		}
		fail("timed out after 5s waiting for " + what);
	}

	/** Diagnostic snapshot of the current viewport; never an assertion. */
	private void savePluginShot(String name) throws IOException {
		if (!(driver instanceof TakesScreenshot))
			return;
		File dir = new File(SHOT_DIR);
		dir.mkdirs();
		File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		Files.copy(shot.toPath(), new File(dir, name + ".png").toPath(),
				StandardCopyOption.REPLACE_EXISTING);
	}
}
