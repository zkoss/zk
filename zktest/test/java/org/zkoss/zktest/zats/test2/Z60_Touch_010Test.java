/* Z60_Touch_010Test.java

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
import org.openqa.selenium.WebElement;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TouchWebDriverTestCase;

/**
 * @author rudyhuang
 */
@Category(ForkJVMTestOnly.class)
public class Z60_Touch_010Test extends TouchWebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();
		WebElement listbox = toElement(jq("@listbox"));
		System.out.println(">>> Z60_Touch_010Test before scroll");
		jq(".z-listbox-body").scrollTop(3000);
//		scroll(listbox, 0, 3000);
		System.out.println(">>> Z60_Touch_010Test after scroll");
		waitResponse(true);
		System.out.println(">>> after sleep");
		WebElement grid = toElement(jq("@grid"));
		jq(".z-grid-body").scrollTop(3000);
//		scroll(grid, 0, 3000);
		waitResponse(true);
		Assert.assertNotEquals(0, jq("@listbox .z-listbox-body").scrollTop());
		Assert.assertNotEquals(0, jq("@grid .z-grid-body").scrollTop());
	}
}
