package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author bob peng
 */
public class B85_ZK_3726Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-datebox-icon.z-icon-calendar:eq(0)"));
		waitResponse();
		blur(jq(".z-datebox-icon.z-icon-calendar:eq(0)"));
		waitResponse();
		click(jq(".z-datebox-icon.z-icon-calendar:eq(1)"));
		waitResponse();
		blur(jq(".z-datebox-icon.z-icon-calendar:eq(1)"));
		waitResponse();

		assertEquals("The week number of 2012/01/01 in the first datebox should be 1.\n" +
				"The week number of 2012/01/01 in the second datebox should be 52.", "1\n52", getZKLog());
	}
}