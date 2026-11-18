/* F110_ZK_6149_WebSocketTest.java

		Purpose:

		Description:

		History:
				Thu Nov 19 14:10:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class F110_ZK_6149_WebSocketTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F110-ZK-6149-zk.xml");

	@Test
	public void backToKeptDesktopOverWebSocket() {
		connect("/test2/F110-ZK-6149.zul?cacheable=1");
		waitResponse();
		assertEquals("true", getEval("String(zWs.ready)"), "the AU requests should go over the WebSocket");
		String dtid = jq("$dtid").text();
		eval("window.addEventListener('pageshow', function (e) { if (e.persisted) sessionStorage.setItem('zk6149Restored', '1'); })");
		driver.get(driver.getCurrentUrl() + "&zk6149=next");
		waitResponse();
		driver.navigate().back();
		waitResponse();
		assertEquals("1", getEval("String(sessionStorage.getItem('zk6149Restored'))"), "the page was not restored from the back/forward cache");
		click(jq("$btn"));
		waitResponse();
		assertEquals(dtid, jq("$dtid").text(), "the kept desktop should resume without a reload");
		assertFalse(jq("$removed").text().isEmpty(), "the restored page did not reach the server");
	}

	// a leave the user cancels fires beforeunload but no pagehide; headless Chrome shows no leave prompt, so dispatch the event
	@Test
	public void cancelledLeaveKeepsWebSocket() {
		connect("/test2/F110-ZK-6149.zul");
		waitResponse();
		assertEquals("true", getEval("String(zWs.ready)"), "the AU requests should go over the WebSocket");
		eval("zk.confirmClose = 'leave?'");
		eval("window.dispatchEvent(new Event('beforeunload'))");
		click(jq("$btn"));
		waitResponse();
		assertFalse(jq("$removed").text().isEmpty(), "the page did not reach the server after the leave was cancelled");
	}

	// zEmbedded.destroy() drops the transport the way _removeWebSocketEndPoint does; a later restore must not revive it
	@Test
	public void restoreDoesNotReviveRemovedWebSocket() {
		connect("/test2/F110-ZK-6149.zul");
		waitResponse();
		assertEquals("true", getEval("String(zWs.ready)"), "the AU requests should go over the WebSocket");
		eval("(function () { var ep = zWs.webSocketEndPoint; ep.onmessage = ep.onopen = ep.onclose = ep.onerror = null; ep.close(); zWs.webSocketEndPoint = undefined; })()");
		eval("window.dispatchEvent(new PageTransitionEvent('pageshow', {persisted: true}))");
		sleep(3000);
		assertEquals("false", getEval("String(!!zWs.webSocketEndPoint)"), "the removed WebSocket was reconnected");
	}
}
