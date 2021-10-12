/* F96_ZK_4810_2Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 15 17:23:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Radio;

import java.util.List;

public class F96_ZK_4810_2Test extends ZATSTestCase {
	@Test
	public void modelTest() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> radios = desktop.queryAll("#rg1 radio");
		List<ComponentAgent> buttons = desktop.queryAll("#hl1 button");
		// step1:
		checkRadiosDisabled(radios, false);
		// step2~3:
		radios.get(1).check(true);
		buttons.get(3).click();
		checkRadiosDisabled(radios, true);
		//step4:
		buttons.get(1).click();
		radios = desktop.queryAll("#rg1 radio");
		checkRadiosDisabled(radios, true);
		//step5:
		buttons.get(2).click();
		radios = desktop.queryAll("#rg1 radio");
		checkRadiosDisabled(radios, true);
		//Step6:
		buttons.get(0).click();
		radios = desktop.queryAll("#rg1 radio");
		checkRadiosDisabled(radios, true);
		//step7:
		buttons.get(3).click();
		radios = desktop.queryAll("#rg1 radio");
		checkRadiosDisabled(radios, false);
	}
	
	@Test
	public void modelTemplateTest() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> radios = desktop.queryAll("#rg2 radio");
		List<ComponentAgent> buttons = desktop.queryAll("#hl2 button");
		// step1:
		checkRadiosDisabled(radios, true);
		// step2:
		buttons.get(3).click();
		checkRadiosDisabled(radios, false);
		
		// step3~4:
		radios.get(1).check(true);
		buttons.get(3).click();
		checkRadiosDisabled(radios, true);
		//step5:
		buttons.get(1).click();
		radios = desktop.queryAll("#rg2 radio");
		checkRadiosDisabled(radios, true);
		//step6:
		buttons.get(2).click();
		radios = desktop.queryAll("#rg2 radio");
		checkRadiosDisabled(radios, true);
		//Step7:
		buttons.get(0).click();
		radios = desktop.queryAll("#rg2 radio");
		checkRadiosDisabled(radios, true);
		//step8:
		buttons.get(3).click();
		radios = desktop.queryAll("#rg2 radio");
		checkRadiosDisabled(radios, false);
	}
	
	@Test
	public void modelRendererTest() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> radios = desktop.queryAll("#rg3 radio");
		List<ComponentAgent> buttons = desktop.queryAll("#hl3 button");
		// step1:
		checkRadiosDisabled(radios, false);
		// step2~3:
		radios.get(1).check(true);
		buttons.get(3).click();
		checkRadiosDisabled(radios, true);
		//step4:
		buttons.get(1).click();
		radios = desktop.queryAll("#rg3 radio");
		checkRadiosDisabled(radios, true);
		//step5:
		buttons.get(2).click();
		radios = desktop.queryAll("#rg3 radio");
		checkRadiosDisabled(radios, true);
		//Step6:
		buttons.get(0).click();
		radios = desktop.queryAll("#rg3 radio");
		checkRadiosDisabled(radios, true);
		//step7:
		buttons.get(3).click();
		radios = desktop.queryAll("#rg3 radio");
		checkRadiosDisabled(radios, false);
	}
	
	private void checkRadiosDisabled(List<ComponentAgent> radios, boolean expected) {
		for (int i = 0, size = radios.size(); i < size; i++) {
			Assert.assertEquals(expected, radios.get(i).as(Radio.class).isDisabled());
		}
	}
}
