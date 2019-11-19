/* B90_ZK_4401_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 15:39:38 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4401_1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
	}
}
