/* B96_ZK_4859Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 22 16:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4859Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		type(jq("@textbox"), "$z!t#d:");
		waitResponse(); //no need to assert. If bug exists, there will be an UnhandledAlertException
	}
}
