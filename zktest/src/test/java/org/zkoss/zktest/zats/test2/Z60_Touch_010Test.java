/* Z60_Touch_010Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 18:20:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.TouchWebDriverTestCase;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class Z60_Touch_010Test extends TouchWebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();

		WebElement listbox = toElement(jq("@listbox"));
		scroll(listbox, 0, 3000);
		waitResponse();
		WebElement grid = toElement(jq("@grid"));
		scroll(grid, 0, 3000);
		waitResponse();
		assertNotEquals(0, jq("@listbox .z-listbox-body").scrollTop());
		assertNotEquals(0, jq("@grid .z-grid-body").scrollTop());
	}
}