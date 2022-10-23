package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author Jameschu
 */
public class HttpParamTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/httpparam.zul");
		click(jq("$cmd1"));
		waitResponse();
		assertTrue(jq("$l12").toWidget().get("value") != null);
		assertTrue(jq("$l13").toWidget().get("value") != null);
		assertTrue(!"".equals(jq("$l12").toWidget().get("value").trim()));
		assertTrue(!"".equals(jq("$l13").toWidget().get("value").trim()));
	}
}
