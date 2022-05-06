/* B96_ZK_5095Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 14:45:46 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5095Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$serializeBtn"));
		waitResponse();
		assertFalse(jq("span:contains(java.io.NotSerializableException)").exists());
	}
}