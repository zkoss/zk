/* B96_ZK_4890Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 19 18:50:21 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4890Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		MatcherAssert.assertThat(
				"Listbox scrollToIndex failed",
				jq(widget("@listbox").$n("body")).scrollTop(),
				Matchers.greaterThanOrEqualTo(10000)); // be likely item 500
		MatcherAssert.assertThat(
				"Grid scrollToIndex failed",
				jq(widget("@grid").$n("body")).scrollTop(),
				Matchers.greaterThanOrEqualTo(10000)); // be likely item 500
	}
}
