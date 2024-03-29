/* B50_2871082Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 16:09:35 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B50_2871082Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@menu"));
		waitResponse();

		JQuery links = jq("@menuitem a");
		assertThat(stripJsessionid(links.eq(0).attr("href")), startsWith("http://www.zkoss.org"));
		assertThat(stripJsessionid(links.eq(1).attr("href")), endsWith("/zktest/test2/test.zul"));
	}
}
