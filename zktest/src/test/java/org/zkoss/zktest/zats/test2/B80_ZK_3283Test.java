/* B80_ZK_3282Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Aug 23, 2016 12:17:13 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkmax.au.InaccessibleWidgetBlockService;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zkmax.zul.Timepicker;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.event.ZulEvents;

/**
 * 
 * @author christopher
 */

public class B80_ZK_3283Test extends ZATSTestCase {

	private InaccessibleWidgetBlockService blockService;

	@BeforeEach
	public void setup() {
		blockService = new InaccessibleWidgetBlockService();
	}

	@Test
	public void testListbox() throws IllegalAccessException, InvocationTargetException {
		testComponent(new Listbox(), Events.ON_SELECT, true);
	}

	@Test
	public void testSelectbox() throws IllegalAccessException, InvocationTargetException {
		testComponent(new Selectbox(), Events.ON_SELECT, true);
	}

	@Test
	public void testChosenbox() throws IllegalAccessException, InvocationTargetException {
		testComponent(new Chosenbox(), Events.ON_SELECT, true);
	}

	@Test
	public void testTreeitem() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Treeitem(), Events.ON_SELECT, true);
		testComponent(new Treeitem(), Events.ON_CLICK, true);
		testComponent(new Treeitem(), Events.ON_OPEN, false);
		testComponent(new Treeitem(), Events.ON_DOUBLE_CLICK, true);
	}

	@Test
	public void testPaging() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Paging(), Events.ON_CLICK, true);
		testComponent(new Paging(), ZulEvents.ON_PAGING, true);
		testComponent(new Paging(), ZulEvents.ON_PAGE_SIZE, true);
	}

	@Test
	public void testTextbox() throws IllegalAccessException, InvocationTargetException {
		testComponent(new Textbox(), Events.ON_CHANGE, true);
		testComponent(new Textbox(), Events.ON_CHANGING, true);
		testComponent(new Textbox(), Events.ON_SELECTION, true);
	}

	@Test
	public void testCombobox() throws IllegalAccessException, InvocationTargetException {
		testComponent(new Combobox(), Events.ON_SELECT, true);
		testComponent(new Combobox(), Events.ON_CHANGE, true);
	}

	@Test
	public void testComboitem() throws IllegalAccessException, InvocationTargetException {
		testComponent(new Comboitem(), Events.ON_CLICK, true);
		testComponent(new Comboitem(), Events.ON_DOUBLE_CLICK, true);
	}

	@Test
	public void testMenuitem() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Menuitem(), Events.ON_CHECK, true);
		testComponent(new Menuitem(), Events.ON_SELECT, true);
	}

	@Test
	public void testNavitem() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Navitem(), Events.ON_SELECT, true);
	}

	@Test
	public void testCheckbox() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Checkbox(), Events.ON_CHECK, true);
	}

	@Test
	public void testRadio() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Radio(), Events.ON_CHECK, true);
	}

	@Test
	public void testCombobutton() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Combobutton(), Events.ON_CLICK, true);
	}

	@Test
	public void testToolbarbutton() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Toolbarbutton(), Events.ON_CLICK, true);
		testComponent(new Toolbarbutton(), Events.ON_CHECK, true);
	}

	@Test
	public void testButton() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Button(), Events.ON_CLICK, true);
		testComponent(new Button(), Events.ON_DOUBLE_CLICK, true);
	}

	@Test
	public void testDatebox() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Datebox(), Events.ON_CLICK, true);
		testComponent(new Datebox(), Events.ON_DOUBLE_CLICK, true);
		testComponent(new Datebox(), Events.ON_CHANGE, true);
		testComponent(new Datebox(), Events.ON_CHANGING, true);
		testComponent(new Datebox(), Events.ON_SELECT, true);
		testComponent(new Datebox(), Events.ON_SELECTION, true);
	}

	@Test
	public void testTimepicker() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Timepicker(), Events.ON_CLICK, true);
		testComponent(new Timepicker(), Events.ON_DOUBLE_CLICK, true);
		testComponent(new Timepicker(), Events.ON_SELECTION, true);
	}

	@Test
	public void testTimebox() throws InvocationTargetException, IllegalAccessException {
		testComponent(new Timebox(), Events.ON_CLICK, true);
		testComponent(new Timebox(), Events.ON_DOUBLE_CLICK, true);
		testComponent(new Timebox(), Events.ON_CHANGE, true);
		testComponent(new Timebox(), Events.ON_CHANGING, true);
		testComponent(new Timebox(), Events.ON_SELECT, true);
		testComponent(new Timebox(), Events.ON_SELECTION, true);
	}

	private void testComponent(Component comp, String eventName, boolean shouldBlock) throws IllegalAccessException, InvocationTargetException {
		AuRequest auRequest = createMockAuRequest(comp, eventName);
		if (shouldBlock) {
			Assertions.assertFalse(blockService.service(auRequest, false), "'" + eventName + "' should be allowed on enabled '" + comp);
			BeanUtils.setProperty(comp, "disabled", true);
			Assertions.assertTrue(blockService.service(auRequest, false), "'" + eventName + "' should be blocked on disabled  '" + comp);
		} else {
			Assertions.assertFalse(blockService.service(auRequest, false), "'" + eventName + "' should be allowed on enabled '" + comp);
			BeanUtils.setProperty(comp, "disabled", true);
			Assertions.assertFalse(blockService.service(auRequest, false), "'" + eventName + "' should still be allowed on disabled  '" + comp);
		}
	}

	private AuRequest createMockAuRequest(final Component comp, final String eventName) {
		AuRequest auRequest = new AuRequest(EasyMock.createMock(Desktop.class), "", null) {
			@Override
			public Component getComponent() {
				return comp;
			}
			@Override
			public String getCommand() {
				return eventName;
			}
		};
		return auRequest;
	}
}