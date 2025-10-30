/* B80_ZK_3342Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 1 14:14:32 CST 2016, Created by jameschu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;

/**
 * 
 * @author jameschu
 */
public class B80_ZK_3342Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
		String today = jq(".z-calendar-title").eq(1).text();
		assertEquals("Today", today);
		click(jq(".z-datebox-button"));
		waitResponse();
		today = jq(".z-calendar-title").eq(3).text();
		Date tDate = new Date();
		final DateFormat df = new SimpleDateFormat(jq("@datebox").toWidget().eval("getDateFormat()"), Locales.getCurrent());
		df.setTimeZone(TimeZones.getCurrent());
		assertEquals(df.format(tDate).toString(), today);
    }
}
