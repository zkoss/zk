package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01088FormUpdateTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		JQuery lb11 = jq("$lb11");
		JQuery lb12 = jq("$lb12");
		JQuery tb11 = jq("$tb11");
		JQuery tb12 = jq("$tb12");
		JQuery lb21 = jq("$lb21");
		JQuery lb22 = jq("$lb22");
		JQuery reload = jq("$reload");
		JQuery save = jq("$save");


		assertEquals("Dennis", lb11.text());
		assertEquals("Chen", lb12.text());
		assertEquals("Dennis", tb11.val());
		assertEquals("Chen", tb12.val());
		assertEquals("Dennis Chen", lb21.text());
		assertEquals("false", lb22.text());

		type(tb11, "chunfu");
		waitResponse();
		type(tb12, "chang");
		waitResponse();
		assertEquals("Dennis", lb11.text());
		assertEquals("Chen", lb12.text());
		assertEquals("chunfu", tb11.val());
		assertEquals("chang", tb12.val());
		assertEquals("Dennis Chen", lb21.text());
		assertEquals("true", lb22.text());

		click(reload);
		waitResponse();
		assertEquals("Dennis", lb11.text());
		assertEquals("Chen", lb12.text());
		assertEquals("Dennis", tb11.val());
		assertEquals("Chen", tb12.val());
		assertEquals("Dennis Chen", lb21.text());
		assertEquals("false", lb22.text());

		type(tb11, "chunfu");
		waitResponse();
		type(tb12, "chang");
		waitResponse();
		click(save);
		waitResponse();
		assertEquals("chunfu", lb11.text());
		assertEquals("chang", lb12.text());
		assertEquals("chunfu", tb11.val());
		assertEquals("chang", tb12.val());
		assertEquals("chunfu chang", lb21.text());
		assertEquals("false", lb22.text());


	}
}
