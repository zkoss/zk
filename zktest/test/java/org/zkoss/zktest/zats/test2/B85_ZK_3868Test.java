/* B85_ZK_3868Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 12 15:32:15 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3868Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		typeAndCheckVal(0, "Feb19");
		typeAndCheckVal(1, "Feb2019");
		typeAndCheckVal(2, "February19");
		typeAndCheckVal(3, "February2019");
		typeAndCheckVal(4, "Feb-19");
		typeAndCheckVal(5, "022019");
		typeAndCheckVal(6, "19Feb");
		typeAndCheckVal(7, "2019Feb");
	}

	private void typeAndCheckVal(int inputIndex, String valueChanged) {
		type(jq("input").eq(inputIndex), valueChanged);
		waitResponse();
		Assert.assertEquals(valueChanged, jq("input").eq(inputIndex).val());
	}
}
