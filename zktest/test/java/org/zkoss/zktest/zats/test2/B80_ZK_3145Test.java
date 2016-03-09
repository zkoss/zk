/* B80_ZK_3145Test.java
	Purpose:

	Description:

	History:
		Tue Mar 8 16:12:46 CST 2016, Created by jameschu
Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_3145Test extends WebDriverTestCase {

    @Test
    public void test() {
		connect();
		click(jq("@button").get(0));
		waitResponse(true);
		String uuid = jq("@chosenbox").get(0).get("id");
		assertEquals(getEval("zk.currentFocus.uuid"), uuid);
		click(jq("@button").get(1));
		waitResponse(true);
		assertNotSame(getEval("zk.currentFocus.uuid"), uuid);
		click(jq("@button").get(2));
		waitResponse(true);
		assertEquals(getEval("zk.currentFocus.uuid"), uuid);
    }
}
