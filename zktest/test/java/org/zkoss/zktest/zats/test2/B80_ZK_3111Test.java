/* B80_ZK_3111Test.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 02/01/16, Created by christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author christopher
 */
public class B80_ZK_3111Test extends WebDriverTestCase {

	private List<String> locales = Arrays.asList(
			new String[] { "ar", "bg", "ca", "cs", "da", "de", "es", "fr", "hu",
					"id", "it", "ja", "ko", "nl", "pl", "pt", "pt_BR", "ro",
					"ru", "sk", "sl", "sv", "tr", "uk", "vi", "zh", "zh_CN",
					"zh_SG", "zh_TW" });

	@Test
	public void test() {
		connect();

		//check init label, should be english
		Iterator<JQuery> btns = jq(".z-window-close, .z-panel-close").iterator();
		while (btns.hasNext()) {
			JQuery btn = btns.next();
			assertTrue(btn.attr("title").equals("Close"));
		}

		for (String locale : locales) {
			//switch locale
			JQuery textbox = jq("@textbox");
			type(textbox, locale);
			waitResponse(true);
			click(jq("button").eq(0));
			waitResponse(true);

			//check label again, should be localized label
			btns = jq(".z-window-close, .z-panel-close").iterator();
			while (btns.hasNext()) {
				JQuery btn = btns.next();
				assertFalse(btn.attr("title").contains("27112700"));
			}
		}
	}
}
