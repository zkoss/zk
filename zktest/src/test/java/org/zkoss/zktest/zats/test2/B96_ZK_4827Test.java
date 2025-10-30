/* B96_ZK_4827Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 19 10:30:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */

public class B96_ZK_4827Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect("/test2/B96-ZK-4827.jsp");
		sleep(3000);
		assertNoJSError();
	}
}
