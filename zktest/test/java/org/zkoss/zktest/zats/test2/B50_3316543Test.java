/* B50_3316543Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 12:39:58 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3316543Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertTrue(jq(".z-label:contains(mdminfile)").exists());
		Assert.assertTrue(jq(".z-label:contains(wtbinfile)").exists());
	}
}
