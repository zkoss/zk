/* F50_ZK_214Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 15:35:03 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.matchesRegex;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_ZK_214Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		Pattern pattern = Pattern.compile("\\[<Textbox \\w{5,}#CA>, <Textbox \\w{5,}#TX>, <Textbox \\w{5,}#WA>] - \\[CA, TX, WA]");
		Assert.assertThat(getMessageBoxContent(), matchesRegex(pattern));

		Assert.assertEquals("Hello! ZK.", jq("$xwin @label:last").text());
	}
}
