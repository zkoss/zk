/* IListheaderRichletTest.java

	Purpose:

	Description:

	History:
		Fri Apr 08 15:38:30 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IColumns;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IColumns} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listheader">Listheader</a>,
 * if any.
 *
 * @author katherine
 * @see IColumns
 */
public class IListheaderRichletTest extends WebDriverTestCase {
	@Test
	public void sort() {
		connect("/data/iListheader/sort");
		Iterator<JQuery> iteratorDes = jq(".z-listitem").iterator();
		for (int i = 4; --i > 0 && iteratorDes.hasNext();) {
			assertEquals(String.valueOf(i), iteratorDes.next().text());
		}
		click(jq(".z-listheader"));
		waitResponse();
		Iterator<JQuery> iteratorASC = jq(".z-listitem").iterator();
		for (int i = 0; i++ < 3 && iteratorASC.hasNext();) {
			assertEquals(String.valueOf(i), iteratorASC.next().text());
		}
	}
}