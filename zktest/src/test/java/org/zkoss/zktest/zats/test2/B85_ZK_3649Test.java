package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * @author bob peng
 */
public class B85_ZK_3649Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-spinner-icon.z-spinner-up"));
		waitResponse();
		assertNotSame("Should not be empty.", "", jq(".z-textbox:eq(0)").val());

		click(jq(".z-timebox-icon.z-timebox-up"));
		waitResponse();
		assertNotSame("Should not be empty.", "", jq(".z-textbox:eq(1)").val());

		click(jq(".z-doublespinner-icon.z-doublespinner-up"));
		waitResponse();
		assertNotSame("Should not be empty.", "", jq(".z-textbox:eq(2)").val());
	}
}