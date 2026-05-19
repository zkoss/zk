/* F104_ZK_5409_VisualInspection.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:22:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("diagnostic")
public class F104_ZK_5409_VisualInspection extends WebDriverTestCase {

	private static final String REPORT = "/tmp/zk5409-visual-report.txt";

	/** Inline JavaScript that produces a JSON snapshot of responsive-grid state. */
	private static final String INSPECT_JS =
			"var grids = document.querySelectorAll('.z-grid');\n" +
			"var out = [];\n" +
			"grids.forEach(function(g, i) {\n" +
			"  var info = {\n" +
			"    index: i,\n" +
			"    id: g.id || '',\n" +
			"    classes: g.className,\n" +
			"    width: g.offsetWidth,\n" +
			"    height: g.offsetHeight,\n" +
			"    isStacking: g.classList.contains('z-grid--stacking'),\n" +
			"    rect: { top: g.getBoundingClientRect().top|0, left: g.getBoundingClientRect().left|0 }\n" +
			"  };\n" +
			"  var header = g.querySelector('.z-grid-header');\n" +
			"  info.headerVisible = header ? (header.offsetWidth > 0 && header.offsetHeight > 0) : false;\n" +
			"  info.headerDisplay = header ? getComputedStyle(header).display : 'none';\n" +
			"  var zone = g.querySelector('.z-grid-body tbody');\n" +
			"  if (zone) {\n" +
			"    info.hasStackingZone = true;\n" +
			"    info.zoneRowCount = zone.querySelectorAll(':scope > div.z-row').length;\n" +
			"    info.zoneCellLabelCount = zone.querySelectorAll('td[data-label]').length;\n" +
			"    info.zoneGroupCount = zone.querySelectorAll(':scope > .z-group').length;\n" +
			"    info.zoneGroupfootCount = zone.querySelectorAll(':scope > .z-groupfoot').length;\n" +
			"    info.zoneGridTemplateColumns = getComputedStyle(zone).gridTemplateColumns;\n" +
			"  } else {\n" +
			"    info.hasStackingZone = false;\n" +
			"  }\n" +
			"  var frozen = g.querySelector('.z-frozen');\n" +
			"  info.frozenVisible = frozen ? (frozen.offsetWidth > 0 && frozen.offsetHeight > 0) : null;\n" +
			"  var rows = g.querySelectorAll('.z-grid-body .z-row');\n" +
			"  info.tableRowCount = rows.length;\n" +
			"  out.push(info);\n" +
			"});\n" +
			"return JSON.stringify(out);\n";

	private final StringBuilder report = new StringBuilder();

	private void inspect(String name, String zulPath) {
		try { connect(zulPath); } catch (Throwable ignored) {}
		try { waitResponse(); } catch (Throwable ignored) {}
		try { Thread.sleep(700); } catch (InterruptedException ignored) {}

		// Wide viewport
		try { driver.manage().window().setSize(new Dimension(1400, 900)); } catch (Throwable ignored) {}
		try { waitResponse(); } catch (Throwable ignored) {}
		try { Thread.sleep(500); } catch (InterruptedException ignored) {}
		String wide = safeRunJs();

		// Narrow viewport
		try { driver.manage().window().setSize(new Dimension(600, 900)); } catch (Throwable ignored) {}
		try { waitResponse(); } catch (Throwable ignored) {}
		try { Thread.sleep(900); } catch (InterruptedException ignored) {}
		String narrow = safeRunJs();

		report.append("=== ").append(name).append(" ").append(zulPath).append(" ===\n");
		report.append("WIDE(1400): ").append(wide).append("\n");
		report.append("NARROW(600): ").append(narrow).append("\n\n");
	}

	private String safeRunJs() {
		try {
			if (driver instanceof JavascriptExecutor) {
				Object r = ((JavascriptExecutor) driver).executeScript(INSPECT_JS);
				return r == null ? "null" : r.toString();
			}
			return "no-js-executor";
		} catch (Throwable t) {
			return "ERROR: " + t.getClass().getSimpleName() + " " + t.getMessage();
		}
	}

	private void flushReport() {
		try {
			File f = new File(REPORT);
			Files.writeString(f.toPath(), report.toString());
			System.out.println("[INSPECT] report written to " + f.getAbsolutePath() +
					" (" + f.length() + " bytes)");
		} catch (Throwable t) {
			System.err.println("[INSPECT] failed to write report: " + t);
		}
	}

	@Test public void p01_basic()       { inspect("01-basic",        "/test2/F104-ZK-5409-responsive-grid-basic.zul"); flushReport(); }
	@Test public void p02_noColumns()   { inspect("02-no-columns",   "/test2/F104-ZK-5409-responsive-grid-no-columns.zul"); flushReport(); }
	@Test public void p03_none()        { inspect("03-none",         "/test2/F104-ZK-5409-responsive-grid-none.zul"); flushReport(); }
	@Test public void p04_breakpoint()  { inspect("04-breakpoint",   "/test2/F104-ZK-5409-responsive-grid-breakpoint.zul"); flushReport(); }
	@Test public void p05_columns()     { inspect("05-columns",      "/test2/F104-ZK-5409-responsive-grid-columns.zul"); flushReport(); }
	@Test public void p06_col()         { inspect("06-col",          "/test2/F104-ZK-5409-responsive-grid-col.zul"); flushReport(); }
	@Test public void p07_paging()      { inspect("07-paging",       "/test2/F104-ZK-5409-responsive-grid-paging.zul"); flushReport(); }
	@Test public void p08_model()       { inspect("08-model",        "/test2/F104-ZK-5409-responsive-grid-model.zul"); flushReport(); }
	@Test public void p09_group()       { inspect("09-group",        "/test2/F104-ZK-5409-responsive-grid-group.zul"); flushReport(); }
	@Test public void p10_sizing()      { inspect("10-sizing",       "/test2/F104-ZK-5409-responsive-grid-sizing.zul"); flushReport(); }
	@Test public void p11_cell()        { inspect("11-cell",         "/test2/F104-ZK-5409-responsive-grid-cell.zul"); flushReport(); }
	@Test public void p12_dynamic()     { inspect("12-dynamic",      "/test2/F104-ZK-5409-responsive-grid-dynamic.zul"); flushReport(); }
	@Test public void p13_event()       { inspect("13-event",        "/test2/F104-ZK-5409-responsive-grid-event.zul"); flushReport(); }
	@Test public void p14_exclusion()   { inspect("14-exclusion",    "/test2/F104-ZK-5409-responsive-grid-exclusion.zul"); flushReport(); }
	@Test public void p15_child()       { inspect("15-child",        "/test2/F104-ZK-5409-responsive-grid-child.zul"); flushReport(); }
	@Test public void p16_container()   { inspect("16-container",    "/test2/F104-ZK-5409-responsive-grid-container.zul"); flushReport(); }
	@Test public void p17_features()    { inspect("17-features",     "/test2/F104-ZK-5409-responsive-grid-features.zul"); flushReport(); }
	@Test public void p18_realworld()   { inspect("18-realworld",    "/test2/F104-ZK-5409-responsive-grid-realworld.zul"); flushReport(); }
	@Test public void p19_frozenLimit() { inspect("19-frozen-limit", "/test2/F104-ZK-5409-responsive-grid-frozen-limit.zul"); flushReport(); }
}
