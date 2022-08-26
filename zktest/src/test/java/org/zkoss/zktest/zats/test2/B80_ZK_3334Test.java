package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3334Test extends WebDriverTestCase {
	private WebDriver.Window window;

	@Test
	public void test() {
		connect();
		window = driver.manage().window();
		click(jq(".z-bandbox-button"));
		waitResponse();
		verifyPopup(jq("@bandbox"), jq(".z-bandbox-popup"));

		click(jq(".z-combobox-button"));
		waitResponse();
		verifyPopup(jq("@combobox"), jq(".z-combobox-popup"));

		click(jq(".z-chosenbox-input"));
		waitResponse();
		verifyPopup(jq("@chosenbox"), jq(".z-chosenbox-popup"));
		click(jq("body"));
		waitResponse();

		click(jq(".z-datebox-button"));
		waitResponse();
		verifyPopup(jq("@datebox"), jq(".z-datebox-popup"));

		click(jq(".z-menu"));
		waitResponse();
		verifyPopup(jq("@menu"), jq(".z-menupopup"));

		JQuery com = jq("@textbox");
		focus(com);
		blur(com);
		waitResponse();
		JQuery pp = jq(".z-errorbox");
		assertEquals(com.offsetLeft() + com.outerWidth(), pp.offsetLeft(), 1);

		window.setSize(new Dimension(window.getSize().width - 100, window.getSize().height));
		waitResponse();
		assertEquals(com.offsetLeft() + com.outerWidth(), pp.offsetLeft(), 1);
	}

	public void verifyPopup(JQuery com, JQuery pp) {
		Dimension size = window.getSize();
		assertEquals(com.offsetLeft(), pp.offsetLeft(), 1);
		window.setSize(new Dimension(size.width - 100, size.height));
		waitResponse();
		assertEquals(com.offsetLeft(), pp.offsetLeft(), 1);
		window.setSize(new Dimension(size.width, size.height));
		waitResponse();
	}
}
