/* F86_ZK_3958Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jul 18 11:49:16 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.python.parser.ast.Str;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_3958Test extends WebDriverTestCase {
	
	private JQuery isCheckedButton;
	
	@Test
	public void test() {
		connect();
		isCheckedButton = findButtonContains("isChecked");
		JQuery moldLocation = jq(".z-checkbox-mold");
		String switchMold = ".z-checkbox-switch";
		String toggleMold = ".z-checkbox-toggle";
		String off = "-off";
		String on = "-on";
		
		verifyClassChange(findButtonContains("Switch"), jq(switchMold + off));
		Assert.assertFalse(jq(".z-checkbox > input").isVisible());
		
		verifyChecked("false");
		
		verifyClassChange(moldLocation, jq(switchMold + on));
		
		verifyChecked("true");
		
		verifyClassChange(findButtonContains("Toggle"), jq(toggleMold + on));
		
		verifyClassChange(moldLocation, jq(toggleMold + off));
		
		verifyChecked("false");
		
		verifyClassChange(findButtonContains("setDisabled"), jq(toggleMold + "-disabled"));
		
		verifyClassChange(moldLocation, jq(toggleMold + off));
		
		verifyChecked("false");
	}
	
	private void verifyClassChange(JQuery location, JQuery target) {
		click(location);
		waitResponse();
		Assert.assertTrue(target.exists());
	}
	
	private void verifyChecked(String checked) {
		click(isCheckedButton);
		waitResponse();
		Assert.assertEquals(checked, getZKLog());
		closeZKLog();
		waitResponse();
	}
	
	private JQuery findButtonContains(String content) {
		return jq(".z-button:contains(" + content + ")");
	}
}
