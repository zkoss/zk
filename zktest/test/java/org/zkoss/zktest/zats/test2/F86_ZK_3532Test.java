/* F86_ZK_3532Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Oct 23 15:21:38 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_3532Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery div1 = jq("$d1");
		
		click(jq("$btn1"));
		waitResponse();
		Assert.assertTrue(div1.hasClass("classA"));
		
		click(jq("$btn4"));
		waitResponse();
		Assert.assertTrue(div1.hasClass("classA"));
		
		click(jq("$btn2"));
		waitResponse();
		Assert.assertTrue(div1.hasClass("classA") && div1.hasClass("classB"));
		
		click(jq("$btn3"));
		waitResponse();
		Assert.assertTrue(!div1.hasClass("classA") && div1.hasClass("classB"));
		
		click(jq("$btn4"));
		waitResponse();
		Assert.assertFalse(div1.hasClass("classB"));
		
		click(jq("$btn6"));
		waitResponse();
		Assert.assertTrue(div1.hasClass("classA") && div1.hasClass("classB") && div1.hasClass("classC"));
		
		click(jq("$btn5"));
		waitResponse();
		Assert.assertTrue(!div1.hasClass("classA") && !div1.hasClass("classB") && !div1.hasClass("classC"));
		
		click(jq("$btn6"));
		waitResponse();
		click(jq("$btn7"));
		waitResponse();
		Assert.assertTrue(!div1.hasClass("classA") && !div1.hasClass("classB") && div1.hasClass("classC"));
		
		click(jq("$btn6"));
		waitResponse();
		click(jq("$btn8"));
		waitResponse();
		Assert.assertTrue(div1.hasClass("AAAAA") && div1.hasClass("A"));
		
		click(jq("$btn9"));
		waitResponse();
		Assert.assertTrue(div1.hasClass("AAAAA") && !div1.hasClass("A"));
	}
}
