/* B85_ZK_3861Test.java

	Purpose:

	Description:

	History:
	        Mon Mar 12 14:20:49 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3861Test extends WebDriverTestCase {

	private JQuery buttons = jq("@button");

	@Test
	public void test() {
		connect();

		for (int i = 0; i < 3; i++)
			runTest(i);
	}

	private void runTest(int pageInputIndex) {
		changePageTest(pageInputIndex);

		click(buttons.eq(pageInputIndex * 3 + 2));
		waitResponse(true);

		changePageTest(pageInputIndex);
	}

	private void changePageTest(int pageInputIndex) {
		JQuery pgiInput = jq(".z-paging-input").eq(pageInputIndex);
		int previousBtnIndex = pageInputIndex * 3;

		click(buttons.eq(previousBtnIndex + 1));
		waitResponse(true);
		Assert.assertEquals(2, Integer.parseInt(pgiInput.val()));

		click(buttons.eq(previousBtnIndex));
		waitResponse(true);
		Assert.assertEquals(1, Integer.parseInt(pgiInput.val()));
	}
}
