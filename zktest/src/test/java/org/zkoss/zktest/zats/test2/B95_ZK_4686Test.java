/* B95_ZK_4686Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B95_ZK_4686Test extends ZATSTestCase {
	@Test
	public void testRemoveBeanFirst() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent showCacheSizeBtn = desktop.query("#showCacheSizeBtn");
		showCacheSizeBtn.click();
		Label nodeCacheSize = desktop.query("#nodeCacheSize").as(Label.class);
		Assertions.assertEquals("1", nodeCacheSize.getValue());
		desktop.query("#rmBtn1").click();
		showCacheSizeBtn.click();
		Assertions.assertEquals("1", nodeCacheSize.getValue());
		Label target2 = desktop.query("#target2").as(Label.class);
		ComponentAgent chgToViewBtn = desktop.query("#chgToViewBtn");
		String target2Val1 = target2.getValue();
		chgToViewBtn.click();
		String target2Val2 = target2.getValue();
		Assertions.assertNotEquals(target2Val1, target2Val2);
		desktop.query("#chgToDefaultBtn").click();
		target2Val1 = target2Val2;
		target2Val2 = target2.getValue();
		Assertions.assertNotEquals(target2Val1, target2Val2);
		chgToViewBtn.click();
		target2Val1 = target2Val2;
		target2Val2 = target2.getValue();
		Assertions.assertNotEquals(target2Val1, target2Val2);
		desktop.query("#rmBtn2").click();
		showCacheSizeBtn.click();
		Assertions.assertEquals("0", nodeCacheSize.getValue());
	}

	@Test
	public void testRemoveBeanPropertyFirst() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent showCacheSizeBtn = desktop.query("#showCacheSizeBtn");
		showCacheSizeBtn.click();
		Label nodeCacheSize = desktop.query("#nodeCacheSize").as(Label.class);
		Assertions.assertEquals("1", nodeCacheSize.getValue());
		desktop.query("#rmBtn2").click();
		showCacheSizeBtn.click();
		Assertions.assertEquals("1", nodeCacheSize.getValue());
		Label target1 = desktop.query("#target1").as(Label.class);
		ComponentAgent chgToViewBtn = desktop.query("#chgToViewBtn");
		String target1Val1 = target1.getValue();
		chgToViewBtn.click();
		String target1Val2 = target1.getValue();
		Assertions.assertNotEquals(target1Val1, target1Val2);
		desktop.query("#chgToDefaultBtn").click();
		target1Val1 = target1Val2;
		target1Val2 = target1.getValue();
		Assertions.assertNotEquals(target1Val1, target1Val2);
		chgToViewBtn.click();
		target1Val1 = target1Val2;
		target1Val2 = target1.getValue();
		Assertions.assertNotEquals(target1Val1, target1Val2);
		desktop.query("#rmBtn1").click();
		showCacheSizeBtn.click();
		Assertions.assertEquals("0", nodeCacheSize.getValue());
	}
}
