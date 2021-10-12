/* B95_ZK_4563Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Aug 19, 2020  02:22:18 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4563Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
	}
}
