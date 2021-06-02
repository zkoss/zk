/* F96_ZK_4674Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 02 17:47:40 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F96_ZK_4674Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		try {
			click(jq(".z-a:contains(Atlantic)"));
			waitResponse();
			click(jq("@button"));
			waitResponse();
			Assert.assertTrue("google font should be added with HTTPS",
					getZKLog().contains("https://fonts.googleapis.com/css?family=Open+Sans"));
		} finally {
			click(jq(".z-a:contains(Default)"));
			waitResponse();
		}
	}
}
