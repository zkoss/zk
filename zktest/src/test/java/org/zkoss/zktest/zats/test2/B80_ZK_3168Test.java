package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

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
