package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author bob peng
 */
public class B85_ZK_3554Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();
		rightClick(jq("$btn_context"));
		waitResponse();
		assertTrue("error: did not show menupopup when right click button", jq(".z-menupopup-content").exists());
	}

	@Test
	public void test2() {
		connect();
		click(jq("$btn_popupId"));
		waitResponse();
		assertTrue("error: did not show menupopup when left click button", jq(".z-menupopup-content").exists());
	}
}