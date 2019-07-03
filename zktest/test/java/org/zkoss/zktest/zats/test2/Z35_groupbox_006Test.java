/* Z35_groupbox_006Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 17:37:26 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

import java.util.List;

public class Z35_groupbox_006Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent btn = desktop.query("button[label='Dynamically remove a few comonenets.']");
		ComponentAgent gb = desktop.query("groupbox");
		List<ComponentAgent> childList = gb.getChildren();
		
		Assert.assertEquals(3, childList.size());
		
		ComponentAgent childDeleted = childList.get(2);
		Assert.assertTrue(gb.getUuid().equals(childDeleted.getParent().getUuid()));
		
		btn.click();
		childList = gb.getChildren();
		
		Assert.assertEquals(2, childList.size());
		Assert.assertNull(childDeleted.getParent());
	}
}
