package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author Jameschu
 */
public class HttpParamTest extends WebDriverTestCase {
    @Test
    public void test() {
        connect("/bind/basic/httpparam.zul");
		click(jq("$cmd1"));
		waitResponse();
		assertTrue(jq("$l12").toWidget().get("value") != null);
		assertTrue(jq("$l13").toWidget().get("value") != null);
		assertTrue(!"".equals(jq("$l12").toWidget().get("value").trim()));
		assertTrue(!"".equals(jq("$l13").toWidget().get("value").trim()));
    }
}
