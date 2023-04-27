package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_5429Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		driver.manage().window().setSize(new Dimension(820, 1080));
		waitResponse();
		for (int i = 0; i < 11; i++) {
			click(jq(".z-tabbox-right-scroll"));
			waitResponse(true);
		}
		JQuery tab15 = jq("@tab").eq(14);
		click(tab15);
		waitResponse();
		driver.manage().window().setSize(new Dimension(830, 1080));
		waitResponse();
		//in view
		System.out.println("tab15 offsetLeft: " + tab15.toElement().get("offsetLeft"));
		System.out.println("tabs rightmost: " + jq("@tabs").scrollLeft() + jq("@tabs").width());
		assertTrue(parseInt(tab15.toElement().get("offsetLeft")) < (jq("@tabs").scrollLeft() + jq("@tabs").width()));
	}
}
