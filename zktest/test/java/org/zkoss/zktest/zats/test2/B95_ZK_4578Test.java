/* B95_ZK_4578Test.java

		Purpose:
		
		Description:
		
		History:
			Tue May 12 11:32:33 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4578Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertFalse(isZKLogAvailable());
	}
}
