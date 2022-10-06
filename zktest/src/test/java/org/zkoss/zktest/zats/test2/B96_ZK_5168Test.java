/* B96_ZK_5168Test.java

	Purpose:
		
	Description:
		
	History:
		6:26 PM 2022/10/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5168Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int height = jq("@groupbox").height();
		assertThat(height * 0.8, Matchers.lessThan(
				jq("@groupbox .z-groupbox-content").height() * 1.0));
	}
}
