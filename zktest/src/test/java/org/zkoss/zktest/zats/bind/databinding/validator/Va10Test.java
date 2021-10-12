/* Va10Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Datebox;

/**
 * @author jameschu
 */
public class Va10Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent startAgent = desktopAgent.query("#start");
		ComponentAgent endAgent = desktopAgent.query("#end");
		Datebox start = startAgent.as(Datebox.class);
		Datebox end = endAgent.as(Datebox.class);
		ComponentAgent okButtonAgent = desktopAgent.query("#okButton");
		Datebox resultStartDb = desktopAgent.query("#resultStartDb").as(Datebox.class);
		Datebox resultEndDb = desktopAgent.query("#resultEndDb").as(Datebox.class);

		startAgent.type("2011/11/02");
		endAgent.type("2011/11/03");
		//check input is correct
		assertEquals("2011/11/02", start.getText());
		assertEquals("2011/11/03", end.getText());
		okButtonAgent.click();
		SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = df1.parse("Wed Nov 02 00:00:00 CST 2011");
			endDate = df1.parse("Wed Nov 03 00:00:00 CST 2011");
		} catch (ParseException e) {
			fail(e.getMessage());
		}
		assertEquals(startDate, resultStartDb.getValue());
		assertEquals(endDate, resultEndDb.getValue());

		endAgent.type("2011/11/01");
		assertEquals("2011/11/01", endAgent.as(Datebox.class).getText());
		okButtonAgent.click();
		assertEquals(startDate, resultStartDb.getValue());
		assertEquals(endDate, resultEndDb.getValue());
	}
}
