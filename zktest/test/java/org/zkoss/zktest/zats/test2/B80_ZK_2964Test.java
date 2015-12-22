/* B80_ZK_2964Test.java

	Purpose:
		
	Description:
		
	History:
		10:33 AM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author jumperchen
 */
public class B80_ZK_2964Test extends WebDriverTestCase {
	private SimpleDateFormat sdf0 = new SimpleDateFormat("h:mm:ss a");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");
	@Test public void testZK2964() throws ParseException {
		connect();
		JQuery dateboxes = jq("@datebox");
		testTimeformat(widget(dateboxes.get(0)), sdf0);
		testTimeformat(widget(dateboxes.get(1)), sdf0);
		testTimeformat(widget(dateboxes.get(2)), sdf0);
		testTimeformat(widget(dateboxes.get(3)), sdf1);

	}

	private void testTimeformat(Widget widget, SimpleDateFormat sdf)
			throws ParseException {
		click(widget.$n("btn"));
		assertTrue(jq(widget.$n("pp")).find(".z-timebox-input").exists());
		sdf.parse(jq(widget.$n("pp")).find(".z-timebox-input").val());
	}
}
