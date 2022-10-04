/* B96_ZK_5030Test.java

	Purpose:
		
	Description:
		
	History:
		12:18 PM 2022/10/3, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5030Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 2;

	@Test
	public void testResizeColumn() {
		connect();
		JQuery listheader = jq("@listheader");

		JQuery column1 = listheader.eq(1);
		JQuery column3 = listheader.eq(3);
		Assertions.assertTrue(column3.width() > 10);
		resizeColumn(column1);
		assertThat(column1.outerWidth(), greaterThan(10));
		Assertions.assertTrue(column3.width() > 10);

	}

	@Test
	public void testFixedWidth() {
		connect(getTestURL("B96-ZK-5030-2.zul"));
		JQuery listheaders = jq("@listheader");
		JQuery listcells = jq("@listcell");

		Iterator<JQuery> headerIter = listheaders.iterator();
		Iterator<JQuery> cellIter = listcells.iterator();

		while (headerIter.hasNext() && cellIter.hasNext()) {
			JQuery next = headerIter.next();
			JQuery cellNext = cellIter.next();
			if (next.isVisible()) {
				assertEquals(next.width(), cellNext.width(), 2);
			}
		}
		JQuery column1 = listheaders.eq(1);

		resizeColumn(column1);
		headerIter = listheaders.iterator();
		cellIter = listcells.iterator();

		while (headerIter.hasNext() && cellIter.hasNext()) {
			JQuery next = headerIter.next();
			JQuery cellNext = cellIter.next();
			if (next.isVisible()) {
				assertEquals(next.width(), cellNext.width(), 2);
			}
		}
	}

	private void resizeColumn(JQuery col) {
		int colWidth = col.outerWidth();
		getActions().moveToElement(toElement(col), colWidth / 2 - DRAG_THRESHOLD, 1)
				.clickAndHold()
				.moveByOffset(-50, 1)
				.release()
				.perform();
		int colWidthAfter = col.outerWidth();
		assertThat("resize failed", colWidthAfter, lessThan(colWidth));
	}
}
