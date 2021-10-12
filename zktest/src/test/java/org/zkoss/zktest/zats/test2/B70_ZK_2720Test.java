/* B70_ZK_2720Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 01 16:27:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.endsWith;

import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@Category(ForkJVMTestOnly.class)
public class B70_ZK_2720Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B70_ZK_2720Test.class);

	@Test
	public void test() {
		connect();
		waitResponse();
		sleep(15000);
		MatcherAssert.assertThat(driver.getCurrentUrl(), endsWith("timeout.zul"));
	}
}
