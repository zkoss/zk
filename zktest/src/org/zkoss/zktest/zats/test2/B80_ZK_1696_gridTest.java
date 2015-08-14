/* B80_ZK_1696_gridTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 12 17:55:08 CST 2015, Created by Christopher

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.PagingAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;

import junit.framework.Assert;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_1696_gridTest extends ZATSTestCase {
	@Test
	public void test(){
		DesktopAgent desktop = connect();
		ComponentAgent gridAgent = desktop.query("#grid");
		Grid mygrid = gridAgent.as(Grid.class);
		Assert.assertNotNull(mygrid);
		
		//check default render result
		Assert.assertEquals(0, mygrid.getActivePage());
		List<ComponentAgent> rows = gridAgent.queryAll("row");
		Assert.assertEquals("item 1", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item 2", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item 3", rows.get(2).as(Row.class).getValue());
		
		//switch to 2nd page
		PagingAgent paging = desktop.query("grid > paging").as(PagingAgent.class);
		paging.moveTo(1);
		//check that render result after switching page
		Assert.assertEquals(1, mygrid.getActivePage());
		rows = gridAgent.queryAll("row");
		Assert.assertEquals("item 4", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item 5", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item 6", rows.get(2).as(Row.class).getValue());
		
		desktop.query("#btn4").click(); //switch to 2nd model
		//check render result after changing model
		Assert.assertEquals(0, mygrid.getActivePage());
		rows = gridAgent.queryAll("row");
		Assert.assertEquals("item a", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item b", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item c", rows.get(2).as(Row.class).getValue());
		
		desktop.query("#btn1").click(); //switch back to 1st model
		//check render result after switching back to original model
		Assert.assertEquals(1, mygrid.getActivePage());
		rows = gridAgent.queryAll("row");
		Assert.assertEquals("item 4", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item 5", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item 6", rows.get(2).as(Row.class).getValue());
	}
	
	@Test
	public void testApi(){
		DesktopAgent desktop = connect();
		ComponentAgent gridAgent = desktop.query("#grid");
		Grid mygrid = gridAgent.as(Grid.class);
		Assert.assertNotNull(mygrid);
		
		//check default render result
		Assert.assertEquals(0, mygrid.getActivePage());
		List<ComponentAgent> rows = gridAgent.queryAll("row");
		Assert.assertEquals("item 1", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item 2", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item 3", rows.get(2).as(Row.class).getValue());
		
		desktop.query("#btn4").click(); //switch to 2nd model
		//check render result after changing model
		Assert.assertEquals(0, mygrid.getActivePage());
		rows = gridAgent.queryAll("row");
		Assert.assertEquals("item a", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item b", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item c", rows.get(2).as(Row.class).getValue());

		desktop.query("#btn2").click(); //switch back to 1st model
		//check render result after switching back to original model
		Assert.assertEquals(1, mygrid.getActivePage());
		rows = gridAgent.queryAll("row");
		Assert.assertEquals("item 4", rows.get(0).as(Row.class).getValue());
		Assert.assertEquals("item 5", rows.get(1).as(Row.class).getValue());
		Assert.assertEquals("item 6", rows.get(2).as(Row.class).getValue());
	}
}
