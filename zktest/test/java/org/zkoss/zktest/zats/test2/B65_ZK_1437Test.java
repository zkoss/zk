package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
		assertTrue("window3's title was changed to current time", current.after(past));
		assertTrue("should not see any error message.", !jq(".z-errorbox").exists());
    }
}
