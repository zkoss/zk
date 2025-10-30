/* F50_2840886Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 16:10:23 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2840886Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals(jq("@grid @column:first").width(), jq("@grid @frozen .z-frozen-body").width(), 2);

		int first2ColumnWidth = jq("@listbox @listheader:eq(0)").width() + jq("@listbox @listheader:eq(1)").width();
		Assertions.assertEquals(first2ColumnWidth, jq("@listbox @frozen .z-frozen-body").width(), 2);
	}
}
