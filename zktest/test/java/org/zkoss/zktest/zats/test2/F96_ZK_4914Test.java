/* F96_ZK_4914Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 29 14:23:33 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class F96_ZK_4914Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4914-zk.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		assertNotEquals("null", getEval("zk.scriptErrorHandler"));
		JQuery jqBtnError = jq("$btnError");
		click(jqBtnError);
		waitResponse();
		assertTrue(getZKLog().contains("1-Clients.evalJavaScript Error!"));

		JQuery jqBtnNormal = jq("$btnNormal");
		click(jqBtnNormal);
		click(jqBtnNormal);
		click(jqBtnError);
		waitResponse();
		assertTrue(getZKLog().contains("2-Clients.evalJavaScript Error!"));
		assertFalse(getZKLog().contains("3-Clients.evalJavaScript Error!"));

		click(jq("$btnCallJsScript"));
		waitResponse();
		assertFalse(getZKLog().contains("3-Clients.evalJavaScript Error!"));
	}
}
