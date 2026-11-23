/* B80_ZK_3026Test.java
	Purpose:

	Description:

	History:
		Mon Jan 25 15:36:18 CST 2016, Created by jameschu
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author jameschu
 */
public class B80_ZK_3026Test extends ZATSTestCase {

    @Test
    public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> btns = desktop.queryAll("button");
		ComponentAgent btn1 = btns.get(0);
		ComponentAgent btn2 = btns.get(1);
		checkResult("ABCDE", desktop.queryAll("div label"));
		btn1.click();
		checkResult("AABCDE", desktop.queryAll("div label"));
		btn2.click();
		checkResult("ABCDE", desktop.queryAll("div label"));
		btn1.click();
		checkResult("AABCDE", desktop.queryAll("div label"));
		btn2.click();
		checkResult("ABCDE", desktop.queryAll("div label"));
		btn1.click();
		checkResult("AABCDE", desktop.queryAll("div label"));
		btn2.click();
		checkResult("ABCDE", desktop.queryAll("div label"));
    }

	public void checkResult(String expectedStr, List<ComponentAgent> labels) {
		String result = "";
		for (ComponentAgent l: labels) {
			result += l.as(Label.class).getValue();
		}
		assertEquals(expectedStr, result);
	}
}
