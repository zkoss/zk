/* F86_ZK_2756_2Test.java

	Purpose:

	Description:

	History:
		Mon Sep 17 17:23:52 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author wenninghsu
 */
public class B86_ZK_4056Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq(".z-button").eq(0));
		waitResponse();
		assertEquals("testing", getSelection());
		click(jq(".z-button").eq(1));
		waitResponse();
		assertEquals("testi", getSelection());
	}

	public String getSelection() {
		return getEval("window.getSelection().toString()");
	}
}
