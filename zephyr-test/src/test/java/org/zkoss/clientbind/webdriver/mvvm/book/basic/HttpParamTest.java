package org.zkoss.clientbind.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author Jameschu
 */
public class HttpParamTest extends ClientBindTestCase {
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
