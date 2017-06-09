/** B85_ZK_3637Test.java.

 Purpose:

 Description:

 History:
 	Tue June 6 17:14:22 CST 2017, Created by jameschu

 Copyright (C) 2017 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 *
 */
public class B85_ZK_3637Test extends WebDriverTestCase {
    @Test
    public void test() {
		connect();
		for (int i = 1; i < 9; i++) {
			click(jq("@button:eq(" + i +")"));
			waitResponse();
			String log = "";
			if (i <= 4)
				log = "clicked_local";
			else
				log = "clicked_global";
			assertEquals(log, getZKLog());
			closeZKLog();
			waitResponse();
		}
	}
}