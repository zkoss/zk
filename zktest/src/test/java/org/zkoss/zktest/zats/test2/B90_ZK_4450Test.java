/* B90_ZK_4450Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Dec 6 16:45:06 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4450Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertTrue(jq("@listbox").hasClass("z-flex-item"));
	}
}
