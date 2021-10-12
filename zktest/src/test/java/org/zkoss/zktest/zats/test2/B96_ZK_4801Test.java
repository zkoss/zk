/* B96_ZK_4801Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 25 15:53:21 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_4801Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		Assert.assertFalse("google font shouldn't be added if atlantic theme is not active",
			getZKLog().contains("fonts.googleapis.com/css?family=Open+Sans"));
		closeZKLog();


		try {
			click(jq(".z-a:contains(Atlantic)"));
			waitResponse();
			click(button);
			waitResponse();
			Assert.assertTrue("google font should be added if atlantic theme is active",
					getZKLog().contains("fonts.googleapis.com/css?family=Open+Sans"));
		} finally {
			click(jq(".z-a:contains(Default)"));
			waitResponse();
		}
	}
}

