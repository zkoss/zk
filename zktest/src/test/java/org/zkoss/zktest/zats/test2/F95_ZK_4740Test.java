/* F95_ZK_4740Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 22 10:38:33 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import java.time.Duration;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_4740Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final WebDriverWait wait = new WebDriverWait(driver,
				Duration.ofSeconds(3));
		final WebElement pdfCanvas = toElement(jq("canvas"));
		wait.until(ExpectedConditions.attributeToBeNotEmpty(pdfCanvas, "height"));

		final Dimension pdfSize = pdfCanvas.getSize();
		MatcherAssert.assertThat("Seems it's portrait", pdfSize.width, greaterThan(pdfSize.height));
	}
}
