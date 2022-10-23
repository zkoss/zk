/* Issue0105PagingTest.java

	Purpose:

	Description:

	History:
		6:13 PM 2022/3/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class Issue0105PagingTest extends WebDriverTestCase {
	@Test
	public void test0105Grid() {
		connect("/issue0105/grid");
		assertNoAnyError();
		validate(0);
		click(jq(".z-paging-next"));
		validate(5);
		click(jq(".z-paging-previous"));
		validate(1);
		assertNoAnyError();
	}

	@Test
	public void test0105Listbox() {
		connect("/issue0105/listbox");
		assertNoAnyError();
		validate(0);
		click(jq(".z-paging-next"));
		validate(5);
		click(jq(".z-paging-previous"));
		validate(1);
		assertNoAnyError();
	}

	@Test
	public void test0105Tree() {
		connect("/issue0105/tree");
		assertNoAnyError();
		validate(0);
		click(jq(".z-paging-next"));
		validate(5);
		click(jq(".z-paging-next"));
		validate(15);
		click(jq(".z-paging-previous"));
		validate(1);
		assertNoAnyError();
	}

	private void validate(int count) {
		Iterator<JQuery> iterator = jq(".z-textbox").iterator();
		while (iterator.hasNext()) {
			type(iterator.next(), String.valueOf(count));
			waitResponse();
			assertEquals(String.valueOf(count), jq("$msg").text());
			count++;
		}
	}
}
