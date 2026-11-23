/* B96_ZK_4701Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 23 09:43:02 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import static org.hamcrest.Matchers.lessThan;

import static org.hamcrest.Matchers.greaterThan;

public class B96_ZK_4701Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		MatcherAssert.assertThat(
			jq(".z-chosenbox-textcontent").width(), lessThan(jq(".z-chosenbox-input").width()));
	}
}
