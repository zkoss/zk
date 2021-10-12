/* B96_ZK_4848Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 4 16:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4848Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$col1"));
		waitResponse();
		assertFalse(hasError());
		click(jq("$col2"));
		waitResponse();
		assertTrue(jq(".z-messagebox .z-label").html().contains("Unable to sort"));
		click(jq(".z-messagebox-buttons button"));
		waitResponse();
		click(jq("$lh2"));
		waitResponse();
		assertFalse(hasError());
		click(jq("$lh1"));
		waitResponse();
		assertTrue(jq(".z-messagebox .z-label").html().contains("Unable to sort"));
	}
}
