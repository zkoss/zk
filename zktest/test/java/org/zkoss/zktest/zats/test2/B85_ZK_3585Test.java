/* B85_ZK_3585Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 19:04:30 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class B85_ZK_3585Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();

		Textbox city = desktop.queryAll("textbox").get(2).as(Textbox.class);
		String cityValue = city.getValue();
		desktop.queryAll("button").get(0).click();

		Assert.assertFalse("That should be changed.", cityValue.equals(city.getValue()));
	}
}
