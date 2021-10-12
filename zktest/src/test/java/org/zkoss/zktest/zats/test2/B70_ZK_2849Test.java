package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
