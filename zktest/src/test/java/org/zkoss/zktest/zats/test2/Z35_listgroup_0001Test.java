/* Z35_listgroup_0001Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 21 15:39:38 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class Z35_listgroup_0001Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		// data only
		click(jq("@button:contains(data only)"));
		waitResponse();
		MatcherAssert.assertThat("jq('tr.z-listgroup:eq(0)').text(): ", jq("tr.z-listgroup:eq(0)").text(), containsString("option"));

		// data, head
		click(jq("@button:contains(data ,head)"));
		waitResponse();
		MatcherAssert.assertThat("jq('tr.z-listgroup:eq(0)').text(): ", jq("tr.z-listgroup:eq(0)").text(), containsString("Group"));

		// data, head, foot
		click(jq("@button:contains(data,head,foot)"));
		waitResponse();
		Assert.assertEquals("jq('tr.z-listgroupfoot').length(): ", 4, jq("tr.z-listgroupfoot").length());

		// not all foot
		click(jq("@button:contains(not all foot)"));
		waitResponse();
		Assert.assertEquals("jq('tr.z-listgroupfoot').length(): ", 2, jq("tr.z-listgroupfoot").length());
		MatcherAssert.assertThat("jq('tr.z-listgroupfoot:eq(0)'): ", jq("tr.z-listgroupfoot:eq(0)").text(), containsString("A1"));

		// not all foot 2
		click(jq("@button:contains(not all foot 2)"));
		waitResponse();
		Assert.assertEquals("jq('tr.z-listgroupfoot').length(): ", 2, jq("tr.z-listgroupfoot").length());
		click(jq("tr.z-listgroup:eq(0)").toWidget().$n("img"));
		waitResponse();
		sleep(300);
		MatcherAssert.assertThat("jq('tr.z-listgroupfoot:eq(0)'): ", jq("tr.z-listgroupfoot:eq(0)").text(), containsString("B2"));

		// set Paging
		click(jq("@button:contains(set Paging)"));
		waitResponse();
		Assert.assertTrue("Set paging failed", jq(".z-paging").exists());

		// invalidate
		click(jq("@button:contains(invalidate)"));
		waitResponse();
		Assert.assertTrue("The screen cannot has any change after clicking invalidate", jq(".z-paging").exists());
	}
}
