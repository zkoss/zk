/* B96_ZK_5203Test.java

	Purpose:
		
	Description:
		
	History:
		1:16 PM 2022/10/6, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5203Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		assertThat(jq(".z-window-content").height(), Matchers.greaterThan(100));
	}
}
