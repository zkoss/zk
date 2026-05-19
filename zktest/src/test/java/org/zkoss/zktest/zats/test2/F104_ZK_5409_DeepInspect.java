/* F104_ZK_5409_DeepInspect.java

        Purpose:
                
        Description:
                
        History:
                Sat Apr 25 22:23:21 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("diagnostic")
public class F104_ZK_5409_DeepInspect extends WebDriverTestCase {

	@Test
	public void inspectGroupGrid() {
		try { connect("/test2/F104-ZK-5409-responsive-grid-group.zul"); } catch (Throwable ignored) {}
		try { waitResponse(); } catch (Throwable ignored) {}
		try { Thread.sleep(700); } catch (InterruptedException ignored) {}
		try { driver.manage().window().setSize(new Dimension(600, 900)); } catch (Throwable ignored) {}
		try { waitResponse(); } catch (Throwable ignored) {}
		try { Thread.sleep(900); } catch (InterruptedException ignored) {}

		String js = "var g = document.querySelector('.z-grid');\n" +
				"var zone = g ? g.querySelector('.z-grid-body tbody') : null;\n" +
				"if (!zone) return 'no-zone; grid=' + (g ? g.className : 'null');\n" +
				"var info = {\n" +
				"  inlineStyle: zone.getAttribute('style'),\n" +
				"  cssVar: zone.style.getPropertyValue('--zk-resp-cols'),\n" +
				"  computedColumns: getComputedStyle(zone).gridTemplateColumns,\n" +
				"  computedDisplay: getComputedStyle(zone).display,\n" +
				"  children: [],\n" +
				"  hasZkRespCols: getComputedStyle(zone).getPropertyValue('--zk-resp-cols')\n" +
				"};\n" +
				"for (var i = 0; i < zone.children.length; i++) {\n" +
				"  var c = zone.children[i];\n" +
				"  info.children.push({\n" +
				"    tag: c.tagName,\n" +
				"    cls: c.className,\n" +
				"    gridCol: getComputedStyle(c).gridColumn,\n" +
				"    gridColStart: getComputedStyle(c).gridColumnStart,\n" +
				"    gridColEnd: getComputedStyle(c).gridColumnEnd\n" +
				"  });\n" +
				"}\n" +
				"return JSON.stringify(info, null, 2);\n";
		String result = (String) ((JavascriptExecutor) driver).executeScript(js);
		System.out.println("=== GRID1 DEEP INSPECT ===");
		System.out.println(result);
	}
}
