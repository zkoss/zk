package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B65_ZK_1437Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		SimpleDateFormat fmt = new SimpleDateFormat("'Hello World!!' EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		JQuery header = jq(jq(".z-window-embedded:eq(2)").toWidget().$n("cap"));
		Date past;
		try {
			past = fmt.parse(header.text());
		} catch (ParseException e) {
			fail();
			return;
		}
		// it verify sec is diff
		sleep(1000);
		click(jq(".z-button:contains(reload)"));
		waitResponse();
		Date current;
		try {
			current = fmt.parse(header.text());
		} catch (ParseException e) {
			fail();
			return;
		}
		assertTrue(current.after(past), "window3's title was changed to current time");
		assertTrue(!jq(".z-errorbox").exists(), "should not see any error message.");
    }
}
