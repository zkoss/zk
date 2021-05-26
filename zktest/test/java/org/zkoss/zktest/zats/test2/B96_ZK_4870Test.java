/* B96_ZK_4870Test.java

		Purpose:
		
		Description:
		
		History:
				Wed May 26 18:33:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4870Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// only test default theme here
		Assert.assertEquals("0px", jq(".z-caption-content").css("padding"));
		Assert.assertEquals("normal", jq(".z-caption-content").css("line-height"));
		Assert.assertEquals("normal", jq(".z-caption-content .z-label").css("line-height"));
	}
}
