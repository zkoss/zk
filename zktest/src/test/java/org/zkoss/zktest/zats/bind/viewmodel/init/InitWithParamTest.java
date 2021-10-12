/* InitWithParamTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 15:18:04 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.init;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class InitWithParamTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init-with-param.zul");
		desktop.query("button").click();
		Assert.assertEquals("test test2", desktop.getZkLog().get(0));
	}
}
