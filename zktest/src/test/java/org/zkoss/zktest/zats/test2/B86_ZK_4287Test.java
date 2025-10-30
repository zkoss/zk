/* B86_ZK_4287Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 08 17:34:37 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4287Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		String src = jq("script[src$=\"zul.inp.wpd\"]").attr("src");
		assertThat(src, not(endsWith("zkau/web/js/zul.inp.wpd")));
	}
}
