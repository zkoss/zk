/* B96_ZK_4879_2Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 25 14:25:00 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;

@Category(WcagTestOnly.class)
public class B96_ZK_4879_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String menuitemSelector = "> [role=\"menuitem\"]";
		
		click(jq("$nav1"));
		waitResponse();
		Assert.assertTrue("the focus shall move to the navitem inside", jq("$navitem11").find(menuitemSelector).is(":focus"));
		
		click(jq("$navitem2"));
		waitResponse();
		Assert.assertTrue("the focus shall move to navitem2", jq("$navitem2").find(menuitemSelector).is(":focus"));
		
		click(jq("$nav1"));
		waitResponse();
		Assert.assertTrue("the focus shall move to the navitem inside", jq("$navitem11").find(menuitemSelector).is(":focus"));
		
		click(jq("$navitem2"));
		waitResponse();
		Assert.assertTrue("the focus shall move to navitem2", jq("$navitem2").find(menuitemSelector).is(":focus"));
		
		click(jq("$nav3"));
		waitResponse();
		Assert.assertTrue("the focus shall move to the nav/navitem you clicked.", jq("$nav3").find(menuitemSelector).is(":focus"));
		
		click(jq("$navitem4"));
		waitResponse();
		Assert.assertTrue("the focus shall move to the nav/navitem you clicked.", jq("$navitem4").find(menuitemSelector).is(":focus"));
		
		click(jq("$nav5"));
		waitResponse();
		Assert.assertTrue("the focus shall move to the nav/navitem you clicked.", jq("$nav5").find(menuitemSelector).is(":focus"));
		
		click(jq("$nav6"));
		waitResponse();
		Assert.assertTrue("the focus shall move to the nav/navitem you clicked.", jq("$nav6").find(menuitemSelector).is(":focus"));
	}
}
