package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00901ReferenceBindingTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery tb11 = jq("$tb11");
		JQuery tb12 = jq("$tb12");
		JQuery tb13 = jq("$tb13");
		JQuery tb21 = jq("$tb21");
		JQuery tb22 = jq("$tb22");
		JQuery tb23 = jq("$tb23");
		JQuery tb32 = jq("$tb32");
		JQuery tb33 = jq("$tb33");
		JQuery tb43 = jq("$tb43");
		JQuery replace1 = jq("$replace1");
		JQuery replace2 = jq("$replace2");

		assertEquals("Dennis", tb11.val());
		assertEquals("Dennis", tb21.val());
		assertEquals("1234", tb12.val());
		assertEquals("1234", tb22.val());
		assertEquals("1234", tb32.val());
		assertEquals("11 street", tb13.val());
		assertEquals("11 street", tb23.val());
		assertEquals("11 street", tb33.val());
		assertEquals("11 street", tb43.val());

		type(tb11, "Ray");
		waitResponse();
		assertEquals("Ray", tb11.val());
		assertEquals("Ray", tb21.val());
		assertEquals("1234", tb12.val());
		assertEquals("1234", tb22.val());
		assertEquals("1234", tb32.val());
		assertEquals("11 street", tb13.val());
		assertEquals("11 street", tb23.val());
		assertEquals("11 street", tb33.val());
		assertEquals("11 street", tb43.val());

		type(tb21, "Bluce");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("1234", tb12.val());
		assertEquals("1234", tb22.val());
		assertEquals("1234", tb32.val());
		assertEquals("11 street", tb13.val());
		assertEquals("11 street", tb23.val());
		assertEquals("11 street", tb33.val());
		assertEquals("11 street", tb43.val());

		type(tb12, "111");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("111", tb12.val());
		assertEquals("111", tb22.val());
		assertEquals("111", tb32.val());
		assertEquals("11 street", tb13.val());
		assertEquals("11 street", tb23.val());
		assertEquals("11 street", tb33.val());
		assertEquals("11 street", tb43.val());

		type(tb22, "222");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("222", tb12.val());
		assertEquals("222", tb22.val());
		assertEquals("222", tb32.val());
		assertEquals("11 street", tb13.val());
		assertEquals("11 street", tb23.val());
		assertEquals("11 street", tb33.val());
		assertEquals("11 street", tb43.val());

		type(tb32, "333");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("333", tb12.val());
		assertEquals("333", tb22.val());
		assertEquals("333", tb32.val());
		assertEquals("11 street", tb13.val());
		assertEquals("11 street", tb23.val());
		assertEquals("11 street", tb33.val());
		assertEquals("11 street", tb43.val());

		type(tb13, "street1");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("333", tb12.val());
		assertEquals("333", tb22.val());
		assertEquals("333", tb32.val());
		assertEquals("street1", tb13.val());
		assertEquals("street1", tb23.val());
		assertEquals("street1", tb33.val());
		assertEquals("street1", tb43.val());

		type(tb23, "street2");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("333", tb12.val());
		assertEquals("333", tb22.val());
		assertEquals("333", tb32.val());
		assertEquals("street2", tb13.val());
		assertEquals("street2", tb23.val());
		assertEquals("street2", tb33.val());
		assertEquals("street2", tb43.val());

		type(tb33, "street3");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("333", tb12.val());
		assertEquals("333", tb22.val());
		assertEquals("333", tb32.val());
		assertEquals("street3", tb13.val());
		assertEquals("street3", tb23.val());
		assertEquals("street3", tb33.val());
		assertEquals("street3", tb43.val());

		type(tb43, "street4");
		waitResponse();
		assertEquals("Bluce", tb11.val());
		assertEquals("Bluce", tb21.val());
		assertEquals("333", tb12.val());
		assertEquals("333", tb22.val());
		assertEquals("333", tb32.val());
		assertEquals("street4", tb13.val());
		assertEquals("street4", tb23.val());
		assertEquals("street4", tb33.val());
		assertEquals("street4", tb43.val());

		click(replace1);
		waitResponse();
		assertEquals("Alex", tb11.val());
		assertEquals("Alex", tb21.val());
		assertEquals("888", tb12.val());
		assertEquals("888", tb22.val());
		assertEquals("888", tb32.val());
		assertEquals("888 st", tb13.val());
		assertEquals("888 st", tb23.val());
		assertEquals("888 st", tb33.val());
		assertEquals("888 st", tb43.val());

		click(replace2);
		waitResponse();
		assertEquals("Alex", tb11.val());
		assertEquals("Alex", tb21.val());
		assertEquals("999", tb12.val());
		assertEquals("999", tb22.val());
		assertEquals("999", tb32.val());
		assertEquals("999 st", tb13.val());
		assertEquals("999 st", tb23.val());
		assertEquals("999 st", tb33.val());
		assertEquals("999 st", tb43.val());
	}
}
