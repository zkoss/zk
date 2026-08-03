/* F104_ZK_5409_SmokeScreenshots.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:22:50 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("diagnostic")
public class F104_ZK_5409_SmokeScreenshots extends WebDriverTestCase {

	private static final String OUT_DIR = "/tmp/zk5409-screenshots";

	private void capture(String shortName, String zulPath) {
		new File(OUT_DIR).mkdirs();
		try { connect(zulPath); } catch (Throwable t) { System.out.println("[SMOKE] connect: " + t); }
		try { waitResponse(); } catch (Throwable t) { /* tolerate */ }
		try { Thread.sleep(800); } catch (InterruptedException ignored) {}

		// Wide — table mode (breakpoint md=768, so 1400 → table)
		try { driver.manage().window().setSize(new Dimension(1400, 900)); } catch (Throwable t) { System.out.println("[SMOKE] resize wide: " + t); }
		try { waitResponse(); } catch (Throwable t) { }
		try { Thread.sleep(600); } catch (InterruptedException ignored) {}
		try { savePng(shortName + "-wide"); } catch (Throwable t) { System.out.println("[SMOKE] save wide: " + t); }

		// Narrow — stacking mode
		try { driver.manage().window().setSize(new Dimension(600, 900)); } catch (Throwable t) { System.out.println("[SMOKE] resize narrow: " + t); }
		try { waitResponse(); } catch (Throwable t) { }
		try { Thread.sleep(900); } catch (InterruptedException ignored) {}
		try { savePng(shortName + "-narrow"); } catch (Throwable t) { System.out.println("[SMOKE] save narrow: " + t); }
	}

	private void savePng(String baseName) throws IOException {
		if (!(driver instanceof TakesScreenshot)) {
			System.out.println("[SMOKE] driver is not TakesScreenshot: " + driver.getClass());
			return;
		}
		// OutputType.FILE writes to a temp file via Selenium; we then copy.
		// This avoids the jdk HttpClient issue that fails on large BYTES payloads.
		File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File out = new File(OUT_DIR, baseName + ".png");
		Files.copy(tmp.toPath(), out.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		System.out.println("[SMOKE] wrote " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
	}

	@Test public void p01_basic()        throws Exception { capture("01-basic",        "/test2/F104-ZK-5409-responsive-grid-basic.zul"); }
	@Test public void p02_noColumns()    throws Exception { capture("02-no-columns",   "/test2/F104-ZK-5409-responsive-grid-no-columns.zul"); }
	@Test public void p03_none()         throws Exception { capture("03-none",         "/test2/F104-ZK-5409-responsive-grid-none.zul"); }
	@Test public void p04_breakpoint()   throws Exception { capture("04-breakpoint",   "/test2/F104-ZK-5409-responsive-grid-breakpoint.zul"); }
	@Test public void p05_columns()      throws Exception { capture("05-columns",      "/test2/F104-ZK-5409-responsive-grid-columns.zul"); }
	@Test public void p06_col()          throws Exception { capture("06-col",          "/test2/F104-ZK-5409-responsive-grid-col.zul"); }
	@Test public void p07_paging()       throws Exception { capture("07-paging",       "/test2/F104-ZK-5409-responsive-grid-paging.zul"); }
	@Test public void p08_model()        throws Exception { capture("08-model",        "/test2/F104-ZK-5409-responsive-grid-model.zul"); }
	@Test public void p09_group()        throws Exception { capture("09-group",        "/test2/F104-ZK-5409-responsive-grid-group.zul"); }
	@Test public void p10_sizing()       throws Exception { capture("10-sizing",       "/test2/F104-ZK-5409-responsive-grid-sizing.zul"); }
	@Test public void p11_cell()         throws Exception { capture("11-cell",         "/test2/F104-ZK-5409-responsive-grid-cell.zul"); }
	@Test public void p12_dynamic()      throws Exception { capture("12-dynamic",      "/test2/F104-ZK-5409-responsive-grid-dynamic.zul"); }
	@Test public void p13_event()        throws Exception { capture("13-event",        "/test2/F104-ZK-5409-responsive-grid-event.zul"); }
	@Test public void p14_exclusion()    throws Exception { capture("14-exclusion",    "/test2/F104-ZK-5409-responsive-grid-exclusion.zul"); }
	@Test public void p15_child()        throws Exception { capture("15-child",        "/test2/F104-ZK-5409-responsive-grid-child.zul"); }
	@Test public void p16_container()    throws Exception { capture("16-container",    "/test2/F104-ZK-5409-responsive-grid-container.zul"); }
	@Test public void p17_features()     throws Exception { capture("17-features",     "/test2/F104-ZK-5409-responsive-grid-features.zul"); }
	@Test public void p18_realworld()    throws Exception { capture("18-realworld",    "/test2/F104-ZK-5409-responsive-grid-realworld.zul"); }
	@Test public void p19_frozenLimit()  throws Exception { capture("19-frozen-limit", "/test2/F104-ZK-5409-responsive-grid-frozen-limit.zul"); }
}
