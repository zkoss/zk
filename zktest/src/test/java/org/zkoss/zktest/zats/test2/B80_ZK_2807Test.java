/* B80_ZK_2807Test.java

	Purpose:
		
	Description:
		
	History:
		11:59 AM 7/30/15, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

/**
 * @author Christopher
 */
public class B80_ZK_2807Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Grid grid = desktop.query("#grid").as(Grid.class);
		List<String> before = new ArrayList<String>();
		List<String> expectedBefore = new ArrayList<String>(Arrays.asList("false", "true", "false"));
		
		for (Object r : grid.getRows().getChildren()) {
			Row row = (Row)r;
			Label l = (Label)row.getLastChild();
			before.add(l.getValue());
		}
		Assert.assertEquals(expectedBefore, before);
		
		desktop.query("#btn1").click();
		
		List<String> after = new ArrayList<String>();
		List<String> expectedAfter = new ArrayList<String>(Arrays.asList("true", "true", "true"));
		
		for (Object r : grid.getRows().getChildren()) {
			Row row = (Row)r;
			Label l = (Label)row.getLastChild();
			after.add(l.getValue());
		}
		Assert.assertEquals(expectedAfter, after);
	}
}
