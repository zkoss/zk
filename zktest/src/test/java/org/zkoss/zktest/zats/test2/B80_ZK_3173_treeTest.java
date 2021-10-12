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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.PagingAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.ext.Pageable;

/**
 *
 * @author Christopher
 */
public class B80_ZK_3173_treeTest extends ZATSTestCase {
	
	/**
	 * Add items, go to last page, clear all.
	 * 
	 * Check the tree and model have the same active page number and page count at every step
	 */
	@Test
	public void testClear() {
		DesktopAgent desktop = connect();
		Tree tree = desktop.query("#tree").as(Tree.class);
		TreeModel model = tree.getModel();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		desktop.query("#add10Btn").click();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		PagingAgent paging = desktop.query("tree > paging").as(PagingAgent.class);
		paging.moveTo(tree.getPageCount() - 1);
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		desktop.query("#clearBtn").click();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
	}

	/**
	 * Add items, go to last page, remove one item.
	 * 
	 * Check the tree and model have the same active page number and page count at every step
	 */
	@Test
	public void testRemoveOne() {
		DesktopAgent desktop = connect();
		Tree tree = desktop.query("#tree").as(Tree.class);
		TreeModel model = tree.getModel();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		desktop.query("#add10Btn").click();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		PagingAgent paging = desktop.query("tree > paging").as(PagingAgent.class);
		paging.moveTo(tree.getPageCount() - 1);
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		desktop.query("#remove1Btn").click();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
	}

	/**
	 * Add items, go to last page, increase page size (will reduce page count).
	 * 
	 * Check the tree and model have the same active page number and page count at every step
	 */
	@Test
	public void testChangePageSize() {
		DesktopAgent desktop = connect();
		Tree tree = desktop.query("#tree").as(Tree.class);
		TreeModel model = tree.getModel();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		desktop.query("#add10Btn").click();
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		PagingAgent paging = desktop.query("tree > paging").as(PagingAgent.class);
		paging.moveTo(tree.getPageCount() - 1);
		assertEquals("active page out of sync", tree.getActivePage(), ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
		
		desktop.query("#set5Btn").click();
		assertEquals("wrong tree active page", 3, tree.getActivePage());
		assertEquals("wrong model active page", 3, ((Pageable) model).getActivePage());
		assertEquals("page count out of sync", tree.getPageCount(), ((Pageable) model).getPageCount());
	}
}
