/* F85_ZK_3846.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 07 15:45:28 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F85_ZK_3846Test extends WebDriverTestCase {
	
	private JQuery datebox;
	
	@Test
	public void test() {
		connect();
		
		datebox = jq(".z-datebox-input");
		
		assertBySeparator("~");
		
		click(jq("@button"));
		waitResponse();
		
		assertBySeparator("...");
	}
	
	private void assertBySeparator(String separator) {
		type(datebox.eq(0), "2019/01/01");
		waitResponse();
		Assertions.assertEquals("2018/01/01 " + separator + " 2018/12/31", getDateOfErrorMsg());
		
		type(datebox.eq(1), "01.01.2019");
		waitResponse();
		Assertions.assertEquals("01.01.2018 " + separator + " 31.12.2018", getDateOfErrorMsg());
		
		type(datebox.eq(2), "20190101");
		waitResponse();
		Assertions.assertEquals("20180101 " + separator + " 20181231", getDateOfErrorMsg());
	}
	
	private String getDateOfErrorMsg() {
		JQuery errorbox = jq(".z-errorbox-content");
		String errorMsg = errorbox.text();
		errorbox.remove();
		return errorMsg.substring(errorMsg.indexOf(':') + 2);
	}
}