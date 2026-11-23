package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jameschu
 */
public class B50_2981273Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		String date = jq(jq("$db").toWidget().$n("real")).val();
		String year = date.substring(date.lastIndexOf(".") + 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		assertEquals(sdf.format(new Date()), year);
	}
}