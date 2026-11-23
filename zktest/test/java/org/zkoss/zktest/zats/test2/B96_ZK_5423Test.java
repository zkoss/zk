/* B96_ZK_5423Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 14 15:50:12 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */

public class B96_ZK_5423Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-cell.z-calendar-weekday").eq(10)); //random date
		waitResponse();
		Assert.assertFalse(jq(".z-errorbox").exists());
	}
}
