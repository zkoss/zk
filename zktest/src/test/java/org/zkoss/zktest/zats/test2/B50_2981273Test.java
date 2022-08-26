package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B50_2981273Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String date = jq(jq("$db").toWidget().$n("real")).val();
		String year = date.substring(date.lastIndexOf(".") + 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		assertEquals(sdf.format(new Date()), year);
	}
}