/* B96_ZK_3563Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 04 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B96_ZK_4791Test extends ZATSTestCase {
	@Test
	public void testDetachViewModelComponent() {
		DesktopAgent desktop = connect();
		ComponentAgent showBtn = desktop.query("#show");
		showBtn.click();
		Label label = desktop.query("#result").as(Label.class);
		Assert.assertEquals("true", label.getValue());
		desktop.query("#rmVm").click();
		showBtn.click();
		Assert.assertEquals("false", label.getValue());
	}

	@Test
	public void testDetachParentComponent() {
		DesktopAgent desktop = connect();
		ComponentAgent showBtn = desktop.query("#show");
		showBtn.click();
		Label label = desktop.query("#result").as(Label.class);
		Assert.assertEquals("true", label.getValue());
		desktop.query("#rmParent").click();
		showBtn.click();
		Assert.assertEquals("false", label.getValue());
	}

	@Test
	public void testDetachBothComponents() {
		DesktopAgent desktop = connect();
		ComponentAgent showBtn = desktop.query("#show");
		showBtn.click();
		Label label = desktop.query("#result").as(Label.class);
		Assert.assertEquals("true", label.getValue());
		desktop.query("#rmVm").click();
		desktop.query("#rmParent").click();
		showBtn.click();
		Assert.assertEquals("false", label.getValue());
	}
}
