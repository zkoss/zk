package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_5433Test extends WebDriverTestCase {

	// Chrome's headless mode does not support resizing the browser, so the size
	// has to be given when the browser is launched.
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		options.addArguments(new String[]{"window-size=820,1080"});
		return options;
	}
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery tab15 = jq("@tab").eq(14);
		//in view
		System.out.println("tab15 offsetLeft: " + tab15.toElement().get("offsetLeft"));
		System.out.println("tabs rightmost: " + (jq("@tabs").scrollLeft() + jq("@tabs").width()));
		assertTrue(parseInt(tab15.toElement().get("offsetLeft")) < (jq("@tabs").scrollLeft() + jq("@tabs").width()));
	}
}
