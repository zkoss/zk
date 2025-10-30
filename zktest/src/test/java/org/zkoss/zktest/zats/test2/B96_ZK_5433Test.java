package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B96_ZK_5433Test extends WebDriverTestCase {

	// fix Chrome's headless mode doesn't support resizing the browser
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		options.addArguments(new String[]{"window-size=820,1080"});
		return options;
	}

	@Test
	public void test() {
		connect();
		waitResponse();
//		Chrome's headless mode doesn't support resizing the browser
//		driver.manage().window().setSize(new Dimension(820, 1080));
//		driver.manage().window().setSize(new Dimension(800, 1080)); // force resizing the browser
//		waitResponse(true);
		JQuery tab15 = jq("@tab").eq(14);
		//in view
		System.out.println("tab15 offsetLeft: " + tab15.toElement().get("offsetLeft"));
		System.out.println("tabs rightmost: " + (jq("@tabs").scrollLeft() + jq("@tabs").width()));
		assertTrue(parseInt(tab15.toElement().get("offsetLeft")) < (jq("@tabs").scrollLeft() + jq("@tabs").width()));
	}
}
