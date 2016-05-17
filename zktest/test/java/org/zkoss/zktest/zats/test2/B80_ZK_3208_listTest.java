/* B80_ZK_3208_listTest.java

	Purpose:
		
	Description:
		
	History:
		Tue, May 17, 2016  6:45:14 PM, Created by Christopher

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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.ext.Pageable;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_3208_listTest extends ZATSTestCase {
	@Test
	public void test(){
		DesktopAgent desktop = connect();
		Listbox lb1 = desktop.query("#lb1").as(Listbox.class);
		Listbox lb2 = desktop.query("#lb2").as(Listbox.class);
		Pageable model = (Pageable) lb1.getModel();
		Paging pg = desktop.query("#myPaging").as(Paging.class);
		
		//check init status
		assertEquals(5, lb1.getPageSize());
		assertEquals(5, lb2.getPageSize());
		assertEquals(5, model.getPageSize());
		assertEquals(5, pg.getPageSize());
		
		assertEquals(0, lb1.getActivePage());
		assertEquals(0, lb2.getActivePage());
		assertEquals(0, model.getActivePage());
		assertEquals(0, pg.getActivePage());
		
		//switch to 2nd page
		PagingAgent paging = desktop.query("#myPaging").as(PagingAgent.class);
		paging.moveTo(1);
		
		//check result after switching page
		assertEquals(5, lb1.getPageSize());
		assertEquals(5, lb2.getPageSize());
		assertEquals(5, model.getPageSize());
		assertEquals(5, pg.getPageSize());
		
		assertEquals(1, lb1.getActivePage());
		assertEquals(1, lb2.getActivePage());
		assertEquals(1, model.getActivePage());
		assertEquals(1, pg.getActivePage());
		
		//switch back for 1st page
		paging.moveTo(0);
		
		//check result after switching page
		assertEquals(5, lb1.getPageSize());
		assertEquals(5, lb2.getPageSize());
		assertEquals(5, model.getPageSize());
		assertEquals(5, pg.getPageSize());
		
		assertEquals(0, lb1.getActivePage());
		assertEquals(0, lb2.getActivePage());
		assertEquals(0, model.getActivePage());
		assertEquals(0, pg.getActivePage());
	}
}
