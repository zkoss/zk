/* B95_ZK_4765Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 13, 2021 02:48:18 PM, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4765Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		//test splitter
		JQuery jqSplitter = jq("@splitter");
		Element splitterBtn = jqSplitter.toWidget().$n("btn");
		int btnPosLeft = jq(splitterBtn).positionLeft();
		click(splitterBtn);
		waitResponse();
		getActions().moveToElement(driver.findElement(jqSplitter))
				.clickAndHold()
				.moveByOffset(100, 0)
				.release()
				.perform();
		waitResponse();
		assertThat(jq(splitterBtn).positionLeft(), greaterThan(btnPosLeft + 10)); //should move apparently

		//test splitlayout
		JQuery jqSplitlayout = jq("@splitlayout");
		splitterBtn = jqSplitlayout.toWidget().$n("splitter-btn");
		btnPosLeft = jq(splitterBtn).positionLeft();
		click(splitterBtn);
		waitResponse();
		getActions().moveToElement(driver.findElement(jqSplitlayout.toWidget().$n("splitter")))
				.clickAndHold()
				.moveByOffset(100, 0)
				.release()
				.perform();
		waitResponse();
		assertThat(jq(splitterBtn).positionLeft(), greaterThan(btnPosLeft + 10)); //should move apparently
	}
}
