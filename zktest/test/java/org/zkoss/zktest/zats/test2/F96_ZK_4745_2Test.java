/* F96_ZK_4745_2Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Feb 18 17:31:24 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

public class F96_ZK_4745_2Test  extends WebDriverTestCase {
	@Test
	public void testJapaneseParse() {
		connect();
		Element db1real = widget("@datebox:eq(0)").$n("real");
		Element db2real = widget("@datebox:eq(1)").$n("real");
		Element db3real = widget("@datebox:eq(2)").$n("real");

		Element db1btn = widget("@datebox:eq(0)").$n("btn");
		Element db2btn = widget("@datebox:eq(1)").$n("btn");
		Element db3btn = widget("@datebox:eq(2)").$n("btn");

		type(db1real, "大正14/07/31");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db1btn, "7(月)? 大正14", "31");

		type(db2real, "大正 14/07/31");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db2btn, "7(月)? 大正14", "31");

		type(db3real, "2022/01/01");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db3btn, "1(月)? 令和4", "1");
	}

	@Test
	public void testJapaneseFormat() {
		connect();
		Element db1real = widget("@datebox:eq(0)").$n("real");
		Element db2real = widget("@datebox:eq(1)").$n("real");
		Element db3real = widget("@datebox:eq(2)").$n("real");

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("平成31/04/30", db1real.get("value"));
		Assert.assertEquals("平成 31-04-30", db2real.get("value"));
		Assert.assertEquals("2019/04/30", db3real.get("value"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("令和01/05/01", db1real.get("value"));
		Assert.assertEquals("令和 01-05-01", db2real.get("value"));
		Assert.assertEquals("2019/05/01", db3real.get("value"));
	}

	@Test
	public void testISOParse() {
		connect();
		Element db4real = widget("@datebox:eq(3)").$n("real");
		Element db4btn = widget("@datebox:eq(3)").$n("btn");

		type(db4real, "西暦 2021-02-03");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db4btn, "2(月)? 2021", "3");
	}

	@Test
	public void testISOFormat() {
		connect();
		Element db4real = widget("@datebox:eq(3)").$n("real");

		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertEquals("西暦 2019-04-30", db4real.get("value"));

		click(jq("@button:eq(3)"));
		waitResponse();
		Assert.assertEquals("西暦 2019-05-01", db4real.get("value"));
	}

	@Test
	public void testENJapaneseParse() {
		connect();
		Element db5real = widget("@datebox:eq(4)").$n("real");
		Element db5btn = widget("@datebox:eq(4)").$n("btn");

		type(db5real, "Reiwa 03-03-27");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db5btn, "Mar Reiwa3", "27");
	}

	@Test
	public void testENJapaneseFormat() {
		connect();
		Element db5real = widget("@datebox:eq(4)").$n("real");

		click(jq("@button:eq(4)"));
		waitResponse();
		Assert.assertEquals("Heisei 31-04-30", db5real.get("value"));

		click(jq("@button:eq(5)"));
		waitResponse();
		Assert.assertEquals("Reiwa 01-05-01", db5real.get("value"));
	}

	private void checkDateboxCalendar(Element btn, String expectedTitle, String expectedDay) {
		click(btn);
		waitResponse();
		String calendarTitle = jq(".z-calendar-title").last().text();
		String currentDay = jq(".z-calendar-selected").last().text();
		MatcherAssert.assertThat(calendarTitle, Matchers.matchesRegex(expectedTitle));
		Assert.assertEquals(expectedDay, currentDay);
		click(jq("@label"));
		waitResponse();
	}
}

