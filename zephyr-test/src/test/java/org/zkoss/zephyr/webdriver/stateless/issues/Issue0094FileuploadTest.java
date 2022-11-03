/* Issue0094FileuploadTest.java

	Purpose:
		
	Description:
		
	History:
		3:19 PM 2022/1/13, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Issue0094FileuploadTest extends WebDriverTestCase {
	@Test
	public void testFileupload() {
		connect("/stateless/issue0094");
		sendKeys(jq("input[type=file]"), System.getProperty("user.dir") + "/src/main/webapp/stateless/ZK-Logo.gif");
		click(jq("button:contains(Submit)"));
		waitResponse();
		assertEquals("ZK-Logo.gif", jq("$msg").text());

		// try again for non-desktop case
		sendKeys(jq("input[type=file]"), System.getProperty("user.dir") + "/src/main/webapp/stateless/video.sul");
		click(jq("button:contains(Submit)"));
		waitResponse();
		assertEquals("video.sul", jq("$msg").text());
	}
}
