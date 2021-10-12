/* B96_ZK_4839Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 26 15:21:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;

@Category(WcagTestOnly.class)
public class B96_ZK_4839Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		mouseOver(jq(".z-icon-car"));
		waitResponse();
		assertNoJSError();
	}
}
