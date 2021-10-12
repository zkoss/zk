/* F96_ZK_1209Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 15:47:25 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertNotEquals;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class F96_ZK_1209Test extends WebDriverTestCase {
	@ClassRule
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
