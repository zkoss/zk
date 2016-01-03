/* B80_ZK_3011Test.java
	Purpose:

	Description:

	History:
		Fri Dec 25 10:35:42 CST 2015, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 12/25/15.
 */
public class B80_ZK_3011Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        JQuery img = jq("img");
        String img1Src = img.get(0).get("src");
        int i = img1Src.indexOf("img/");
        assertEquals("img/icon_users.png", img1Src.substring(i));
        click(jq("@button"));
        waitResponse();
        String img2Src = img.get(1).get("src");
        assertEquals("img/icon_users.png", img2Src.substring(i));
    }
}
