/* F80_ZK_2641Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 10:59:16 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F80_ZK_2641Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery tb1 = jq("@textbox").eq(0);
		JQuery eBtn = jq("$emptyBtn");
		click(tb1);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no empty", jq(".z-errorbox-content").text());

		click(tb1);
		waitResponse();
		sendKeys(tb1, "a");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("email only", jq(".z-errorbox-content").text());

		JQuery ib = jq("@intbox");
		waitResponse();
		sendKeys(ib, "-1");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no neg", jq(".z-errorbox-content").eq(1).text());

		click(ib);
		sendKeys(ib, Keys.BACK_SPACE, Keys.BACK_SPACE, "1");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no pos", jq(".z-errorbox-content").eq(1).text());

		JQuery ib2 = jq("@intbox").eq(1);
		click(ib2);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no empty", jq(".z-errorbox-content").eq(2).text());

		sendKeys(ib2, "0");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no zero", jq(".z-errorbox-content").eq(2).text());

		JQuery db = jq(".z-datebox-input");
		//get tomorrow's date
		click(db);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no empty", jq(".z-errorbox-content").eq(3).text());

		String tomorrowStr = LocalDate.now()
				.plusDays(1)
				.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		sendKeys(db, tomorrowStr);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no future", jq(".z-errorbox-content").eq(3).text());

		JQuery db2 = jq(".z-datebox-input").eq(1);
		sendKeys(db2, "20150415");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no before", jq(".z-errorbox-content").eq(4).text());

		click(db2);
		sendKeys(db2, Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, "21");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no after", jq(".z-errorbox-content").eq(4).text());

		JQuery db3 = jq(".z-datebox-input").eq(2);
		click(db3);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no empty", jq(".z-errorbox-content").eq(5).text());

		sendKeys(db3, "20150421");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("between", jq(".z-errorbox-content").eq(5).text());

		JQuery cb = jq(".z-combobox-input");
		click(cb);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("no empty", jq(".z-errorbox-content").eq(6).text());

		sendKeys(cb, "a");
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("strict", jq(".z-errorbox-content").eq(6).text());

		JQuery ib3 = jq("@intbox").eq(2);
		click(jq("@button").eq(1));
		waitResponse();
		sendKeys(ib3, "-1");
		waitResponse();
		click(ib3);
		waitResponse();
		click(eBtn);
		waitResponse();
		Assertions.assertEquals("Only positive number or zero is allowed", jq(".z-errorbox-content").eq(7).text());
	}
}
