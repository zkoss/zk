/* B85_ZK_3738Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 30 5:35 PM:32 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import java.util.List;

public class B85_ZK_3738Test extends ZATSTestCase {

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent selectAllbtn = desktop.query("button");
		ComponentAgent label = desktop.query("#l1");
		List<ComponentAgent> treeItems = desktop.queryAll("treeitem");
		selectAllbtn.click();
		treeItems.get(0).as(MultipleSelectAgent.class).deselect();
		Assert.assertEquals("[1.1, 1.2, 2.1, 2.2, 2.3, 2, 3.1, 3]",label.as(Label.class).getValue());

	}

}
