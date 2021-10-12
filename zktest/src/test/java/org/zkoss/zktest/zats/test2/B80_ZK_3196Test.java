/** B80_ZK_3196Test.java.

	Purpose:
		
	Description:
		
	History:
		Tue May 03 16:10:02 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3196Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		selectComboitem(jq("@combobox").toWidget(), 0);
		waitResponse();
		click(jq("$show"));
		waitResponse();
		JQuery proxyVal = jq("$p_val");
		JQuery originVal = jq("$o_val");
		assertEquals("B80_ZK_3196Object [name=Marie]", proxyVal.text());
		assertEquals("B80_ZK_3196Object [name=Paul]", originVal.text());
		click(jq("$save"));
		waitResponse();
		assertEquals("B80_ZK_3196Object [name=Marie]", proxyVal.text());
		assertEquals("B80_ZK_3196Object [name=Marie]", originVal.text());
	}
}
