/* B96_ZK_4813Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 07 12:13:31 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */

public class B96_ZK_5418Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqRoot1 = jq("$root1");
		JQuery jqRoot2 = jq("$root2");
		type(jqRoot1.find("@textbox"), "a");
		type(jqRoot2.find("@textbox"), "a");
		waitResponse();

		connect("/test2/B96-ZK-5418-1.zul");
		waitResponse();
		type(jqRoot1.find("@textbox"), "b");
		type(jqRoot2.find("@textbox"), "b");
		waitResponse();
		Assert.assertEquals("b", jq("$r1").text());
		Assert.assertEquals("b", jq("$r2").text());
	}
}
