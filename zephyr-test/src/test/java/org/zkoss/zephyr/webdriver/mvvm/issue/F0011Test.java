package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F0011Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date now = new Date();
		String today = sdf.format(now).toString();
		String yesterday = sdf.format(new Date(now.getTime() - 1000 * 60 * 60 * 24));
		String tomorrow = sdf.format(new Date(now.getTime() + 1000 * 60 * 60 * 24));
		JQuery db1 = jq("$db1");
		JQuery lb11 = jq("$lb11");
		JQuery lb12 = jq("$lb12");
		assertEquals(today, db1.find("input").val());
		assertEquals(today, lb11.text());
		assertEquals("", lb12.text());

		type(db1.find("input"), tomorrow);
		waitResponse();
		assertEquals(tomorrow, db1.find("input").val());
		assertEquals(today, lb11.text());
		assertEquals("date bday1 must small than today", lb12.text());

		type(db1.find("input"), yesterday);
		waitResponse();
		assertEquals(yesterday, db1.find("input").val());
		assertEquals(yesterday, lb11.text());
		assertEquals("", lb12.text());

		JQuery db2 = jq("$db2");
		JQuery lb21 = jq("$lb21");
		JQuery lb22 = jq("$lb22");
		assertEquals("", db2.find("input").val());
		assertEquals("", lb21.text());
		assertEquals("", lb22.text());

		type(db2.find("input"), yesterday);
		waitResponse();
		assertEquals(yesterday, db2.find("input").val());
		assertEquals("", lb21.text());
		assertEquals("date bday2 must large than today", lb22.text());

		type(db2.find("input"), tomorrow);
		waitResponse();
		assertEquals(tomorrow, db2.find("input").val());
		assertEquals(tomorrow, lb21.text());
		assertEquals("", lb22.text());

		JQuery tb31 = jq("$tb31");
		JQuery tb32 = jq("$tb32");
		JQuery lb31 = jq("$lb31");
		JQuery lb32 = jq("$lb32");

		assertEquals("", tb31.val());
		assertEquals("", tb32.val());
		assertEquals("", lb31.text());
		assertEquals("", lb32.text());

		JQuery btn1 = jq("$btn1");
		click(btn1);
		waitResponse();
		assertEquals("value1 is empty", lb32.text());

		type(tb31, "abc");
		waitResponse();
		assertEquals("", lb31.text());
		assertEquals("value1 is empty", lb32.text());

		click(btn1);
		waitResponse();
		assertEquals("value2 must euqlas to value 1", lb32.text());

		type(tb32, "abc");
		waitResponse();
		assertEquals("", lb31.text());
		assertEquals("value2 must euqlas to value 1", lb32.text());

		click(btn1);
		waitResponse();
		assertEquals("abc", lb31.text());
		assertEquals("do Command1", lb32.text());

		JQuery tb41 = jq("$tb41");
		JQuery tb42 = jq("$tb42");
		JQuery lb41 = jq("$lb41");
		JQuery lb42 = jq("$lb42");

		assertEquals("", tb41.val());
		assertEquals("", tb42.val());
		assertEquals("", lb41.text());
		assertEquals("", lb42.text());

		JQuery btn2 = jq("$btn2");
		click(btn2);
		waitResponse();
		assertEquals("value3 is empty", lb42.text());

		type(tb41, "abc");
		waitResponse();
		assertEquals("", lb41.text());
		assertEquals("", lb42.text());

		type(tb41, "");
		waitResponse();
		assertEquals("", lb41.text());
		assertEquals("value3 is empty", lb42.text());

		type(tb41, "abc");
		waitResponse();
		click(btn2);
		waitResponse();
		assertEquals("", lb41.text());
		assertEquals("value4 is empty", lb42.text());

		type(tb42, "def");
		waitResponse();
		assertEquals("", lb41.text());
		assertEquals("", lb42.text());

		click(btn2);
		waitResponse();
		assertEquals("", lb41.text());
		assertEquals("value4 must euqlas to value 3", lb42.text());

		type(tb42, "abc");
		waitResponse();
		assertEquals("", lb41.text());
		assertEquals("", lb42.text());

		click(btn2);
		waitResponse();
		assertEquals("abc", lb41.text());
		assertEquals("do Command2", lb42.text());

		JQuery tb51 = jq("$tb51");
		JQuery tb52 = jq("$tb52");
		JQuery lb51 = jq("$lb51");
		JQuery lb52 = jq("$lb52");

		assertEquals("abc", tb51.val());
		assertEquals("abc", tb52.val());
		assertEquals("abc", lb51.text());
		assertEquals("", lb52.text());

		type(tb51, "");
		waitResponse();
		type(tb52, "");
		waitResponse();
		JQuery btn3 = jq("$btn3");
		click(btn3);
		waitResponse();
		assertEquals("do Command3", lb52.text());

		type(tb51, "abc");
		waitResponse();
		assertEquals("", lb51.text());
		assertEquals("do Command3", lb52.text());

		click(btn3);
		waitResponse();
		assertEquals("value2 must euqlas to value 1", lb52.text());

		type(tb52, "def");
		waitResponse();
		click(btn3);
		waitResponse();
		assertEquals("", lb51.text());
		assertEquals("value2 must euqlas to value 1", lb52.text());

		type(tb52, "abc");
		waitResponse();
		click(btn3);
		waitResponse();
		assertEquals("abc", lb51.text());
		assertEquals("do Command3", lb52.text());

	}
}
