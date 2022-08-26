/* B80_ZK_3173_list_norodTest.java

	Purpose:

	Description:

	History:
		Thu, Apr 28, 2016  9:25:37 AM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.PagingAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ext.Pageable;

/**
 *
 * @author Christopher
 */
public class B80_ZK_3173_list_norodTest extends ZATSTestCase {

	/**
	 * Add items, go to last page, clear all.
	 * 
	 * Check the listbox and model have the same active page number and page count at every step
	 */
	@Test
	public void testClear() {
		DesktopAgent desktop = connect();
		Listbox list = desktop.query("#list").as(Listbox.class);
		ListModel model = list.getModel();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		desktop.query("#add10Btn").click();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		PagingAgent paging = desktop.query("listbox > paging").as(PagingAgent.class);
		paging.moveTo(list.getPageCount() - 1);
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		desktop.query("#clearBtn").click();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
	}

	/**
	 * Add items, go to last page, remove one item.
	 * 
	 * Check the list and model have the same active page number and page count at every step
	 */
	@Test
	public void testRemoveOne() {
		DesktopAgent desktop = connect();
		Listbox list = desktop.query("#list").as(Listbox.class);
		ListModel model = list.getModel();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		desktop.query("#add10Btn").click();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		PagingAgent paging = desktop.query("listbox > paging").as(PagingAgent.class);
		paging.moveTo(list.getPageCount() - 1);
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		desktop.query("#remove1Btn").click();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
	}

	/**
	 * Add items, go to last page, increase page size (will reduce page count).
	 * 
	 * Check the list and model have the same active page number and page count at every step
	 */
	@Test
	public void testChangePageSize() {
		DesktopAgent desktop = connect();
		Listbox list = desktop.query("#list").as(Listbox.class);
		ListModel model = list.getModel();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		desktop.query("#add10Btn").click();
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		PagingAgent paging = desktop.query("listbox > paging").as(PagingAgent.class);
		paging.moveTo(list.getPageCount() - 1);
		assertEquals(list.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");

		desktop.query("#set5Btn").click();
		assertEquals(1, list.getActivePage(), "wrong list active page");
		assertEquals(1, ((Pageable) model).getActivePage(), "wrong model active page");
		assertEquals(list.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
	}
}
