/* Z35_funcmapTest.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 16:13:49 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class Z35_funcmapTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals("'Correct'", jq(".z-label").eq(1).text().trim());
	}
}
