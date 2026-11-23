/* B85_ZK_3713Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 09 10:51:04 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B85_ZK_3713Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		List<ComponentAgent> lbls = desktop.queryAll("hlayout label");

		Assert.assertEquals("result bean[fieldVariable]: ", lbls.get(0).as(Label.class).getValue());
		Assert.assertEquals("value1", lbls.get(1).as(Label.class).getValue());
	}
}
