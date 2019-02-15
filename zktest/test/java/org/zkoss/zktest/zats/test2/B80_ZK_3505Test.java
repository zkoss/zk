package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
		assertFalse("panel caption 1 should not be selected", a.contains(pn1captionid));
		assertTrue("panel caption 2 should be selected", a.contains(pn2captionid));
		assertFalse("groupbox caption 1 should not be selected", a.contains(gb1captionid));
		assertTrue("groupbox caption 2 should be selected", a.contains(gb2captionid));
		assertFalse("window caption 1 should not be selected", a.contains(wd1captionid));
		assertTrue("window caption 2 should be selected", a.contains(wd2captionid));
	}
}