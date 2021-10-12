package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_3103Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery selBtn = jq("$select");
		JQuery clearBtn = jq("$clear");
		JQuery intbox = jq("@intbox").eq(0);
		sendKeys(intbox, "2");
		waitResponse(true);
		click(selBtn);
		waitResponse(true);
		for (int i = 0; i <=3; i++) {
			assertTrue(jq(".z-listitem-selected div:eq(" + i + ")").text().contains("Item 2"));
		}
		click(clearBtn);
		waitResponse(true);
		sendKeys(intbox, "18");
		waitResponse(true);
		click(selBtn);
		waitResponse(true);
		for (int i = 0; i <=3; i++) {
			assertTrue(jq(".z-listitem-selected div:eq(" + i + ")").text().contains("Item 18"));
		}
		click(clearBtn);
		waitResponse(true);
		sendKeys(intbox, "21");
		waitResponse(true);
		click(selBtn);
		waitResponse(true);
		for (int i = 0; i <=3; i++) {
			assertTrue(jq(".z-listitem-selected div:eq(" + i + ")").text().contains("Item 21"));
			JQuery lbb = jq(".z-listbox-body:eq(" + i + ")");
			JQuery li = jq(".z-listitem:contains(Item 21):eq(" + i + ")");
			assertTrue(lbb.offsetTop() + lbb.height() >= li.offsetTop() + li.height());
			assertTrue(lbb.offsetTop() <= li.offsetTop());
		}
		click(clearBtn);
		waitResponse(true);
		sendKeys(intbox, "17");
		waitResponse(true);
		click(selBtn);
		waitResponse(true);
		waitResponse(true);
		for (int i = 0; i <=3; i++) {
			assertTrue(jq(".z-listitem-selected div:eq(" + i + ")").text().contains("Item 17"));
			JQuery lbb = jq(".z-listbox-body:eq(" + i + ")");
			JQuery li = jq(".z-listitem:contains(Item 17):eq(" + i + ")");
			assertTrue(lbb.offsetTop() + lbb.height() >= li.offsetTop() + li.height());
			assertTrue(lbb.offsetTop() <= li.offsetTop());
		}
		for (int i = 0; i <=3; i++) {
			jq(".z-listbox-body").eq(i).scrollTop(0);
			waitResponse(true);
		}
		for (int i = 0; i <=2; i++) {
			click(jq(".z-paging-next i:eq(" + i + ")"));
			waitResponse(true);
		}
		for (int i = 0; i <=3; i++) {
			JQuery lbb = jq(".z-listbox-body:eq(" + i + ")");
			JQuery li = jq(".z-listitem:contains(Item 20):eq(" + i + ")");
			assertEquals(lbb.offsetTop(), li.offsetTop());
		}
		for (int i = 0; i <=2; i++) {
			click(jq(".z-paging-previous i:eq(" + i + ")"));
			waitResponse(true);
		}
		for (int i = 0; i <=3; i++) {
			JQuery lbb = jq(".z-listbox-body:eq(" + i + ")");
			JQuery li = jq(".z-listitem:contains(Item 0):eq(" + i + ")");
			assertEquals(lbb.offsetTop(), li.offsetTop());
		}
	}
}
