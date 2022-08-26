/* F50_2943475Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 17:21:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.time.Duration;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_2943475Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		try {
			final JQuery processing = jq("#zk_proc-t");
			click(jq("@radio:eq(4)")); // center
			waitForPageReloaded();
			checkProgressbarPosition(processing,
					() -> (jq("html").width() - processing.width()) / 2,
					() -> (jq("html").height() - processing.height()) / 2);
			waitResponse();

			click(jq("@radio:eq(8)")); // right,bottom
			waitForPageReloaded();
			checkProgressbarPosition(processing,
					() -> jq("html").width() - processing.width(),
					() -> jq("html").height() - processing.height());
		} finally {
			click(jq("@radio:eq(0)")); // restore to default
			waitForPageReloaded();
		}
	}

	private void waitForPageReloaded() {
		sleep(1000); // wait for redirection first
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
	}

	private void checkProgressbarPosition(JQuery processing,
	                                      Supplier<Integer> offsetLeft,
	                                      Supplier<Integer> offsetTop) {
		click(jq("@button"));
		sleep(1000);
		Assertions.assertEquals(offsetLeft.get(), processing.offsetLeft(), 3);
		Assertions.assertEquals(offsetTop.get(), processing.offsetTop(), 3);
	}
}
