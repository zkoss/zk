/* Z30_tree_0003Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 11:34:52 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Z30_tree_0003Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery buttons = jq(".z-button");
		verify(buttons.eq(0), () -> Assertions.assertEquals(8, jq(".z-treerow-checkable").length()));
		verify(buttons.eq(1), () -> jq(".z-treerow:eq(1)").hasClass("z-treerow-selected"));
		verify(buttons.eq(2), () -> Assertions.assertEquals(41, jq(".z-treerow").length()));
	}

	private void verify(JQuery button, Command command) {
		click(button);
		waitResponse();
		command.execute();
	}

	interface Command {
		void execute();
	}
}
