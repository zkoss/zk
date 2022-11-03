/* PagingRichletTest.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 2021/12/22, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.supplementary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IPaging;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IPaging} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Paging">Paging</a>,
 * if any.
 * @see IPaging
 * @author jumperchen
 */
public class IPagingRichletTest extends WebDriverTestCase {
	@Test
	public void testIndex() {
		connect("/supplementary/ipaging");
		assertEquals("disabled", jq(".z-paging-first").attr("disabled"));
		assertEquals("disabled", jq(".z-paging-previous").attr("disabled"));
		assertEquals("null", jq(".z-paging-next").attr("disabled"));
		assertEquals("null", jq(".z-paging-last").attr("disabled"));
		assertEquals("1", jq(".z-paging-input").val());
		assertEquals(" / 5", jq(".z-paging-text").text());

		click(jq(".z-paging-next"));
		waitResponse();
		assertEquals("null", jq(".z-paging-first").attr("disabled"));
		assertEquals("null", jq(".z-paging-previous").attr("disabled"));
		assertEquals("2", jq(".z-paging-input").val());
	}

	@Test
	public void testDisabled() {
		connect("/supplementary/ipaging/disabled");
		assertEquals("disabled", jq(".z-paging-first").attr("disabled"));
		assertEquals("disabled", jq(".z-paging-previous").attr("disabled"));
		assertEquals("disabled", jq(".z-paging-next").attr("disabled"));
		assertEquals("disabled", jq(".z-paging-last").attr("disabled"));
		assertEquals("disabled", jq(".z-paging-input").attr("disabled"));
		assertEquals("1", jq(".z-paging-input").val());
		assertEquals(" / 50", jq(".z-paging-text").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("null", jq(".z-paging-next").attr("disabled"));
		assertEquals("null", jq(".z-paging-last").attr("disabled"));
		assertEquals("null", jq(".z-paging-input").attr("disabled"));
		click(jq(".z-paging-next"));
		waitResponse();
		assertEquals("null", jq(".z-paging-first").attr("disabled"));
		assertEquals("null", jq(".z-paging-previous").attr("disabled"));
	}

	@Test
	public void testOsMold() {
		connect("/supplementary/ipaging/os");
		assertEquals("1", jq(".z-paging-selected").text());
		Iterator<JQuery> iterator = jq(".z-paging-button").iterator();
		String[] result = {"1", "2", "3", "4", "5", "Next"};
		int index = 0;
		while (iterator.hasNext()) {
			assertEquals(result[index++], iterator.next().text());
		}
		click(jq(".z-paging-button:eq(3)"));
		waitResponse();
		iterator = jq(".z-paging-button").iterator();
		String[] result2 = {"Prev", "1", "2", "3", "4", "5", "Next"};
		index = 0;
		while (iterator.hasNext()) {
			assertEquals(result2[index++], iterator.next().text());
		}
		assertEquals("4", jq(".z-paging-selected").text());
		assertEquals(1, jq(".z-paging-selected").length());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-paging-os").exists());
	}

	@Test
	public void testPagingEvent() {
		connect("/supplementary/ipaging/pagingEvent");
		Iterator<JQuery> iterator = jq(".z-vlayout .z-label").iterator();
		int i = 0;
		while (iterator.hasNext()) {
			assertEquals(String.valueOf(i++), iterator.next().text());
		}
		assertEquals(20, i);
		click(jq(".z-paging-next"));
		waitResponse();
		iterator = jq(".z-vlayout .z-label").iterator();
		while (iterator.hasNext()) {
			assertEquals(String.valueOf(i++), iterator.next().text());
		}
		assertEquals(40, i);

		click(jq(".z-paging-previous"));
		waitResponse();
		iterator = jq(".z-vlayout .z-label").iterator();
		i = 0;
		while (iterator.hasNext()) {
			assertEquals(String.valueOf(i++), iterator.next().text());
		}
		assertEquals(20, i);
	}

	@Test
	public void testId() {
		connect("/supplementary/ipaging/id");
		assertTrue(jq("$mypaging").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq("$mypaging").exists());
		assertTrue(jq("$mypaging2").exists());
	}

	@Test
	public void testTotalSize() {
		connect("/supplementary/ipaging/totalSize");
		assertEquals("disabled", jq(".z-paging-first").attr("disabled"));
		assertEquals("disabled", jq(".z-paging-previous").attr("disabled"));
		assertEquals("null", jq(".z-paging-next").attr("disabled"));
		assertEquals("null", jq(".z-paging-last").attr("disabled"));
		assertEquals("1", jq(".z-paging-input").val());
		assertEquals(" / 5", jq(".z-paging-text").text());

		click(jq(".z-paging-next"));
		waitResponse();
		assertEquals("null", jq(".z-paging-first").attr("disabled"));
		assertEquals("null", jq(".z-paging-previous").attr("disabled"));
		assertEquals("2", jq(".z-paging-input").val());
		click(jq("@button"));
		waitResponse();
		assertEquals(" / 3", jq(".z-paging-text").text());
	}

	@Test
	public void testPageCount() {
		connect("/supplementary/ipaging/pageCount");
		assertEquals(" / 10", jq(".z-paging-text").text());
		click(jq("@button"));
		waitResponse();
		assertEquals(" / 20", jq(".z-paging-text").text());
	}

	@Test
	public void testAutohide() {
		connect("/supplementary/ipaging/autohide");
		assertTrue(jq("@paging").exists());
		assertFalse(jq("@paging").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq("@paging").isVisible());
	}

	@Test
	public void testPageIncrement() {
		connect("/supplementary/ipaging/pageIncrement");
		assertEquals(6, jq(".z-paging-button").length());
		click(jq("@button"));
		waitResponse();
		assertEquals(5, jq(".z-paging-button").length());
	}
	@Test
	public void activePage() {
		connect("/supplementary/ipaging/activePage");
		assertEquals("3", jq(".z-paging-input").val());
		click(jq("@button"));
		waitResponse();
		assertEquals("4", jq(".z-paging-input").val());
	}

	@Test
	public void pageSize() {
		connect("/supplementary/ipaging/pageSize");
		assertEquals(" / 20", jq(".z-paging-text").text());
		click(jq("@button"));
		waitResponse();
		assertEquals(" / 10", jq(".z-paging-text").text());
	}
}
