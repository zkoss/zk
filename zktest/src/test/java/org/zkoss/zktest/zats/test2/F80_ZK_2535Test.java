/** F80_ZK_2535Test.java.

 Purpose:

 Description:

 History:
 	Mon May 30 15:14:22 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 *
 */
public class F80_ZK_2535Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		// this case cannot work with A11Y as the fixed ZK-5213: Improve accessibility by add tooltips on icons
		if (!Boolean.valueOf(getEval("!!window.za11y"))) {

			for (int i = 1; i <= 2; i++) {
				JQuery p = jq("$p" + i);

				JQuery maximize = p.find(".z-panel-maximize");
				assertEquals("Maximize", maximize.attr("title"));

				click(maximize);
				waitResponse();
				assertEquals("Restore", maximize.attr("title"));

				JQuery expand = p.find(".z-panel-expand");
				assertEquals("Collapse", expand.attr("title"));

				click(expand);
				waitResponse();
				assertEquals("Expand", expand.attr("title"));
				assertEquals(false, p.find(".z-panel-body").isVisible());

				JQuery close = p.find(".z-panel-close");
				assertEquals("Close", close.attr("title"));
				click(close);
				waitResponse();
				assertEquals(0, jq("$p" + i).length());
			}

			for (int i = 1; i <= 2; i++) {
				JQuery w = jq("$w" + i);

				JQuery maximize = w.find(".z-window-maximize");
				assertEquals("Maximize", maximize.attr("title"));

				click(maximize);
				waitResponse();
				assertEquals("Restore", maximize.attr("title"));

				JQuery close = w.find(".z-window-close");
				assertEquals("Close", close.attr("title"));
				click(close);
				waitResponse();
				assertEquals(0, jq("$w" + i).length());
			}
		}
	}
}