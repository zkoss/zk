/* B35_2073413Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 11:23:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B35_2073413Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//Group1 Open
		click(jq("$today").find("span"));
		waitResponse();

		//Check if rows are visible
		for (int i = 1; i <= 15; i++) {
			assertTrue(jq("$row" + i).isVisible());

			//Open any detail
			click(jq("$row" + i).find(" @detail").toWidget().$n("icon"));
			waitResponse();

			//Verify detail is visible
			assertTrue(jq("$gp" + i).isVisible());
		}

		//Group1 Close
		click(jq("$today").find("span"));
		waitResponse();

		//Group2 Close
		click(jq("$yesterday").find("span"));
		waitResponse();

		//Check if rows are invisible
		for (int i = 1; i <= 15; i++) {
			assertFalse(jq("$row" + i).isVisible());

			//Verify detail is invisible
			assertFalse(jq("$gp" + i).isVisible());
		}

		//Group1 Open
		click(jq("$today").find("span"));
		waitResponse();

		//Group2 Open
		click(jq("$yesterday").find("span"));
		waitResponse();

		//Check if rows & details are visible
		for (int i = 1; i <= 15; i++) {
			assertTrue(jq("$row" + i).isVisible());

			//Verify detail is visible
			assertTrue(jq("$gp" + i).isVisible());
		}


		//Close detail to check unopen never showup
		click(jq("$row14").find("@detail").toWidget().$n("icon"));
		waitResponse();

		//Group2 close
		click(jq("$yesterday").find("span"));
		waitResponse();

		//Group2 Open
		click(jq("$yesterday").find("span"));
		waitResponse();

		//Check if detail still unopen
		assertFalse(jq("$gp14").isVisible());
	}
}
