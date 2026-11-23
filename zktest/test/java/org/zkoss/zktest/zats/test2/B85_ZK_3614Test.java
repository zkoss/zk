package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author bob peng
 */
public class B85_ZK_3614Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btnOpen = jq("$btnOpen");
		JQuery btnAdd = jq("$btnAdd");
		JQuery close = jq(".z-window-close");
		click(btnOpen);
		waitResponse();
		click(close);
		waitResponse();
		click(btnAdd);
		waitResponse();
		click(btnOpen);
		waitResponse();
		assertFalse("error popped", jq(".z-messagebox-error").exists());
	}
}