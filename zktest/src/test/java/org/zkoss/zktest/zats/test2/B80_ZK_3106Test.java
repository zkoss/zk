/* B80_ZK_3106Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Apr 28, 2016  5:28:47 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3106Test extends WebDriverTestCase {
	@Test
	public void B80_ZK_3106Test(){
		connect();
		JQuery label = jq(".target");
		assertTrue(label.exists(), "should find label \"Test\"");
	}
}
