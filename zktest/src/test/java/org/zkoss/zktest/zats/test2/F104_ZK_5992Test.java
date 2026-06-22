/* F104_ZK_5992Test.java

        Purpose:

        Description:

        History:
                Mon May 04 16:51:54 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;

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
 * </ul>
 */
public class F104_ZK_5992Test extends WebDriverTestCase {

	private static final String LIB_PROP = "org.zkoss.zkmax.zul.tbeditor.plugins";
	private static final String PLUGIN_PATH_PROP = "org.zkoss.zkmax.zul.tbeditor.pluginPath";

	/**
	 * Names of every plugin shipped in
	 * {@code zkmax/.../tbeditor/ext/plugins/manifest.json}.
	 * Keep in alphabetical order; keep in sync with the manifest and with the
	 * editors in {@code F104-ZK-5992-AllPlugins.zul}.
	 */
	private static final List<String> ALL_PLUGINS = Arrays.asList(
			"base64", "cleanpaste", "colors", "emoji",
			"fontfamily", "fontsize", "history", "indent",
			"insertaudio", "lineheight", "noembed", "pasteembed",
			"pasteimage", "preformatted", "resizimg", "ruby",
			"specialchars", "table", "template", "upload"
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
		PLUGIN_BUTTONS.put("noembed", Arrays.asList("noembed"));
		PLUGIN_BUTTONS.put("preformatted", Arrays.asList("preformatted"));
		PLUGIN_BUTTONS.put("ruby", Arrays.asList("ruby"));
		PLUGIN_BUTTONS.put("specialchars", Arrays.asList("specialChars"));
		PLUGIN_BUTTONS.put("table", Arrays.asList("table"));
		PLUGIN_BUTTONS.put("upload", Arrays.asList("upload"));
		// NOTE: 'template' is intentionally excluded — its Trumbowyg plugin declares
		// shouldInit = o.plugins.hasOwnProperty('templates'), so it only registers
		// its button when the app supplies a 'templates' config. Without that config
		// (as on the bare AllPlugins page) no button is added, which is correct; the
		// plugin's loading is still covered by eachPluginLoads(). It remains in the
		// manifest's btns for apps that do configure templates.
	}

	/** AC1, AC3 (override + clear), AC4, AC5 (unknown plugin), AC6 (dedupe). */
	@Test
	public void perComponentAndInheritance() {
		clearLibraryProperties();
		connect("/test2/F104-ZK-5992.zul");
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
			connect("/test2/F104-ZK-5992-Lib.zul");
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
			connect("/test2/F104-ZK-5992.zul");
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
			connect("/test2/F104-ZK-5992-Lib.zul");
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
		connect("/test2/F104-ZK-5992-Lib.zul");
		waitResponse();
		waitForEditors(1);

		// No <script> tag pointing at the plugins directory should exist.
		assertEquals("0",
				getEval("jq('script[src*=\"/ext/plugins/\"]').length"),
				"a bare editor with no configured plugins must not request any plugin assets");
	}

	/**
	 * AC9 — every plugin shipped in the manifest is loadable individually. Emitted
	 * as one dynamic test <em>per plugin</em> (~20 cases) plus a registry check, so
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
		connect("/test2/F104-ZK-5992-AllPlugins.zul");
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
	 * (~15 cases) so each plugin's usability is reported independently. {@code template}
	 * is excluded (config-gated — see {@link #PLUGIN_BUTTONS}); behaviour-only plugins
	 * add no button and are covered by {@link #eachPluginLoads()}. Deeper per-plugin
	 * behaviour is Trumbowyg's own concern.
	 *
	 * <p>Reuses {@code F104-ZK-5992-AllPlugins.zul} (one editor per plugin), so each
	 * button is asserted scoped to that plugin's editor — proving it landed in the
	 * right toolbar, not merely somewhere on the page.</p>
	 */
	@TestFactory
	Stream<DynamicTest> eachPluginAddsToolbarButton() {
		clearLibraryProperties();
		connect("/test2/F104-ZK-5992-AllPlugins.zul");
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
	 * Auto-buttons opt-out — {@code config.autoPluginButtons=false} must suppress
	 * the auto-inserted button while the plugin itself still loads (the page sets
	 * the flag on one of two table editors). Proves the feature is opt-out-able and
	 * that loading and button-insertion are independent.
	 */
	@Test
	public void autoPluginButtonsCanBeDisabled() {
		clearLibraryProperties();
		connect("/test2/F104-ZK-5992-Buttons.zul");
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
}
