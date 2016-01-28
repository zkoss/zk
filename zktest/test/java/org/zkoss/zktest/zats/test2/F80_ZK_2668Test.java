/* F80_ZK_3041Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 25 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author jameschu
 */
public class F80_ZK_2668Test extends WebDriverTestCase {

	@Test
	public void test() throws IOException {
		connect();
		JQuery $timebox = jq("@timebox input");
		JQuery $datebox = jq("@datebox input");
		JQuery $textbox = jq("@textbox");
		String oldDate = $datebox.val().substring(0, $datebox.val().indexOf(" "));
		click($textbox);
		waitResponse();
		click(jq(".ui-timepicker-list li:first"));
		waitResponse();
		String newTime = $textbox.val();
		int newTimeSpaceIndex = newTime.indexOf(" ");
		assertEquals((newTime.substring(newTimeSpaceIndex, newTime.length()) + " " + newTime.substring(0, newTimeSpaceIndex) + ":00").trim(), $timebox.val());
		assertEquals((oldDate + " " + $timebox.val()).trim(), $datebox.val());
	}
}
