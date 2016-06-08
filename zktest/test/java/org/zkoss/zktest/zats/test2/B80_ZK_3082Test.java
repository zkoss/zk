/* B80_ZK_3082Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jun  8, 2016  4:54:21 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3082Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery chosenboxes = jq("@chosenbox");
        subTest(widget(chosenboxes.eq(0)).$n("inp"), 1);
        subTest(widget(chosenboxes.eq(1)).$n("inp"), 3);
        subTest(widget(chosenboxes.eq(2)).$n("inp"), 5);
        subTest(widget(chosenboxes.eq(3)).$n("inp"), 7);
    }

    private void subTest(Element e, int startCount) {
        sendKeys(e, "i");
        waitResponse(true);
        Assert.assertEquals(startCount, count(getZKLog()));
        sendKeys(e, Keys.BACK_SPACE);
        waitResponse(true);
        Assert.assertEquals(startCount + 1, count(getZKLog()));
    }

    private int count(String input) {
        String[] strings = input.split(" ");
        int c = 0;
        for(int i = 0; i < strings.length; i++) {
            if ("onSearching".equals(strings[i])) c++;
        }
        return c;
    }
}
