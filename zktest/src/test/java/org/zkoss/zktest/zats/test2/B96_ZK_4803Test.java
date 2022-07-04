/* B96_ZK_4803Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 08 15:01:26 CST 2021, Created by rudyhuang

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
public class B96_ZK_4803Test extends WebDriverTestCase {
	@Test
	public void testListbox() {
		test("/test2/B96-ZK-4803-listbox.zul");
	}

	@Test
	public void testGrid() {
		test("/test2/B96-ZK-4803-grid.zul");
	}

	@Test
	public void testTree() {
		test("/test2/B96-ZK-4803.zul");
	}

	private void test(String zulPath) {
		connect(zulPath);

		waitResponse();

		click(jq("@button:eq(1)"));
		waitResponse();
		double periodB = getPeriod();

		click(jq("@button:eq(0)"));
		waitResponse();
		double periodA = getPeriod();

		MatcherAssert.assertThat("Method A was too slow!", periodA, Matchers.lessThanOrEqualTo(periodB * 2));
	}

	private double getPeriod() {
		double period = Double.parseDouble(getZKLog().replaceAll("[^\\d.]", ""));
		closeZKLog();
		return period;
	}
}
