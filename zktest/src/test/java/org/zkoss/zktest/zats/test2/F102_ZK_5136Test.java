/* F102_ZK_5136Test.java

	Purpose:

	Description:

	History:
		Mon Mar 31 14:23:21 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F102_ZK_5136Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery searchbox = jq(".z-searchbox");
		JQuery showBtn = jq("$showBtn");
		//1. Change search text by enter any value and click "show searchText". You should see value shown in the log.
		click(showBtn);
		waitResponse();
		assertEquals("A", getZKLog());
		closeZKLog();
		click(searchbox);
		waitResponse();
		assertTrue(jq(".z-searchbox-item").isVisible());
		JQuery searchField = jq(".z-searchbox-search");
		type(searchField, "B");
		waitResponse();
		click(showBtn);
		waitResponse();
		assertEquals("B", getZKLog());
		closeZKLog();

		//2. Click "clear searchText" and click "show searchText". You should see the search text become empty -> empty result and empty the log.
		click(jq("$clearBtn"));
		click(searchbox);
		waitResponse();
		assertFalse(jq(".z-searchbox-item").isVisible());
		assertEquals("", searchField.val());
		click(showBtn);
		waitResponse();
		assertEquals("", getZKLog());
		closeZKLog();

		//3. Click "set searchText 'A'" and click "show searchText". You should see the search text become "A" -> result appear and "A" shown in the log.
		click(jq("$changeBtn"));
		click(searchbox);
		waitResponse();
		assertTrue(jq(".z-searchbox-item").isVisible());
		click(showBtn);
		waitResponse();
		assertEquals("A", getZKLog());
	}

	@Test
	public void testMVVM() {
		connect("/test2/F102-ZK-5136-mvvm.zul");
		waitResponse();
		JQuery searchbox = jq(".z-searchbox");
		JQuery searchTextLabel = jq("$searchTextLabel");
		//1. change search text by enter any value, you should see last label change to the same value.
		assertEquals("A", searchTextLabel.text());
		click(searchbox);
		waitResponse();
		assertTrue(jq(".z-searchbox-item").isVisible());
		JQuery searchField = jq(".z-searchbox-search");
		type(searchField, "B");
		waitResponse();
		assertEquals("B", searchTextLabel.text());

		//2. Click "clear searchText" and click "show searchText". You should see the search text become empty -> empty result and empty the log.
		click(jq("$clearBtn"));
		click(searchbox);
		waitResponse();
		assertFalse(jq(".z-searchbox-item").isVisible());
		assertEquals("", searchField.val());
		assertEquals("", searchTextLabel.text());

		//3. Click "set searchText 'A'" and click "show searchText". You should see the search text become "A" -> result appear and "A" shown in the log.
		click(jq("$changeBtn"));
		click(searchbox);
		waitResponse();
		assertTrue(jq(".z-searchbox-item").isVisible());
		assertEquals("A", searchTextLabel.text());
	}
}
