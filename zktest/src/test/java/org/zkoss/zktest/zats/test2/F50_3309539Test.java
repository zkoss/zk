/* F50_3309539Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 03 18:37:10 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3309539Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		checkAllLocaleZhTw();
	}

	private void checkAllLocaleZhTw() {
		JQuery row = jq("@row:eq(0)");
		String dateboxMediumTw = jq(row.find("@datebox:eq(0)").toWidget().$n("real")).val();
		String dateboxLongTw = jq(row.find("@datebox:eq(1)").toWidget().$n("real")).val();
		String timeboxMediumTw = jq(row.find("@timebox:eq(0)").toWidget().$n("real")).val();
		String timeboxLongTw = jq(row.find("@timebox:eq(1)").toWidget().$n("real")).val();

		jq("@row:not(:first)").iterator().forEachRemaining(r -> {
			String dateboxMedium = jq(r.find("@datebox:eq(0)").toWidget().$n("real")).val();
			Assertions.assertEquals(dateboxMediumTw, dateboxMedium);
			String dateboxLong = jq(r.find("@datebox:eq(1)").toWidget().$n("real")).val();
			Assertions.assertEquals(dateboxLongTw, dateboxLong);
			String timeboxMedium = jq(r.find("@timebox:eq(0)").toWidget().$n("real")).val();
			Assertions.assertEquals(timeboxMediumTw, timeboxMedium);
			String timeboxLong = jq(r.find("@timebox:eq(1)").toWidget().$n("real")).val();
			Assertions.assertEquals(timeboxLongTw, timeboxLong);
		});
	}
}
