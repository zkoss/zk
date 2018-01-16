/* B85_ZK_3830Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 15 12:42:38 CST 2017, Created by klyvechen

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import static org.junit.Assert.assertFalse;

/**
 * @author klyvechen
 */
public class B85_ZK_3830Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq(".z-combobox").find("a").get(0));
		waitResponse();
		click(jq(".z-combobox-popup").find("li").get(1));
		waitResponse();
		click(jq(".z-combobox").find("a").get(0));
		waitResponse();
		click(jq(".z-combobox-popup").find("li").get(2));
		waitResponse();
		assertFalse(jq("button").get(0).exists());
	}
}

