package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by rudyhuang on 2017/06/06.
 */
public class F85_ZK_3681_CommandTest extends F85_ZK_3681_TestCase {
	@Test
	public void testCommand() throws Exception {
		connect();

		check(jq(".z-fragment input[type=\"checkbox\"]"));
		waitResponse();
		assertEquals("Checked", jq(".z-fragment span").html());
	}

	@Test
	public void testGlobalCommand() throws Exception {
		connect();

		click(jq(".z-fragment button:eq(0)"));
		waitResponse();
		assertEquals("You called @GlobalCommand. text=Hello,num=1", getZKLog());

		closeZKLog();
		click(jq(".z-fragment button:eq(1)"));
		waitResponse();
		assertEquals("You called @GlobalCommand. text=World,num=2", getZKLog());
	}
}
