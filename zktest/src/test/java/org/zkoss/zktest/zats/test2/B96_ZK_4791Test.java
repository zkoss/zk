/* B96_ZK_3563Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 04 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.zkoss.bind.sys.BinderCtrl;
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
		assertFalse(isVmIdBinderMapEmpty(desktop));
		desktop.query("#deVm").click();
		assertTrue(isVmIdBinderMapEmpty(desktop));
	}

	@Test
	public void testDetachParentComponent() {
		DesktopAgent desktop = connect();
		assertFalse(isVmIdBinderMapEmpty(desktop));
		desktop.query("#deParent").click();
		assertTrue(isVmIdBinderMapEmpty(desktop));
	}

	@Test
	public void testDetachBothComponents() {
		DesktopAgent desktop = connect();
		assertFalse(isVmIdBinderMapEmpty(desktop));
		desktop.query("#deVmThenParent").click();
		assertTrue(isVmIdBinderMapEmpty(desktop));
	}

	@Test
	public void testDetachBothComponentsReversed() {
		DesktopAgent desktop = connect();
		assertFalse(isVmIdBinderMapEmpty(desktop));
		desktop.query("#deParentThenVm").click();
		assertTrue(isVmIdBinderMapEmpty(desktop));
	}

	private boolean isVmIdBinderMapEmpty(DesktopAgent desktopAgent) {
		return !((Map) desktopAgent.getDesktop().getAttribute(BinderCtrl.VIEWMODELID_BINDER_MAP_KEY)).containsKey("vm");
	}

	@Test
	public void testDetachBothComponentsAndAttachBack() {
		DesktopAgent desktop = connect();
		ComponentAgent directCmdBtn = desktop.query("#callCmdBtn");
		ComponentAgent callPVMCmdBtn = desktop.query("#callPVMCmdBtn");
		ComponentAgent label = desktop.query("#count");
		directCmdBtn.click();
		assertEquals("1", label.as(Label.class).getValue());
		callPVMCmdBtn.click();
		assertEquals("2", label.as(Label.class).getValue());

		desktop.query("#deParentThenAttachBack").click();
		directCmdBtn.click();
		assertEquals("3", label.as(Label.class).getValue());
		callPVMCmdBtn.click();
		assertEquals("4", label.as(Label.class).getValue());

		desktop.query("#deAllThenAttachBackChildFirst").click();
		directCmdBtn.click();
		assertEquals("5", label.as(Label.class).getValue());
		callPVMCmdBtn.click();
		assertEquals("6", label.as(Label.class).getValue());
	}
}
