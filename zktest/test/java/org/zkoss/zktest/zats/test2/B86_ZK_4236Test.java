/* B86_ZK_4236Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 30 14:45:54 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B86_ZK_4236Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Label lblEach = desktop.query("#lblEach").as(Label.class);
		Assert.assertNotEquals("LAST_LABEL", lblEach.getValue());
	}
}
