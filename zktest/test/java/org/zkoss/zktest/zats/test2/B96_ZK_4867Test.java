/* B96_ZK_4867Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 21 12:00:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_4867Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery listbox1 = jq("@listbox").eq(0);
		JQuery listbox2 = jq("@listbox").eq(1);
		for (int i = 0; i < 3; i++)
			assertEquals(listbox1.find("@listheader").eq(i).outerWidth(), listbox2.find("@listheader").eq(i).outerWidth(), 1);
	}
}
