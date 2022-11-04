package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01194NestedVMInitTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery headerNameLb = jq("$headerNameLb");
		JQuery vmsNameTxb = jq("$vmsNameTxb");
		JQuery vmsDescTxb = jq("$vmsDescTxb");
		JQuery vmInnerVmDescTxb = jq("$vmInnerVmDescTxb");
		JQuery vmInnerVmDescLb = jq("$vmInnerVmDescLb");
		JQuery outerNameLb = jq("$outerNameLb");
		JQuery outerDescTxb = jq("$outerDescTxb");
		String text = vmsDescTxb.val();

		assertTrue(text.length() > 0);
		assertEquals(text, vmInnerVmDescTxb.val());
		assertEquals(text, vmInnerVmDescLb.text());
		assertEquals(text, outerDescTxb.val());

		text = "Ian Tsai 1";
		type(vmsNameTxb, text);
		waitResponse();
		assertEquals(text, headerNameLb.text());
		assertEquals(text, outerNameLb.val());

		text = "AAA";
		type(vmsDescTxb, text);
		waitResponse();
		assertEquals(text, vmInnerVmDescTxb.val());
		assertEquals(text, vmInnerVmDescLb.text());
		assertEquals(text, outerDescTxb.val());

		text = "BBB";
		type(vmInnerVmDescTxb, text);
		waitResponse();
		assertEquals(text, vmsDescTxb.val());
		assertEquals(text, vmInnerVmDescLb.text());
		assertEquals(text, outerDescTxb.val());

		text = "CCC";
		type(outerDescTxb, text);
		waitResponse();
		assertEquals(text, vmsDescTxb.val());
		assertEquals(text, vmInnerVmDescLb.text());
		assertEquals(text, vmInnerVmDescTxb.val());
	}
}
