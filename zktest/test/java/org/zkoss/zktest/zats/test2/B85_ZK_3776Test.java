/* B85_ZK_3776Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Mar 14 12:34 PM:04 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3776Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int initLength = jq(".z-listcell").length();
		getEval("jq('.z-frozen-inner')[0].scrollLeft = 100");
		waitResponse();
		click(jq(".z-paging-next").get(0));
		waitResponse();
		Assert.assertEquals(initLength, jq(".z-listcell").length());
	}
}
