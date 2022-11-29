package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class Vm_initTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Widget vm1_l1 = jq("$vm1_l1").toWidget();
		Widget vm1_t1 = jq("$vm1_t1").toWidget();
		Widget vm1_l2 = jq("$vm1_l2").toWidget();
		Widget vm1_l3 = jq("$vm1_l3").toWidget();

		assertEquals("AA", vm1_l1.get("value"));
		assertEquals("V1-AA", vm1_t1.get("value"));
		assertEquals("V1-AA", vm1_l2.get("value"));
		assertEquals("V2", vm1_l3.get("value"));

		type(vm1_t1, "OO");
		sendKeys(vm1_t1, Keys.TAB);
		waitResponse();
		assertEquals("OO-AA", vm1_t1.get("value"));
		assertEquals("OO-AA", vm1_l2.get("value"));
		assertEquals("V2", vm1_l3.get("value"));


		click(jq("$vm1_btn").toWidget());
		waitResponse();
		assertEquals("OO-AA", vm1_t1.get("value"));
		assertEquals("OO-AA", vm1_l2.get("value"));
		assertEquals("do command1 AA", vm1_l3.get("value"));

		Widget vm2_l1 = jq("$vm2_l1").toWidget();
		Widget vm2_t1 = jq("$vm2_t1").toWidget();
		Widget vm2_l2 = jq("$vm2_l2").toWidget();
		Widget vm2_l3 = jq("$vm2_l3").toWidget();
		assertEquals("BB", vm2_l1.get("value"));
		assertEquals("V1-BB", vm2_t1.get("value"));
		assertEquals("V1-BB", vm2_l2.get("value"));
		assertEquals("V2", vm2_l3.get("value"));

		type(vm2_t1, "OO");
		sendKeys(vm2_t1, Keys.TAB);
		waitResponse();
		assertEquals("OO-BB", vm2_t1.get("value"));
		assertEquals("OO-BB", vm2_l2.get("value"));
		assertEquals("V2", vm2_l3.get("value"));

		click(jq("$vm2_btn").toWidget());
		waitResponse();
		assertEquals("OO-BB", vm2_t1.get("value"));
		assertEquals("OO-BB", vm2_l2.get("value"));
		assertEquals("do command1 BB", vm2_l3.get("value"));

		Widget vm3_l1 = jq("$vm3_l1").toWidget();
		Widget vm3_t1 = jq("$vm3_t1").toWidget();
		Widget vm3_l2 = jq("$vm3_l2").toWidget();
		Widget vm3_l3 = jq("$vm3_l3").toWidget();
		assertEquals("CC", vm3_l1.get("value"));
		assertEquals("V1-CC", vm3_t1.get("value"));
		assertEquals("V1-CC", vm3_l2.get("value"));
		assertEquals("V2", vm3_l3.get("value"));

		type(vm3_t1, "OO");
		sendKeys(vm3_t1, Keys.TAB);
		waitResponse();
		assertEquals("OO-CC", vm3_t1.get("value"));
		assertEquals("OO-CC", vm3_l2.get("value"));
		assertEquals("V2", vm3_l3.get("value"));

		click(jq("$vm3_btn").toWidget());
		waitResponse();
		assertEquals("OO-CC", vm3_t1.get("value"));
		assertEquals("OO-CC", vm3_l2.get("value"));
		assertEquals("do command1 CC", vm3_l3.get("value"));

		Widget vm4_l1 = jq("$vm4_l1").toWidget();
		Widget vm4_t1 = jq("$vm4_t1").toWidget();
		Widget vm4_l2 = jq("$vm4_l2").toWidget();
		Widget vm4_l3 = jq("$vm4_l3").toWidget();
		assertEquals("XX", vm4_l1.get("value"));
		assertEquals("V1-XX", vm4_t1.get("value"));
		assertEquals("V1-XX", vm4_l2.get("value"));
		assertEquals("V2", vm4_l3.get("value"));

		type(vm4_t1, "OO");
		sendKeys(vm4_t1, Keys.TAB);
		waitResponse();
		assertEquals("OO-XX", vm4_t1.get("value"));
		assertEquals("OO-XX", vm4_l2.get("value"));
		assertEquals("V2", vm4_l3.get("value"));

		click(jq("$vm4_btn").toWidget());
		waitResponse();
		assertEquals("OO-XX", vm4_t1.get("value"));
		assertEquals("OO-XX", vm4_l2.get("value"));
		assertEquals("do command1 XX", vm4_l3.get("value"));
	}
}