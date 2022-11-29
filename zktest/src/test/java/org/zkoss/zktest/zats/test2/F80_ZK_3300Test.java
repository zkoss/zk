package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F80_ZK_3300Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		testMVVM();
	}

	@Test
	public void testZHTML() {
		connect("/test2/F80-ZK-3300.zhtml");
		waitResponse();
		testMVVM();
	}

	private void testMVVM() {
		
	}
}
