/* B103_ZK_5896Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Dec 09 15:59:43 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B103_ZK_5896Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 2;

	@Test
	public void testResizeCol1() {
		testResizeColumn(0);
	}

	@Test
	public void testResizeCol4() {
		testResizeColumn(3);
	}

	@Test
	public void testResizeCol5() {
		testResizeColumn(4);
	}

	private void testResizeColumn(int i) {
		connect();
		JQuery column = jq("@column");
		int[] widths = IntStream.range(0, column.length()).map(j -> column.eq(j).width()).toArray();
		resizeColumn(column.eq(i));
		int[] widthsAfter = IntStream.range(0, column.length()).map(j -> column.eq(j).width()).toArray();
		assertEquals(widthsAfter[2], widthsAfter[1] * 2, 2);
		for (int j = 0; j < column.length(); j++) {
			if (i == j) continue;
			assertEquals(widths[j], widthsAfter[j], 2);
		}
	}

	private void resizeColumn(JQuery col) {
		int colWidth = col.width();
		getActions().moveToElement(toElement(col), colWidth / 2 - DRAG_THRESHOLD, 1).clickAndHold().moveByOffset(-50, 1).release().perform();
		int colWidthAfter = col.width();
		assertThat("resize failed", colWidthAfter, lessThan(colWidth));
	}
}
