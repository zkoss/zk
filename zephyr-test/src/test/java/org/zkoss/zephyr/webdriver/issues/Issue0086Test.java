/* Issue0086Test.java

	Purpose:
		
	Description:
		
	History:
		2:26 PM 2021/12/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class Issue0086Test extends WebDriverTestCase {
	@Test
	public void testTreePagingMold() {
		connect("/issues/issue_0086.zpr");
		Iterator<JQuery> iterator = jq(
				".z-treerow .z-treecell-text").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		validateText(iterator, IntStream.range(0, 9).mapToObj((i) -> String.valueOf(i)).collect(
				Collectors.toList()).toArray(new String[0]));
		assertEquals("[ 1 - 10 / 21 ]", jq(".z-paging-info").text());

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 11 - 20 / 21 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), IntStream.range(9, 19).mapToObj((i) -> String.valueOf(i)).collect(
				Collectors.toList()).toArray(new String[0]));

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 21 - 21 / 21 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {"19"});

		click(jq(".z-paging-button.z-paging-first"));
		waitResponse();
		// open treeitem
		click(jq(".z-icon-caret-right.z-tree-close").eq(0));
		waitResponse();

		iterator = jq(
				".z-treerow .z-treecell-text").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		validateText(iterator, IntStream.range(0, 9).mapToObj((i) -> String.valueOf(i)).collect(
				Collectors.toList()).toArray(new String[0]));
		assertEquals("[ 1 - 10 / 41 ]", jq(".z-paging-info").text());

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 11 - 20 / 41 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), IntStream.range(9, 19).mapToObj((i) -> String.valueOf(i)).collect(
				Collectors.toList()).toArray(new String[0]));

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 21 - 30 / 41 ]", jq(".z-paging-info").text());
		iterator = jq(
				".z-treerow .z-treecell-text").iterator();

		iterator.hasNext();
		assertEquals("19", iterator.next().text());
		validateText(iterator, IntStream.range(0, 9).mapToObj((i) -> String.valueOf(i)).collect(
				Collectors.toList()).toArray(new String[0]));

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 31 - 40 / 41 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), IntStream.range(9, 19).mapToObj((i) -> String.valueOf(i)).collect(
				Collectors.toList()).toArray(new String[0]));
	}

	private void validateText(Iterator<JQuery> iterator, String[] values) {
		for (int from = 0, to = values.length; from < to; from++) {
			assertTrue(iterator.hasNext());
			JQuery next = iterator.next();
			assertEquals(values[from], next.text());
			assertTrue(next.prev().find(".z-tree-close").exists());
		}
	}


	@Test
	public void testTreePagingMoldWithSort() {
		connect("/issues/issue_0086.zpr");
		clickAt(jq(".z-treecol-content"), 5, 5);
		waitResponse();
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"0", "1", "10", "11", "12", "13", "14", "15", "16", "17"
		});

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 11 - 20 / 21 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"18", "19", "2", "3", "4", "5", "6", "7", "8", "9"
		});

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 21 - 21 / 21 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"Visible ROOT"
		});

		click(jq(".z-icon-caret-right.z-tree-close").eq(0));
		waitResponse();

		assertEquals("[ 21 - 30 / 41 ]", jq(".z-paging-info").text());
		Iterator<JQuery> iterator = jq(
				".z-treerow .z-treecell-text").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		validateText(iterator, new String[] {
				"0", "1", "10", "11", "12", "13", "14", "15", "16"
		});

		click(jq(".z-paging-icon.z-icon-angle-right"));
		waitResponse();
		assertEquals("[ 31 - 40 / 41 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"17", "18", "19", "2", "3", "4", "5", "6", "7", "8"
		});

		clickAt(jq(".z-treecol-content"), 5, 5);
		waitResponse();
		assertEquals("[ 31 - 40 / 41 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"18", "17", "16", "15", "14", "13", "12", "11", "10", "1"
		});

		click(jq(".z-paging-icon.z-icon-angle-left"));
		waitResponse();
		assertEquals("[ 21 - 30 / 41 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"0", "9", "8", "7", "6", "5", "4", "3", "2", "19"
		});

		click(jq(".z-paging-icon.z-icon-angle-left"));
		waitResponse();
		assertEquals("[ 11 - 20 / 41 ]", jq(".z-paging-info").text());
		validateText(jq(".z-treerow .z-treecell-text").iterator(), new String[] {
				"18", "17", "16", "15", "14", "13", "12", "11", "10", "1"
		});

		click(jq(".z-paging-icon.z-icon-angle-left"));
		waitResponse();
		assertEquals("[ 1 - 10 / 41 ]", jq(".z-paging-info").text());
		iterator = jq(".z-treerow .z-treecell-text").iterator();
		iterator.hasNext();
		assertEquals("Visible ROOT", iterator.next().text());
		validateText(iterator, new String[] {
				"9", "8", "7", "6", "5", "4", "3", "2", "19"
		});
	}
}
