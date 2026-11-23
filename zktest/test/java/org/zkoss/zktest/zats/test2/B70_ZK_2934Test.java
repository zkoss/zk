/* B70_ZK_2934Test.java
	Purpose:

	Description:

	History:
		Mon Jan 19 16:36:18 CST 2016, Created by wenning
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Created by wenning on 1/19/16.
 */
public class B70_ZK_2934Test extends WebDriverTestCase {

    @Test
    public void test() {
		connect();
		waitResponse(true);
		assertEquals(getEval("zk.currentFocus.uuid"), jq("@button").get(1).get("id"));
    }
}
