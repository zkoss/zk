package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_3505Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String pn1captionid = jq(".z-panel .z-caption").get(0).eval("id");
		String pn2captionid = jq(".z-panel .z-caption").get(1).eval("id");
		String gb1captionid = jq(".z-groupbox .z-caption").get(0).eval("id");
		String gb2captionid = jq(".z-groupbox .z-caption").get(1).eval("id");
		String wd1captionid = jq(".z-window .z-caption").get(0).eval("id");
		String wd2captionid = jq(".z-window .z-caption").get(1).eval("id");
		String a = "";
		WebElement html = driver.findElement(By.tagName("html"));
		for (int i = 0; i <= 20; i++) {
			html.sendKeys(Keys.TAB);
			a += getEval("document.activeElement.id") + " ";
		}
		assertFalse(a.contains(pn1captionid), "panel caption 1 should not be selected");
		assertTrue(a.contains(pn2captionid), "panel caption 2 should be selected");
		assertFalse(a.contains(gb1captionid), "groupbox caption 1 should not be selected");
		assertTrue(a.contains(gb2captionid), "groupbox caption 2 should be selected");
		assertFalse(a.contains(wd1captionid), "window caption 1 should not be selected");
		assertTrue(a.contains(wd2captionid), "window caption 2 should be selected");
	}
}