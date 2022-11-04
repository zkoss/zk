package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00848ValidationMessagesEmptyTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery l11 = jq("$l11");
		JQuery l12 = jq("$l12");
		JQuery t21 = jq("$t21");
		JQuery t22 = jq("$t22");
		JQuery t31 = jq("$t31");
		JQuery t32 = jq("$t32");
		JQuery m31 = jq("$m31");
		JQuery m32 = jq("$m32");
		JQuery btn1 = jq("$btn1");
		JQuery t41 = jq("$t41");
		JQuery t42 = jq("$t42");
		JQuery m41 = jq("$m41");
		JQuery m42 = jq("$m42");
		JQuery m43 = jq("$m43");
		JQuery m44 = jq("$m44");
		JQuery m45 = jq("$m45");
		JQuery m46 = jq("$m46");
		JQuery btn2 = jq("$btn2");
		JQuery btn3 = jq("$btn3");
		JQuery vmsize = jq("$vmsize");
		JQuery vmempty = jq("$vmempty");

		assertEquals("ABC", l11.text());
		assertEquals("10", l12.text());

		assertEquals("ABC", t21.val());
		assertEquals("10", t22.val());

		assertEquals("ABC", t31.val());
		assertEquals("10", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("0", vmsize.text());
		assertEquals("true", vmempty.text());

		type(t21, "ABCD");
		waitResponse();
		type(t22, "6");
		waitResponse();
		assertEquals("ABC", l11.text());
		assertEquals("10", l12.text());

		assertEquals("ABCD", t21.val());
		assertEquals("6", t22.val());

		assertEquals("ABC", t31.val());
		assertEquals("10", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("2", vmsize.text());
		assertEquals("false", vmempty.text());

		type(t21, "Abc");
		waitResponse();
		type(t22, "33");
		waitResponse();
		assertEquals("Abc", l11.text());
		assertEquals("33", l12.text());

		assertEquals("Abc", t21.val());
		assertEquals("33", t22.val());

		assertEquals("Abc", t31.val());
		assertEquals("33", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("0", vmsize.text());
		assertEquals("true", vmempty.text());

		type(t31, "XXX");
		waitResponse();
		type(t32, "1");
		waitResponse();
		assertEquals("Abc", l11.text());
		assertEquals("33", l12.text());

		assertEquals("Abc", t21.val());
		assertEquals("33", t22.val());

		assertEquals("XXX", t31.val());
		assertEquals("1", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("0", vmsize.text());
		assertEquals("true", vmempty.text());

		click(btn1);
		waitResponse();
		assertEquals("Abc", l11.text());
		assertEquals("33", l12.text());

		assertEquals("Abc", t21.val());
		assertEquals("33", t22.val());

		assertEquals("XXX", t31.val());
		assertEquals("1", t32.val());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.text());
		assertEquals("value must not < 10 or > 100, but is 1", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("2", vmsize.text());
		assertEquals("false", vmempty.text());

		type(t32, "55");
		waitResponse();
		assertEquals("Abc", l11.text());
		assertEquals("33", l12.text());

		assertEquals("Abc", t21.val());
		assertEquals("33", t22.val());

		assertEquals("XXX", t31.val());
		assertEquals("55", t32.val());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.text());
		assertEquals("value must not < 10 or > 100, but is 1", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());

		click(btn1);
		waitResponse();
		assertEquals("Abc", l11.text());
		assertEquals("33", l12.text());

		assertEquals("Abc", t21.val());
		assertEquals("33", t22.val());

		assertEquals("XXX", t31.val());
		assertEquals("55", t32.val());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("1", vmsize.text());
		assertEquals("false", vmempty.text());

		type(t31, "aBC");
		waitResponse();
		assertEquals("Abc", l11.text());
		assertEquals("33", l12.text());

		assertEquals("Abc", t21.val());
		assertEquals("33", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());

		click(btn1);
		waitResponse();
		assertEquals("aBC", l11.text());
		assertEquals("55", l12.text());

		assertEquals("aBC", t21.val());
		assertEquals("55", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("ABC", t41.val());
		assertEquals("10", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("0", vmsize.text());
		assertEquals("true", vmempty.text());

		type(t41, "YYY");
		waitResponse();
		type(t42, "1999");
		waitResponse();
		assertEquals("aBC", l11.text());
		assertEquals("55", l12.text());

		assertEquals("aBC", t21.val());
		assertEquals("55", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("YYY", t41.val());
		assertEquals("1999", t42.val());
		assertEquals("value must equals ignore case 'abc', but is YYY", m41.text());
		assertEquals("value must not < 10 or > 100, but is 1999", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());
		assertEquals("2", vmsize.text());
		assertEquals("false", vmempty.text());

		click(btn2);
		waitResponse();
		assertEquals("aBC", l11.text());
		assertEquals("55", l12.text());

		assertEquals("aBC", t21.val());
		assertEquals("55", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("YYY", t41.val());
		assertEquals("1999", t42.val());
		assertEquals("value must equals ignore case 'abc', but is YYY", m41.text());
		assertEquals("value must not < 10 or > 100, but is 1999", m42.text());
		assertEquals("value must equals 'AbC', but is ABC", m43.text());
		assertEquals("value must equals 'AbC', but is ABC", m44.text());
		assertEquals("value must equals 'AbC', but is ABC", m45.text());
		assertEquals("extra validation info ABC", m46.text());

		assertEquals("4", vmsize.text());
		assertEquals("false", vmempty.text());

		click(btn2);
		waitResponse();
		assertEquals("aBC", l11.text());
		assertEquals("55", l12.text());

		assertEquals("aBC", t21.val());
		assertEquals("55", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("YYY", t41.val());
		assertEquals("1999", t42.val());
		assertEquals("value must equals ignore case 'abc', but is YYY", m41.text());
		assertEquals("value must not < 10 or > 100, but is 1999", m42.text());
		assertEquals("value must equals 'AbC', but is ABC", m43.text());
		assertEquals("value must equals 'AbC', but is ABC", m44.text());
		assertEquals("value must equals 'AbC', but is ABC", m45.text());
		assertEquals("extra validation info ABC", m46.text());

		assertEquals("4", vmsize.text());
		assertEquals("false", vmempty.text());

		type(t41, "abc");
		waitResponse();
		type(t42, "77");
		waitResponse();
		assertEquals("aBC", l11.text());
		assertEquals("55", l12.text());

		assertEquals("aBC", t21.val());
		assertEquals("55", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("abc", t41.val());
		assertEquals("77", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("value must equals 'AbC', but is ABC", m43.text());
		assertEquals("value must equals 'AbC', but is ABC", m44.text());
		assertEquals("value must equals 'AbC', but is ABC", m45.text());
		assertEquals("extra validation info ABC", m46.text());

		assertEquals("2", vmsize.text());
		assertEquals("false", vmempty.text());

		click(btn2);
		waitResponse();
		assertEquals("aBC", l11.text());
		assertEquals("55", l12.text());

		assertEquals("aBC", t21.val());
		assertEquals("55", t22.val());

		assertEquals("aBC", t31.val());
		assertEquals("55", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("abc", t41.val());
		assertEquals("77", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("value must equals 'AbC', but is abc", m43.text());
		assertEquals("value must equals 'AbC', but is abc", m44.text());
		assertEquals("value must equals 'AbC', but is abc", m45.text());
		assertEquals("extra validation info abc", m46.text());

		assertEquals("2", vmsize.text());
		assertEquals("false", vmempty.text());

		type(t41, "AbC");
		waitResponse();
		click(btn2);
		waitResponse();
		assertEquals("AbC", l11.text());
		assertEquals("77", l12.text());

		assertEquals("AbC", t21.val());
		assertEquals("77", t22.val());

		assertEquals("AbC", t31.val());
		assertEquals("77", t32.val());
		assertEquals("", m31.text());
		assertEquals("", m32.text());

		assertEquals("AbC", t41.val());
		assertEquals("77", t42.val());
		assertEquals("", m41.text());
		assertEquals("", m42.text());
		assertEquals("", m43.text());
		assertEquals("", m44.text());

		type(t31, "YYY");
		waitResponse();
		type(t32, "2");
		waitResponse();
		click(btn1);
		waitResponse();
		assertEquals("YYY", t31.val());
		assertEquals("2", t32.val());
		assertEquals("value must equals ignore case 'abc', but is YYY", m31.text());
		assertEquals("value must not < 10 or > 100, but is 2", m32.text());

		assertEquals("2", vmsize.text());
		assertEquals("false", vmempty.text());

		click(btn3);
		waitResponse();
		assertEquals("AbC", t31.val());
		assertEquals("2", t32.val());
		assertEquals("", m31.text());
		assertEquals("value must not < 10 or > 100, but is 2", m32.text());

		assertEquals("1", vmsize.text());
		assertEquals("false", vmempty.text());
	}
}
