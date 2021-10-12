package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_3638Test extends WebDriverTestCase{

	@Test
	public void test () {
		connect();
		JQuery btn = jq("@button");
		click(btn.eq(0));
		waitResponse();
		assertEquals(true, isZKLogAvailable());
		assertEquals(true, getZKLog().contains("onCustomzkevent"));
	}
}
