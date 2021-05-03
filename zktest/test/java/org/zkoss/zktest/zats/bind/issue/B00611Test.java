/* B00611Test.java
	Purpose:

	Description:

	History:
		Tue Apr 27 09:57:58 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B00611Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		desktop.query("button").click();
		String[] outLines = outContent.toString().split("\n");
		String loadBindingStr = null;
		for (String line : outLines) {
			String trimLine = line.trim();
			if (trimLine.contains("LoadPropertyBindingImpl") && trimLine.contains("Treecell")) {
				if (loadBindingStr == null)
					loadBindingStr = trimLine;
				assertEquals(loadBindingStr, trimLine);
			}
		}
	}
}
