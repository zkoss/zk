/* F96_ZK_4810Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 15 16:52:21 CST 2021, Created by leon

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

public class F96_ZK_4810Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> radios = desktop.queryAll("radio");
		List<ComponentAgent> buttons = desktop.queryAll("button");
		
		// step1:
		checkRadiosDisabled(radios, 0, 3, false);
		
		// step2~3:
		radios.get(1).check(true);
		buttons.get(9).click();
		checkRadiosDisabled(radios, 0, 3, true);
		
		// step 4~7:
		buttons.get(0).click();
		buttons.get(3).click();
		buttons.get(6).click();
		checkRadiosDisabled(radios, 0, 6, true);
		
		// step 8~11:
		buttons.get(2).click();
		buttons.get(5).click();
		buttons.get(8).click();
		checkRadiosDisabled(radios, 4, 6, false);
		
		// step 12~15:
		buttons.get(1).click();
		buttons.get(4).click();
		buttons.get(7).click();
		checkRadiosDisabled(radios, 0, 3, true);
		
		// step 16:
		buttons.get(9).click();
		checkRadiosDisabled(radios, 0, 6, false);
	}
	
	private void checkRadiosDisabled(List<ComponentAgent> radios, int startIndex, int endIndex, boolean expected) {
		for (int i = startIndex; i <= endIndex; i++) {
			Assert.assertEquals(expected, radios.get(i).as(Radio.class).isDisabled());
		}
	}
}
