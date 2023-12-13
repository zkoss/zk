/* F100_ZK_5516Test.java

        Purpose:
                
        Description:
                
        History:
                Sun Dec 10 23:07:36 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.ztl.Widget;

public class F100_ZK_5516Test extends F100_ZK_5517Test {
	int[] toLastDate = new int[] {31, 28, 31, 30, 31, 30 ,31, 31, 30, 31, 30, 31};
	String[] toMonth = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	int date;
	Widget cld;
	@Test
	public void test() {
		connect();

		click(jq(".z-calendar-selected"));
		waitResponse();

		Actions actions = getActions();
		cld = jq("@calendar").toWidget();
		String month = getMonth(cld);
		date = getDate();

		actions.keyDown(Keys.PAGE_UP).perform();
		waitResponse();

		assertEquals(toMonth(month, -1), getMonth(cld));
		assertEquals(verifyDate(), getDate());

		actions.keyDown(Keys.PAGE_DOWN).perform();
		waitResponse();

		actions.keyDown(Keys.PAGE_DOWN).perform();
		waitResponse();

		assertEquals(toMonth(month, 1), getMonth(cld));
		assertEquals(verifyDate(), getDate());

		actions.keyDown(Keys.PAGE_UP).perform();
		waitResponse();

		assertEquals(month, getMonth(cld));
		assertEquals(verifyDate(), getDate());
	}

	public int toMonthNum(String month) {
		if ("Jan".equals(month)) return 0;
		else if ("Feb".equals(month)) return 1;
		else if ("Mar".equals(month)) return 2;
		else if ("Apr".equals(month)) return 3;
		else if ("May".equals(month)) return 4;
		else if ("Jun".equals(month)) return 5;
		else if ("Jul".equals(month)) return 6;
		else if ("Aug".equals(month)) return 7;
		else if ("Sep".equals(month)) return 8;
		else if ("Oct".equals(month)) return 9;
		else if ("Nov".equals(month)) return 10;
		else return 11;
	}

	public String toMonth(String month, int offset) {
		return toMonth[(toMonthNum(month) + offset + 12) % 12];
	}

	public int verifyDate() {
		String month = getMonth(cld);
		int year = getYear(cld),
			lastDate = (year % 4 == 0) && ("Feb".equals(month)) ? 29 : toLastDate[toMonthNum(month)];
		date = Math.min(date, lastDate);
		return date;
	}
}
