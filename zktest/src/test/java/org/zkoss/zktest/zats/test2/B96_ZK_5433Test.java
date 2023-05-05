package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_5433Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		driver.manage().window().setSize(new Dimension(820, 1080));
		driver.manage().window().setSize(new Dimension(810, 1080)); // force resizing the browser
		waitResponse(true);
		JQuery tab15 = jq("@tab").eq(14);
		//in view
		System.out.println("tab15 offsetLeft: " + tab15.toElement().get("offsetLeft"));
		System.out.println("tabs rightmost: " + (jq("@tabs").scrollLeft() + jq("@tabs").width()));
		assertTrue(parseInt(tab15.toElement().get("offsetLeft")) < (jq("@tabs").scrollLeft() + jq("@tabs").width()));
	}
}
