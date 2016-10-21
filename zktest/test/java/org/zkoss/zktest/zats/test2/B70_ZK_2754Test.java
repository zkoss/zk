package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B70_ZK_2754Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		String msg_q1 = "[to queue1]";
		String msg_q2 = "[to queue2]";
		JQuery label1 = jq("$label1");
		JQuery label2 = jq("$label2");
		click(jq("$publish1"));
		waitResponse();
		assertEquals(msg_q1, label1.text().trim());
		click(jq("$publish2"));
		waitResponse();
		assertEquals(msg_q2, label2.text().trim());

		String jqScript = "var btns = $(\"iframe\").contents().find('button');";
		((ZKWebDriver) getWebDriver()).executeScript(jqScript);
		waitResponse();
		((ZKWebDriver) getWebDriver()).executeScript(jqScript + "btns[0].click();");
		waitResponse();
		assertEquals(msg_q1 + msg_q1, label1.text().trim());
		((ZKWebDriver) getWebDriver()).executeScript(jqScript + "btns[1].click();");
		waitResponse();
		assertEquals(msg_q2 + msg_q2, label2.text().trim());
		((ZKWebDriver) getWebDriver()).executeScript(jqScript + "btns[2].click();");
		waitResponse();
		assertEquals(msg_q1 + msg_q1 + msg_q1, label1.text().trim());
		assertEquals(msg_q2 + msg_q2 + msg_q2, label2.text().trim());
	}
}