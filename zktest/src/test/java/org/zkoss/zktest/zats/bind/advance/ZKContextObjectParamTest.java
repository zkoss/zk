/* ZKContextObjectParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 17:07:21 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

		Assertions.assertNotEquals("", bindContextString.getValue());
		Assertions.assertNotEquals("", binderString.getValue());
		Assertions.assertEquals("null", eventName.getValue());
		Assertions.assertEquals("null", cmdName.getValue());
		Assertions.assertNotEquals("", executionString.getValue());
		Assertions.assertNotEquals("", idSpaceString.getValue());
		Assertions.assertNotEquals("", viewString.getValue());
		Assertions.assertNotEquals("", componentString.getValue());
		Assertions.assertNotEquals("", pageString.getValue());
		Assertions.assertNotEquals("", desktopString.getValue());
		Assertions.assertNotEquals("", sessionString.getValue());
		Assertions.assertNotEquals("", webAppName.getValue());

		desktop.query("button").click();

		Assertions.assertNotEquals("", bindContextString.getValue());
		Assertions.assertNotEquals("", binderString.getValue());
		Assertions.assertEquals("onClick", eventName.getValue());
		Assertions.assertEquals("show", cmdName.getValue());
		Assertions.assertNotEquals("", executionString.getValue());
		Assertions.assertNotEquals("", idSpaceString.getValue());
		Assertions.assertNotEquals("", viewString.getValue());
		Assertions.assertNotEquals("", componentString.getValue());
		Assertions.assertNotEquals("", pageString.getValue());
		Assertions.assertNotEquals("", desktopString.getValue());
		Assertions.assertNotEquals("", sessionString.getValue());
		Assertions.assertNotEquals("", webAppName.getValue());
	}
}
