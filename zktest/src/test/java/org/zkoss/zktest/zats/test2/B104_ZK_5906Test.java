/* B104_ZK_5906Test.java

        Purpose:
                
        Description:
                
        History:
                Tue May 19 15:20:59 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5906Test extends WebDriverTestCase {
	@Test
	public void externalLinkSuppressesRmDesktop() {
		connect();
		waitResponse();

		eval("document.getElementById(zk.Widget.$('$lnk').uuid).addEventListener('click',"
				+ " function(e){ e.preventDefault(); }, false)");

		assertEquals("false", getEval("String(!!zk.skipBfUnload)"));
		assertEquals("false", getEval("String(!!zk.rmDesktoping)"));

		click(jq("$lnk"));

		assertEquals("true", getEval("String(!!zk.skipBfUnload)"));

		eval("window.onbeforeunload && window.onbeforeunload({})");

		assertEquals("false", getEval("String(!!zk.rmDesktoping)"));
	}

	@Test
	public void newTabLinkDoesNotSuppressRmDesktop() {
		connect();
		waitResponse();

		// Avoid actually opening a new tab while exercising the click.
		eval("document.getElementById(zk.Widget.$('$lnkBlank').uuid).addEventListener('click',"
				+ " function(e){ e.preventDefault(); }, false)");

		assertEquals("false", getEval("String(!!zk.skipBfUnload)"));

		click(jq("$lnkBlank"));

		// target="_blank" keeps the current desktop alive, so the flag stays off.
		assertEquals("false", getEval("String(!!zk.skipBfUnload)"));
	}

	@Test
	public void bfcacheRestoreClearsSkipFlag() {
		connect();
		waitResponse();

		eval("document.getElementById(zk.Widget.$('$lnk').uuid).addEventListener('click',"
				+ " function(e){ e.preventDefault(); }, false)");

		click(jq("$lnk"));
		assertEquals("true", getEval("String(!!zk.skipBfUnload)"));

		// Simulate a bfcache restore (the user hits Back): the pageshow handler
		// must clear the otherwise-stale flag so teardown works again.
		eval("jq(window).trigger('pageshow')");

		assertEquals("false", getEval("String(!!zk.skipBfUnload)"));
	}
}
