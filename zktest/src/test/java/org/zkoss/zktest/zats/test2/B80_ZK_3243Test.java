package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertTrue;

/**
 * Created by wenning on 6/22/16.
 */
public class B80_ZK_3243Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        click(jq(".z-combobox-button"));
        waitResponse(true);
        assertTrue(parseInt(jq(".z-combobox-popup").css("width")) < parseInt(getEval("jq.innerWidth()")));
    }

}
