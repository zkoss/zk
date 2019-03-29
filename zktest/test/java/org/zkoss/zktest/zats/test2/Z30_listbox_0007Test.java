/* Z30_listbox_0007Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 11:03:03 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import com.google.common.collect.Streams;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class Z30_listbox_0007Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertEquals(
				Streams.stream(jq(".z-listitem")).limit(4).mapToInt(JQuery::outerHeight).sum(),
				jq(".z-listbox-body").height(),
				2);
	}
}
