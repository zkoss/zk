/* B85_ZK_3861Test.java

    Purpose:

    Description:

    History:
            Fri Mar 09 16:45:26 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3861Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();

        JQuery pgiInput = jq(".z-paging-input");

        click(jq("@button").eq(1));
        waitResponse(true);

        Assert.assertEquals(2, Integer.parseInt(pgiInput.val()));

        click(jq("@button").eq(0));
        waitResponse(true);

        Assert.assertEquals(1, Integer.parseInt(pgiInput.val()));

    }
}
