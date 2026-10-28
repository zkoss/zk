/* B96_ZK_4920Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Jun 21 16:05:52 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

// IceBlue-only: it measures the first cell, which IceBlue's fixed 44px autopaging rows make as tall as the button; in Marble that label-only cell is 20px while the button cell still fits the button.
@Tag("IceBlueOnly")
public class B96_ZK_4920Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int buttonHeight = jq("@button").outerHeight();
		MatcherAssert.assertThat(jq(".z-listcell-content").height(), greaterThanOrEqualTo(buttonHeight));
		MatcherAssert.assertThat(jq(".z-treecell-content").height(), greaterThanOrEqualTo(buttonHeight));
		MatcherAssert.assertThat(jq(".z-row-content").height(), greaterThanOrEqualTo(buttonHeight));
	}
}
