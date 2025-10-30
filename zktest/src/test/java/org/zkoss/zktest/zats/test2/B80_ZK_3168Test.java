package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * 
 * @author Jameschu
 */
public class B80_ZK_3168Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        sendKeys(widget(jq("@chosenbox")).$n("inp"), "I");
		waitResponse();
		click(jq(".z-chosenbox-option:visible").eq(2));
        waitResponse();
        assertEquals(1, jq(".z-chosenbox-item").length());
		assertEquals(1, jq(".z-listcell-content").length());
		assertEquals("Item1", jq(".z-listcell-content").text());
    }
}
