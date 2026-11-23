/* B96_ZK_4824Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 26 12:01:39 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;

@Category(WcagTestOnly.class)
public class B96_ZK_4824Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("The aria-owns of radio group should only contains external radio's id.", "pass", getZKLog());
	}
}