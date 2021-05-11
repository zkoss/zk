/* B96_ZK_4893Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 11 15:56:41 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
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
public class B96_ZK_4893Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		Assert.assertEquals("true", desktop.query("#last3").as(Label.class).getValue());
	}
}
