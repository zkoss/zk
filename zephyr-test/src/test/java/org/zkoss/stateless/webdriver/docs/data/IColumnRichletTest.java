/* IColumnRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 01 15:37:38 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IColumn;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IColumn} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid/Column">Column</a>,
 * if any.
 *
 * @author katherine
 * @see IColumn
 */
public class IColumnRichletTest extends WebDriverTestCase {
	@Test
	public void sort() {
		connect("/data/iColumn/sort");
		Iterator<JQuery> iteratorDes = jq(".z-rows tr").iterator();
		for (int i = 4; --i > 0 && iteratorDes.hasNext();) {
			assertEquals(String.valueOf(i), iteratorDes.next().text());
		}
		click(jq(".z-column"));
		waitResponse();
		Iterator<JQuery> iteratorASC = jq(".z-rows tr").iterator();
		for (int i = 0; i++ < 3 && iteratorASC.hasNext();) {
			assertEquals(String.valueOf(i), iteratorASC.next().text());
		}
	}
}