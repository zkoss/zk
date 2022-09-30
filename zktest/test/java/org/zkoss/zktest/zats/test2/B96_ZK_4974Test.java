/* B96_ZK_4974Test.java

	Purpose:
		
	Description:
		
	History:
		5:26 PM 2022/9/30, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_4974Test extends WebDriverTestCase {
	@Test
	public void testCannotShrink() {
		connect();

		// shrink the browser width to as minimal as possible.
		driver.manage().window().setSize(new Dimension(200, 600));

		assertEquals(200, jq("@grid:eq(0) @column").width(), 1);
		assertEquals(200, jq("@grid:eq(1) @column").width(), 1);
		assertEquals(200, jq("@grid:eq(2) @column").width(), 1);
		assertEquals(200, jq("@grid:eq(0) @column:eq(2)").width(), 1);
		assertEquals(200, jq("@grid:eq(1) @column:eq(2)").width(), 1);
		assertEquals(200, jq("@grid:eq(2) @column:eq(2)").width(), 1);

	}
}
