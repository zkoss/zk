/* F96_ZK_4914_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 29 14:23:33 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class F96_ZK_4914_1Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4914-zk-1.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("null", getEval("zk.scriptErrorHandler"));
	}
}
