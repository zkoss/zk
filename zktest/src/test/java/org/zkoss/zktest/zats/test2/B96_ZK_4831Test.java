/* B96_ZK_4831Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 09 11:53:56 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4831Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		String background = jq("$div1").css("background-image");
		Assertions.assertEquals("url(\"http://localhost:8080/zktest/test2/img/icon_browser.png\")", background);
		background = jq("$div2").css("background-image");
		Assertions.assertEquals("url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==\")", background);
	}
}
