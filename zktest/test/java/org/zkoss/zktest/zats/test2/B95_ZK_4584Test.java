/* B95_ZK_4584Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Aug 31, 2020 02:11:11 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4584Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// iPad
		options.addArguments("user-agent=Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1");
		return options;
	}
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		Assert.assertEquals(jq(".z-spinner-input").outerWidth(), jq(".z-spinner-button").width() + jq(".z-spinner-button").positionLeft(), 1);
		Assert.assertEquals(jq(".z-doublespinner-input").outerWidth(), jq(".z-doublespinner-button").width() + jq(".z-doublespinner-button").positionLeft(), 1);
	}
}
