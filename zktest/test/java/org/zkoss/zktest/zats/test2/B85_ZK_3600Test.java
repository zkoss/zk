/* B85_ZK_3600Test.java

    Purpose:

    Description:

    History:
            Thu Mar 08 12:24:52 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.util.ArrayList;
import java.util.List;

public class B85_ZK_3600Test extends WebDriverTestCase {

    private List<Integer> widths = new ArrayList<>();

    @Test
    public void test() {
        connect();

        initWidths(jq(".z-columns-bar"));
        initWidths(jq(".z-listhead-bar"));

        int originWidth = widths.get(0);
        for (int i = 1; i < widths.size(); i++)
            Assert.assertEquals(originWidth, (int) widths.get(i));

        click(jq("@button"));
        waitResponse();

        Assert.assertEquals(originWidth, jq(".z-columns-bar").eq(0).width());
        Assert.assertEquals(originWidth, jq(".z-listhead-bar").eq(0).width());
    }

    private void initWidths(JQuery jq) {
        for (JQuery aJq : jq)
            widths.add(aJq.width());
    }
}
