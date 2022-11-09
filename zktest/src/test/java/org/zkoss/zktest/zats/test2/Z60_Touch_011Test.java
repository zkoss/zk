/* Z60_Touch_011Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 23 18:20:00 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.TouchWebDriverTestCase;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class Z60_Touch_011Test extends TouchWebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();

		scroll(toElement(jq("@listbox")), 0, 3000);
		waitResponse();
		scroll(toElement(jq("@grid")), 0, 3000);
		waitResponse();
		assertNotEquals(0, jq("@listbox .z-listbox-body").scrollTop());
		assertNotEquals(0, jq("@grid .z-grid-body").scrollTop());
	}
}
