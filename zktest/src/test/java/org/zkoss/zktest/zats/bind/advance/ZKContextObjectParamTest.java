/* ZKContextObjectParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 17:07:21 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class ZKContextObjectParamTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Label bindContextString = desktop.query("#bindContextString").as(Label.class);
		Label binderString = desktop.query("#binderString").as(Label.class);
		Label eventName = desktop.query("#eventName").as(Label.class);
		Label cmdName = desktop.query("#cmdName").as(Label.class);
		Label executionString = desktop.query("#executionString").as(Label.class);
		Label idSpaceString = desktop.query("#idSpaceString").as(Label.class);
		Label viewString = desktop.query("#bindViewString").as(Label.class);
		Label componentString = desktop.query("#bindComponentString").as(Label.class);
		Label pageString = desktop.query("#pageString").as(Label.class);
		Label desktopString = desktop.query("#desktopString").as(Label.class);
		Label sessionString = desktop.query("#sessionString").as(Label.class);
		Label webAppName = desktop.query("#webAppName").as(Label.class);

		Assert.assertNotEquals("", bindContextString.getValue());
		Assert.assertNotEquals("", binderString.getValue());
		Assert.assertEquals("null", eventName.getValue());
		Assert.assertEquals("null", cmdName.getValue());
		Assert.assertNotEquals("", executionString.getValue());
		Assert.assertNotEquals("", idSpaceString.getValue());
		Assert.assertNotEquals("", viewString.getValue());
		Assert.assertNotEquals("", componentString.getValue());
		Assert.assertNotEquals("", pageString.getValue());
		Assert.assertNotEquals("", desktopString.getValue());
		Assert.assertNotEquals("", sessionString.getValue());
		Assert.assertNotEquals("", webAppName.getValue());

		desktop.query("button").click();

		Assert.assertNotEquals("", bindContextString.getValue());
		Assert.assertNotEquals("", binderString.getValue());
		Assert.assertEquals("onClick", eventName.getValue());
		Assert.assertEquals("show", cmdName.getValue());
		Assert.assertNotEquals("", executionString.getValue());
		Assert.assertNotEquals("", idSpaceString.getValue());
		Assert.assertNotEquals("", viewString.getValue());
		Assert.assertNotEquals("", componentString.getValue());
		Assert.assertNotEquals("", pageString.getValue());
		Assert.assertNotEquals("", desktopString.getValue());
		Assert.assertNotEquals("", sessionString.getValue());
		Assert.assertNotEquals("", webAppName.getValue());
	}
}
