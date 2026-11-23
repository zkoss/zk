/* B90_ZK_4415Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 18 16:52:38 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4415Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		sleep(1000); // Wait for pdf.js
		Assert.assertNotEquals("", jq(widget("@pdfviewer").$n("toolbar-page-total")).text());
	}
}
