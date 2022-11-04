/* B00611Test.java
	Purpose:

	Description:

	History:
		Tue Apr 27 09:57:58 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B00611Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		click(jq("@button"));
		String[] outLines = outContent.toString().split("\n");
		String loadBindingStr = null;
		for (String line : outLines) {
			String trimLine = line.trim();
			if (trimLine.contains("LoadPropertyBindingImpl") && trimLine.contains("Treecell")) {
				if (loadBindingStr == null) loadBindingStr = trimLine;
				assertEquals(loadBindingStr, trimLine);
			}
		}
	}
}
