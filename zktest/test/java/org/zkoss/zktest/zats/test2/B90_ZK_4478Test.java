/* B90_ZK_4478Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Mar 18 18:08:46 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B90_ZK_4478Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		type(jq("@chosenbox input"), "<img src=img onError=zk.log('hello')>");
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
