/* B85_ZK_3618Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Mar 26 12:01:54 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3618Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery img = jq(".z-image");

		click(img);
		waitResponse(true);

		Assert.assertFalse(img.exists());
	}
}
