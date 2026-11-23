/* F96_ZK_4745Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jan 29 10:14:23 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

public class F96_ZK_4745Test extends WebDriverTestCase {
	@Test
	public void testROCParse() {
		connect();
		Element db1real = widget("@datebox:eq(0)").$n("real");
		Element db2real = widget("@datebox:eq(1)").$n("real");
		Element db3real = widget("@datebox:eq(2)").$n("real");

		Element db1btn = widget("@datebox:eq(0)").$n("btn");
		Element db2btn = widget("@datebox:eq(1)").$n("btn");
		Element db3btn = widget("@datebox:eq(2)").$n("btn");

		type(db1real, "民國110/03/13");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db1btn, "3月 民國110", "13");

		type(db2real, "民國前 003-02-18");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db2btn, "2月 民國前3", "18");

		type(db3real, "2022/01/01");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db3btn, "1月 民國111", "1");
	}

	@Test
	public void testROCFormat() {
		connect();
		Element db1real = widget("@datebox:eq(0)").$n("real");
		Element db2real = widget("@datebox:eq(1)").$n("real");
		Element db3real = widget("@datebox:eq(2)").$n("real");

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("民國前007/01/01", db1real.get("value"));
		Assert.assertEquals("民國前 007-01-01", db2real.get("value"));
		Assert.assertEquals("1905/01/01", db3real.get("value"));

		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertEquals("民國104/01/01", db1real.get("value"));
		Assert.assertEquals("民國 104-01-01", db2real.get("value"));
		Assert.assertEquals("2015/01/01", db3real.get("value"));
	}

	@Test
	public void testISOParse() {
		connect();
		Element db4real = widget("@datebox:eq(3)").$n("real");
		Element db4btn = widget("@datebox:eq(3)").$n("btn");

		type(db4real, "西元 2021-02-03");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db4btn, "2月 2021", "3");
	}

	@Test
	public void testISOFormat() {
		connect();
		Element db4real = widget("@datebox:eq(3)").$n("real");

		click(jq("@button:eq(2)"));
		waitResponse();
		Assert.assertEquals("西元 1905-01-01", db4real.get("value"));

		click(jq("@button:eq(3)"));
		waitResponse();
		Assert.assertEquals("西元 2015-01-01", db4real.get("value"));
	}

	@Test
	public void testENROCParse() {
		connect();
		Element db5real = widget("@datebox:eq(4)").$n("real");
		Element db5btn = widget("@datebox:eq(4)").$n("btn");

		type(db5real, "Before R.O.C. 002-02-07");
		Assert.assertFalse(hasError());
		checkDateboxCalendar(db5btn, "Feb Before R.O.C.2", "7");
	}

	@Test
	public void testENROCFormat() {
		connect();
		Element db5real = widget("@datebox:eq(4)").$n("real");

		click(jq("@button:eq(4)"));
		waitResponse();
		Assert.assertEquals("Before R.O.C. 007-01-01", db5real.get("value"));

		click(jq("@button:eq(5)"));
		waitResponse();
		MatcherAssert.assertThat(db5real.get("value"), Matchers.matchesRegex("(R\\.O\\.C\\.|Minguo) 104-01-01"));
	}

	private void checkDateboxCalendar(Element btn, String expectedTitle, String expectedDay) {
		click(btn);
		waitResponse();
		String calendarTitle = jq(".z-calendar-title").last().text();
		String currentDay = jq(".z-calendar-selected").last().text();
		Assert.assertEquals(expectedTitle, calendarTitle);
		Assert.assertEquals(expectedDay, currentDay);
		click(jq("@label"));
		waitResponse();
	}
}
