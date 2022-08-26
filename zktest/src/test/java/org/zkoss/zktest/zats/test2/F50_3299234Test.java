/* F50_3299234Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 15:30:26 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3299234Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Pattern pattern = Pattern.compile("\\[<Textbox \\w{5,}#CA>, <Textbox \\w{5,}#TX>, <Textbox \\w{5,}#WA>] - \\[CA, TX, WA]");
		assertThat(getMessageBoxContent(), matchesRegex(pattern));
	}
}
