package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_2881Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		JQuery item = jq(".z-listitem:last");
		click(item);
		waitResponse();
		assertTrue(item.hasClass("z-listitem-focus"));
		click(item);
		waitResponse();
		assertTrue(item.hasClass("z-listitem-focus"));
	}
}