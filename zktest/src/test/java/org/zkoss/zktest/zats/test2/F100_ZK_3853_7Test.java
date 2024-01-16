/* F100_ZK_3853_7Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jan 09 14:29:13 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

public class F100_ZK_3853_7Test extends F100_ZK_3853_1Test {

	@Override
	@Test
	public void test() {
		connect();

		// open all and select all
		clickOpen(0, 1, 2, 3);
		clickHdcm();

		// insert a un-checked new node (should cause ancestors partial)
		click(jq("@textbox:eq(0)"));
		waitResponse();
		keyboardInput("2 0 2 1");
		click(jq("@button:eq(0)"));
		waitResponse();

		assertHdcm(2);
		assertCm(2, 4, 5, 8);
		assertCm(1, 0, 1, 2, 3, 6, 7, 9, 11, 12, 13, 14);
		assertCm(0, 10);

		// delete the un-checked new node (every checkbox should change back to selected state)
		click(jq("@textbox:eq(1)"));
		waitResponse();
		keyboardInput("2 0 2 1");
		click(jq("@button:eq(1)"));
		waitResponse();

		assertHdcm(1);
		for (int i = 0; i < 13; ++i)
			assertCm(1, i);
	}

	public void keyboardInput(String keys) {
		Actions action = getActions();
		String[] keysArr = keys.split(" ");
		for (int i = 0, n = keysArr.length; i < n; ++i) {
			action.sendKeys(keysArr[i]);
			if (i + 1 < n)
				action.sendKeys(Keys.SPACE);
		}
		action.perform();
	}
}
