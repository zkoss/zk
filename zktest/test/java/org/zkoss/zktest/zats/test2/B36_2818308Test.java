/* B36_2818308Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 13 15:10:26 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThan;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B36_2818308Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery body = jq(widget("$tree").$n("body"));
		click(jq("@button[label=\"add under bottom\"]"));
		waitResponse();
		click(jq("@button[label=\"add under bottom\"]"));
		waitResponse();
		Assert.assertThat(jq("@treecell:contains(test):last").positionTop(), lessThan(body.outerHeight()));

		click(jq("@button[label=\"add upon top\"]"));
		waitResponse();
		click(jq("@button[label=\"add upon top\"]"));
		waitResponse();
		Assert.assertEquals(0, body.scrollTop());
	}
}
