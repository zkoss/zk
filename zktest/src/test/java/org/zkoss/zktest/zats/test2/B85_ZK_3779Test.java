/* B85_ZK_3779Test.java

		Purpose:
                
		Description:
                
		History:
				Wed Mar 27 11:04:39 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3779Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		IntStream.range(0, 2).forEach((i) -> {
			click(jq(".z-button"));
			waitResponse();
		});
		Assert.assertFalse(isZKLogAvailable());
	}
}
