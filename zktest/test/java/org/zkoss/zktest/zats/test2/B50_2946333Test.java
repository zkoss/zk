/* B50_2946333Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 17:43:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2946333Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int splitterTop50per = jq(".z-north-splitter").positionTop();

		click(jq("@button:contains(25%)"));
		waitResponse();
		Assert.assertThat(jq(".z-north-splitter").positionTop(), lessThan(splitterTop50per));

		click(jq("@button:contains(50%)"));
		waitResponse();
		Assert.assertEquals(splitterTop50per, jq(".z-north-splitter").positionTop(), 2);

		click(jq("@button:contains(75%)"));
		waitResponse();
		Assert.assertThat(jq(".z-north-splitter").positionTop(), greaterThan(splitterTop50per));
	}
}
