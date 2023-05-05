/* Z60_Touch_011Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 18:20:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TouchWebDriverTestCase;

/**
 * @author rudyhuang
 */
@Category(ForkJVMTestOnly.class)
public class Z60_Touch_011Test extends TouchWebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();

		scroll(toElement(jq("@listbox")), 0, 3000);
		waitResponse();
		scroll(toElement(jq("@grid")), 0, 3000);
		waitResponse();
		Assert.assertNotEquals(0, jq("@listbox .z-listbox-body").scrollTop());
		Assert.assertNotEquals(0, jq("@grid .z-grid-body").scrollTop());
	}
}
