/* B80_ZK_3567Test.java

		Purpose:
                
		Description:
                
		History:
				Wed Mar 27 10:42:04 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3567Test extends WebDriverTestCase {

	@Test
	public void test() {
		Actions actions = new Actions(connect());
		JQuery listitems = jq(".z-listitem");

		verify(() -> actions.dragAndDrop(toElement(listitems.eq(0)), toElement(listitems.eq(1))).build().perform());

		verify(() -> click(listitems.eq(1)));

		verify(() -> {
			actions.moveToElement(toElement(jq(".z-button"))).build().perform();
			waitResponse(3000);
		});
	}

	private void verify(Command command) {
		command.execute();
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}

	interface Command {
		void execute();
	}
}
