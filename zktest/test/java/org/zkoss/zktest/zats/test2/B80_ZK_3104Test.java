/* B80_ZK_3104Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 28 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author jameschu
 */
public class B80_ZK_3104Test extends WebDriverTestCase {

	@Test
	public void test() throws IOException {
		connect();
		JQuery btns = jq("@button");
		click(btns.eq(0));
		waitResponse();
		assertEquals("true", getZKLog());
		closeZKLog();
		waitResponse();
		click(btns.eq(1));
		waitResponse();
		assertEquals("true", getZKLog());
		closeZKLog();
		waitResponse();
		click(btns.eq(2));
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
