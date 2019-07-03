/* B30_1822585Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 17:00:16 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B30_1822585Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		for (int i = 0; i < 5; i++) {
			columnSizeChangeTest(act, i);
		}
	}

	private void columnSizeChangeTest(Actions act, int index) {
		JQuery target = jq(".z-column").eq(index);
		act.moveToElement(toElement(target), target.width() - 1, 1).doubleClick().perform();
		waitResponse();
		Assert.assertTrue(jq(".z-label").eq(1).text().contains("colindex:" + index));
	}
}
