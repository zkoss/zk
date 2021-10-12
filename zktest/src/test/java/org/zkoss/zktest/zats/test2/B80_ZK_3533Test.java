/** B80_ZK_3196Test.java.

	Purpose:
		
	Description:
		
	History:
 		Thu Dec 1 16:00:12 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 *
 */
@NotThreadSafe
public class B80_ZK_3533Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();

		try {
			click(jq(".z-panel-maximize"));
			waitResponse();
			assertThat(jq(".z-panel-maximize").attr("title"), not(containsString("2711")));
		} finally {
			click(jq("@button"));
			waitResponse();
		}
	}
}
