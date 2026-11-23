/* B96_ZK_5019Test.java

	Purpose:
		
	Description:
		
	History:
		11:59 AM 2021/11/9, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5019Test  extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-bandbox-button:eq(0)"));
		waitResponse();
		click(jq(".z-bandbox-button:eq(1)"));
		waitResponse();
		Assert.assertEquals(2, jq(".z-bandbox-popup.z-bandbox-open").length());

		click(jq(".z-listcell-content:contains(AAA)"));
		waitResponse(true);

		Assert.assertEquals(1, jq(".z-bandbox-popup.z-bandbox-open").length());
		Assert.assertEquals("AAA", jq(".z-bandbox-input:eq(1)").val());
	}
}
