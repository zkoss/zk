/* F85_ZK_3624Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 31 09:33:50 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class F85_ZK_3624Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		increase();
		Assert.assertEquals("1", jq(".z-spinner-input").val());
		clearSpinnerValue();
		
		click(jq("@button:eq(0)"));
		waitResponse();
		
		increase();
		Assert.assertEquals("1", jq(".z-spinner-input").val());
		clearSpinnerValue();
		
		click(jq("@button:eq(1)"));
		waitResponse();
		
		increase();
		Assert.assertEquals("2", jq(".z-spinner-input").val());
	}
	
	
	private void increase() {
		click(jq(".z-spinner-up"));
		waitResponse();
	}
	
	private void clearSpinnerValue() {
		type(jq(".z-spinner-input"), "");
		waitResponse();
	}
	
}
