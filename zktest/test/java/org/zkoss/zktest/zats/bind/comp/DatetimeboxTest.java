/* DatetimeboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:38:22 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Timebox;

/**
 * @author rudyhuang
 */
public class DatetimeboxTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final Datebox datebox = desktop.query("datebox").as(Datebox.class);
		final Timebox timebox = desktop.query("timebox").as(Timebox.class);
		final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

		desktop.query("timebox").type("AM 11:11:11");
		Assert.assertEquals("11:11:11 AM", timeFormat.format(datebox.getValue()));

		desktop.query("datebox").type("2021/01/23 PM 06:05:04");
		Assert.assertEquals("06:05:04 PM", timeFormat.format(timebox.getValue()));
	}
}
