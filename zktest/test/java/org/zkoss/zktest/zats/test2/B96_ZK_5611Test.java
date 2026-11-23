/* B96_ZK_5611Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 25 18:07:48 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_5611Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		for (int i = 0; i < 100; ++i)
			click(jq("@button"));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 100; ++i) {
			JQuery children = jq("$hlayout" + i).eq(0).children();
			// check whether the number of children is 3 (all threads exists)
			assertEquals(3, children.length());

			// check the 3 threads ID are 0, 1, 2 (no duplicate)
			HashSet<String> ids = new HashSet<String>();
			for (int j = 0; j < 3; ++j)
				ids.add(children.get(j).firstChild().get("innerHTML"));
			assertEquals(3, ids.size());
		}
	}
}