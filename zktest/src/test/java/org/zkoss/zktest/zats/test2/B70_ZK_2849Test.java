package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Created by wenning on 1/20/16.
 */
public class B70_ZK_2849Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        Element cbpp = jq(".z-combobox-popup").get(0);
        assertFalse(cbpp.exists());
        JQuery cbb = jq(".z-combobox-button");
        click(cbb);
        waitResponse();
        assertTrue(cbpp.exists());
        click(cbb);
        waitResponse();
        assertEquals("none", cbpp.eval("style.display"));
    }
}
