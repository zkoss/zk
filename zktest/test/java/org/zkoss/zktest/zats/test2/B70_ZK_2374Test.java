/* B70_ZK_2374Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 17:49:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.openqa.selenium.Point;

import org.zkoss.zktest.zats.TouchWebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2374Test extends TouchWebDriverTestCase {

	@Test
	public void test() {
		connect();

		swipe(new Point(50, 300), new Point(50, 200), 700);
		waitResponse();
		assertNoJSError();
	}
}
