/* B96_ZK_4879Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 20 12:30:26 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;
import org.zkoss.zktest.zats.ztl.JQuery;

@Category(WcagTestOnly.class)
public class B96_ZK_4879Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery navs = jq("@nav");
		String menuitemSelector = "> [role=\"menuitem\"]";

		click(navs.eq(0));
		waitResponse(true);
		Assert.assertTrue("the focus shall stay at the nav you clicked", navs.eq(0).find(menuitemSelector).is(":focus"));

		click(navs.eq(1));
		waitResponse(true);
		Assert.assertTrue("the focus shall move to the nav you clicked", navs.eq(1).find(menuitemSelector).is(":focus"));

		click(navs.eq(2));
		waitResponse(true);
		Assert.assertTrue("the focus shall move to the navitem inside", jq("@navitem").find(menuitemSelector).is(":focus"));

		click(navs.eq(3));
		waitResponse(true);
		Assert.assertTrue("the focus shall move to the nav you clicked", navs.eq(3).find(menuitemSelector).is(":focus"));
	}
}
