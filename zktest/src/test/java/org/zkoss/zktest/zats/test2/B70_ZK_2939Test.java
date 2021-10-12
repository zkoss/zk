/* B70_ZK_2939Test.java
	Purpose:

	Description:

	History:
		Mon Jan 20 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/20/16.
 */
public class B70_ZK_2939Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        JQuery lis = jq(".z-listitem");
        JQuery lbs = jq(".z-label");
        click(jq("@button"));
        waitResponse();
        String model = widget(lbs.get(lbs.length() - 1)).eval("_value");
        String ms[] = model.substring(model.indexOf("[") + 1, model.indexOf("]")).split(", ");
        for (int i = 0 ; i < lis.length(); i++) {
            if (widget(lis.get(i)).eval("_visible") == "true") {
                assertEquals(widget(lbs.get(i + 1)).eval("_value"), ms[i]);
            }
        }
    }

}
