/* B85_ZK_1148Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 16 6:09 PM:01 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.zktest.test2.F85_ZK_1148FileDealer;

public class F85_ZK_1148Test extends ClientBindTestCase {

	@Test
	public void testInnerWindowDetach() {
		connect();
		sleep(2000);
		JQuery wout = jq("$outWin");
		JQuery btn = wout.find("$delBtn");
		click(btn);
		waitResponse();
		JQuery label = jq("$resultLabel");
		Assertions.assertEquals("DestroyD DestroyC", label.text().trim());
	}

	@Test
	public void testOutWindowDetach() {
		connect();
		sleep(2000);
		JQuery btn = jq("$delBtno");
		click(btn);
		waitResponse();
		JQuery label = jq("$resultLabel");
		Assertions.assertEquals("DestroyD DestroyC", label.text().trim());

	}

	@Test
	public void testDesktopDestroy() {
		F85_ZK_1148FileDealer.writeMsg("", false);
		connect();
		sleep(2000);
		eval("zk.Desktop.destroy(zk.$('.z-page').desktop)");
		eval("zAu.cmd1.rm(zk.$('.z-page'))");
		waitResponse();
		try {
			Assertions.assertEquals("DestroyD is called!DestroyC is called!IncludeVM is called!", F85_ZK_1148FileDealer.readMsg());
		} finally {
			F85_ZK_1148FileDealer.close();
		}
	}

	@Test
	public void testIncludeDestroy() {
		F85_ZK_1148FileDealer.writeMsg("", false);
		connect();
		sleep(2000);
		JQuery reloadWin = jq("$reloadWin");
		JQuery btn = reloadWin.find("$reload");
		click(btn);
		waitResponse();
		try {
			Assertions.assertEquals("IncludeVM is called!", F85_ZK_1148FileDealer.readMsg());
		} finally {
			F85_ZK_1148FileDealer.close();
		}
	}

	@AfterAll
	public static void cleanUp() {
		F85_ZK_1148FileDealer.close();
	}

}
