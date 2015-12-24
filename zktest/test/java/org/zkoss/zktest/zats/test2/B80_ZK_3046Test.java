/* B80_ZK_3046Test.java

	Purpose:
		
	Description:
		
	History:
		5:46 PM 12/24/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B80_ZK_3046Test extends WebDriverTestCase {
	@Test public void testZK3046() {
		connect();
		JQuery groups = jq("@groupbox");
		assertEquals(7, groups.length());
		for (JQuery group : groups) {
			JQuery buttons = group.find("button");
			assertEquals(5, buttons.length());
			for (JQuery button : buttons) {
				String text = button.text();
				assertTrue(text.startsWith("zh"));
				click(button);
				waitResponse();
				assertEquals(text, getZKLog());
				closeZKLog();
			}
		}
	}
	@Test public void testZK3046_1() {
		connect(getTestURL("B80-ZK-3046-1.zul"));
		JQuery groups = jq("@groupbox");
		assertEquals(1, groups.length());
		for (JQuery group : groups) {
			JQuery buttons = group.find("button");
			assertEquals(5, buttons.length());
			for (JQuery button : buttons) {
				String text = button.text();
				assertTrue(text.startsWith("zh"));
				click(button);
				waitResponse();
				assertEquals(text, getZKLog());
				closeZKLog();
			}
		}
	}
	@Test public void testZK3046_2() {
		connect(getTestURL("B80-ZK-3046-2.zul"));
		JQuery groups = jq("@groupbox");
		assertEquals(1, groups.length());
		for (JQuery group : groups) {
			JQuery buttons = group.find("button");
			assertEquals(5, buttons.length());
			for (JQuery button : buttons) {
				String text = button.text();
				assertTrue(text.startsWith("zh"));
				click(button);
				waitResponse();
				assertEquals(text, getZKLog());
				closeZKLog();
			}
		}
	}
	@Test public void testZK3046_3() {
		connect(getTestURL("B80-ZK-3046-3.zul"));
		JQuery groups = jq("@groupbox");
		assertEquals(1, groups.length());
		for (JQuery group : groups) {
			JQuery buttons = group.find("button");
			assertEquals(5, buttons.length());
			for (JQuery button : buttons) {
				String text = button.text();
				assertTrue(text.startsWith("zh"));
				click(button);
				waitResponse();
				assertEquals(text, getZKLog());
				closeZKLog();
			}
		}
	}
	@Test public void testZK3046_4() {
		connect(getTestURL("B80-ZK-3046-4.zul"));
		JQuery groups = jq("@groupbox");
		assertEquals(1, groups.length());
		for (JQuery group : groups) {
			JQuery buttons = group.find("button");
			assertEquals(5, buttons.length());
			for (JQuery button : buttons) {
				String text = button.text();
				assertTrue(text.startsWith("zh"));
				click(button);
				waitResponse();
				assertEquals(text, getZKLog());
				closeZKLog();
			}
		}
	}
}
