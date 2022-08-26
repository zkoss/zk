/* Z30_listbox_0007Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 11:03:03 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import com.google.common.collect.Streams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Z30_listbox_0007Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assertions.assertEquals(
				Streams.stream(jq(".z-listitem")).limit(4).mapToDouble(JQuery::outerHeight).sum(),
				jq(".z-listbox-body").height(),
				3);
	}
}
