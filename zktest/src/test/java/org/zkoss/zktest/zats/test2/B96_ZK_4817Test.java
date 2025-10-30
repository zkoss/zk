/* B96_ZK_4817Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 31 14:53:15 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.TouchWebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B96_ZK_4817Test extends TouchWebDriverTestCase {

	@Test
	public void test() {
		connect();

		final Widget listgroup = widget("@listgroup");
		tap(toElement(listgroup.$n("img")));
		waitResponse();
		Assertions.assertFalse(jq(listgroup).hasClass("z-listgroup-selected"));
		Assertions.assertTrue(jq(listgroup).hasClass("z-listgroup-open"));

		final Widget group = widget("@group");
		tap(toElement(group.$n("img")));
		waitResponse();
		Assertions.assertTrue(jq(group).hasClass("z-group-open"));

		final Widget detail = widget("@detail");
		tap(toElement(detail.$n("icon")));
		waitResponse();
		Assertions.assertTrue(jq(detail.$n("chdextr")).hasClass("z-detail-open"));
	}
}