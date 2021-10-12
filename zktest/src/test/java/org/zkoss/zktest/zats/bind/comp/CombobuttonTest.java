/* CombobuttonTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:21:35 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class CombobuttonTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final OpenAgent combobutton = desktop.query("combobutton").as(OpenAgent.class);
		final Label open = desktop.query("#open").as(Label.class);
		combobutton.open(true);
		Assert.assertEquals("true", open.getValue());

		combobutton.open(false);
		Assert.assertEquals("false", open.getValue());
	}
}
