package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.*;

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