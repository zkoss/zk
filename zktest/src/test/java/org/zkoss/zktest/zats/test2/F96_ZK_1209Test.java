/* F96_ZK_1209Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 15:47:25 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
public class F96_ZK_1209Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-1209-zk.xml");
	@Test
	public void test() {
		connect();
		waitResponse();
		int logSize = getZKLog().split("\n").length;
		assertNotEquals(0, logSize);
		JQuery jqBtns = jq("@button");
		for (int i = 0; i < jqBtns.length(); i++) {
			click(jqBtns.eq(i));
			waitResponse();
			int newLogSize = getZKLog().split("\n").length;
			assertNotEquals(logSize, newLogSize);
			logSize = newLogSize;
		}
	}
}
