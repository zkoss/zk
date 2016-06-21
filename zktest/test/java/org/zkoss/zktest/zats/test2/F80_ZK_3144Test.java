package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

import static org.junit.Assert.assertFalse;

/**
 * Created by wenning on 6/17/16.
 */
public class F80_ZK_3144Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        Element btn1 = jq("@button").get(0);
        Element btn2 = jq("@button").get(1);
        Element btn3 = jq("@button").get(2);
        Element btn4 = jq("@button").get(3);
        click(btn1);
        waitResponse(true);
        click(btn1);
        waitResponse(true);
        assertFalse(jq(".z-messagebox-error:eq(0)").exists());
        click(btn2);
        waitResponse(true);
        click(btn2);
        waitResponse(true);
        assertFalse(jq(".z-messagebox-error:eq(0)").exists());
        click(btn3);
        waitResponse(true);
        click(btn3);
        waitResponse(true);
        assertFalse(jq(".z-messagebox-error:eq(0)").exists());
        click(btn4);
        waitResponse(true);
        click(btn4);
        waitResponse(true);
        assertFalse(jq(".z-messagebox-error:eq(0)").exists());
    }

}
