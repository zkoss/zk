/* B70_ZK_2858Test.java
	Purpose:

	Description:

	History:
		Mon Jan 20 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/20/16.
 */
public class B70_ZK_2858Test extends WebDriverTestCase {

    @Test
    public void test() {
        connect();
        sleep(300);
        String zklog = getZKLog();
        assertEquals(zklog.substring(zklog.lastIndexOf("image")), "image/svg+xml; charset=UTF-8");
    }

}