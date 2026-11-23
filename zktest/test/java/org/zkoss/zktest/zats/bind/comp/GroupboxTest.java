/* GroupboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:17:11 CST 2021, Created by rudyhuang

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
public class GroupboxTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final OpenAgent groupbox = desktop.query("groupbox").as(OpenAgent.class);
		final Label openStatus = desktop.query("#openStatus").as(Label.class);

		groupbox.open(true);
		Assert.assertEquals("true", openStatus.getValue());
		groupbox.open(false);
		Assert.assertEquals("false", openStatus.getValue());
	}
}
