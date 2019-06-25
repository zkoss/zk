/* Z35_listbox_0005Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 15:21:24 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class Z35_listbox_0005Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery listbody = jq(".z-listbox-body");
		listbody.scrollTop(listbody.scrollHeight());
		waitResponse();
		listbody.scrollTop(0);
		waitResponse();
		Assert.assertEquals(51, jq(".z-listitem").length());
		for (int i = 0; i < jq("@listitem").length(); i++) {
			Assert.assertTrue(jq("@listitem").eq(i).text().contains(String.valueOf(i)));
		}
	}
}
