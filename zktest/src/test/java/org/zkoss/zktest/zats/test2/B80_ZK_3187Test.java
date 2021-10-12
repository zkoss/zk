/* B80_ZK_3187Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 14:30:31 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jruby.RubyProcess;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 *
 * @author jameschu
 */
public class B80_ZK_3187Test extends WebDriverTestCase {
	@Test
	public void test() throws InterruptedException {
		connect();
		for (int i = 1; i < 4; i++) {
			click(jq("$setInc" + i));
			waitResponse(true);
			assertTrue(getZKLog().contains("mydiv attached"));
			closeZKLog();
			waitResponse(true);
			click(jq("$clearInc" + i));
			waitResponse(true);
			assertTrue(getZKLog().contains("mydiv detached"));
			closeZKLog();
		}
	}
}
