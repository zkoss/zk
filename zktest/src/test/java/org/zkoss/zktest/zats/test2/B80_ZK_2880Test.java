package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B80_ZK_2880Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//click the button
		click(jq("button"));
		waitResponse();
		//check there are no comboitems outside combobox
		//selector: select all comboitems that are NOT direct child of z-combobox-content (the combobox popup);
		assertFalse(jq(".z-comboitem:not(.z-combobox-content > .z-comboitem)").exists());
	}
}
