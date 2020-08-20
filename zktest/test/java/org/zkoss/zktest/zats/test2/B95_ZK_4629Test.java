/* B95_ZK_4629Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Aug 19, 2020 03:42:38 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import sun.lwawt.macosx.CSystemTray;

/**
 * @author jameschu
 */
public class B95_ZK_4629Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertTrue(jq("iframe").contents().find("body").html().contains("HTTP ERROR 400"));
	}
}
