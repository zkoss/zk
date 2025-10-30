/* B80_ZK_3173Test.java

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
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ext.Pageable;

/**
 *
 * @author Christopher
 */
public class B80_ZK_3173_gridTest extends ZATSTestCase {
	
	/**
	 * Add items, go to last page, clear all.
	 * 
	 * Check the grid and model have the same active page number and page count at every step
	 */
	@Test
	public void testClear() {
		DesktopAgent desktop = connect();
		Grid grid = desktop.query("#grid").as(Grid.class);
		ListModel model = grid.getModel();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		desktop.query("#add10Btn").click();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		PagingAgent paging = desktop.query("grid > paging").as(PagingAgent.class);
		paging.moveTo(grid.getPageCount() - 1);
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		desktop.query("#clearBtn").click();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
	}

	/**
	 * Add items, go to last page, remove one item.
	 * 
	 * Check the grid and model have the same active page number and page count at every step
	 */
	@Test
	public void testRemoveOne() {
		DesktopAgent desktop = connect();
		Grid grid = desktop.query("#grid").as(Grid.class);
		ListModel model = grid.getModel();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		desktop.query("#add10Btn").click();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		PagingAgent paging = desktop.query("grid > paging").as(PagingAgent.class);
		paging.moveTo(grid.getPageCount() - 1);
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		desktop.query("#remove1Btn").click();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
	}

	/**
	 * Add items, go to last page, increase page size (will reduce page count).
	 * 
	 * Check the grid and model have the same active page number and page count at every step
	 */
	@Test
	public void testChangePageSize() {
		DesktopAgent desktop = connect();
		Grid grid = desktop.query("#grid").as(Grid.class);
		ListModel model = grid.getModel();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		desktop.query("#add10Btn").click();
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		PagingAgent paging = desktop.query("grid > paging").as(PagingAgent.class);
		paging.moveTo(grid.getPageCount() - 1);
		assertEquals(grid.getActivePage(), ((Pageable) model).getActivePage(), "active page out of sync");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
		
		desktop.query("#set5Btn").click();
		assertEquals(1, grid.getActivePage(), "wrong grid active page");
		assertEquals(1, ((Pageable) model).getActivePage(), "wrong model active page");
		assertEquals(grid.getPageCount(), ((Pageable) model).getPageCount(), "page count out of sync");
	}
}
