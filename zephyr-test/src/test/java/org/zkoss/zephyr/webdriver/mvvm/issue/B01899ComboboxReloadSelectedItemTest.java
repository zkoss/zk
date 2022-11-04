package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01899ComboboxReloadSelectedItemTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery cb3 = jq("$w3 $cb3");
		JQuery lb31 = jq("$w3 $lb31");
		JQuery lb32 = jq("$w3 $lb32");
		JQuery cb4 = jq("$w4 $cb4");
		JQuery lb41 = jq("$w4 $lb41");
		JQuery lb42 = jq("$w4 $lb42");

		type(cb3.find("input"), "01");
		waitResponse();
		assertEquals("01", cb3.find("input").val());
		assertEquals("01", lb31.text());
		assertEquals("", lb32.text());
		
		/*type(cb3, "0");
waitResponse();		assertEquals("0", cb1.find("input").val());
		assertEquals("Please select an item!!", lb11.text());
		assertEquals("01", lb12.text());*/
		
		/*type(cb1, "");
waitResponse();		assertEquals("", cb1.find("input").val());
		assertEquals("Please select an item!!", lb11.text());
		assertEquals("01", lb12.text());*/

		type(cb4.find("input"), "01");
		waitResponse();
		assertEquals("01", cb4.find("input").val());
		assertEquals("01", lb41.text());
		assertEquals("", lb42.text());
		
		/*type(cb2, "0");
waitResponse();		assertEquals("0", cb2.find("input").val());
		//assertEquals("Please select an item!!", lb21.text());
		//assertEquals("0", lb22.text());
*/		
		/*type(cb2, "");
waitResponse();		assertEquals("", cb2.find("input").val());
		//assertEquals("Please select an item!!", lb21.text());
		//assertEquals("0", lb22.text());
*/
	}
}
