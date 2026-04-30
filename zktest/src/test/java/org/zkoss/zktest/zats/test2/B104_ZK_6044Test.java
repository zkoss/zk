/* B104_ZK_6044Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 30 17:48:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * ZK-6044: on the tablet UI, the tree toggle triangle was rendered with a
 * 22px font-size in a 12x12 box, so the glyph overflowed and overlapped
 * adjacent text without breathing room. The fix grows the icon/line box
 * and restores the 8px gap between icon and treecell text.
 */
@ForkJVMTestOnly
public class B104_ZK_6044Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation",
						Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void treeToggleIconHasGapBeforeText() {
		connect();
		waitResponse();

		assertTrue(jq(".z-tree-icon").exists(),
				"tree toggle icon must render");

		// Each tree-icon must paint with a box at least the size of its
		// 22px glyph, and the cell text must start at least 8px after the
		// icon's right edge so the two never overlap visually.
		String script = "(function(){"
				+ "var icons=document.querySelectorAll('.z-tree-icon');"
				+ "for(var i=0;i<icons.length;i++){"
				+ "  var icon=icons[i];"
				+ "  var ir=icon.getBoundingClientRect();"
				+ "  if(ir.width<22||ir.height<22)"
				+ "    return 'icon-too-small:'+ir.width+'x'+ir.height;"
				+ "  var text=icon.parentNode&&icon.parentNode.querySelector('.z-treecell-text');"
				+ "  if(text){"
				+ "    var tr=text.getBoundingClientRect();"
				+ "    if(tr.left<ir.right)return 'overlap:'+tr.left+'<'+ir.right;"
				+ "  }"
				+ "}"
				+ "return 'ok';"
				+ "})()";
		String result = getEval(script);
		assertTrue("ok".equals(result),
				"ZK-6044: tree toggle icon must fit its glyph and have"
						+ " breathing room before the text; got " + result);
	}
}
