/* F96_ZK_4914Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 29 14:23:33 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class F96_ZK_4914Test extends WebDriverTestCase {
	@RegisterExtension
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
