/* B70_ZK_2649Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 12:25:12 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2649Test extends WebDriverTestCase {
	@Override
	protected String getFileExtension() {
		return ".zhtml";
	}

	@Test
	public void test() {
		connect();
		sleep(1000);

		Assert.assertEquals(
				"If you can see this, everything runs well.",
				trim(jq("@label").text()));
	}
}
