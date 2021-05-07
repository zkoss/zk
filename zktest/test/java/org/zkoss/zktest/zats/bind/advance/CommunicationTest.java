/* CommunicationTest.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 12:48:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class CommunicationTest extends ZATSTestCase {
	@Test
	public void composerToViewModelTest() {
		DesktopAgent desktop = connect();

		desktop.query("#btn1").click();

		Assert.assertArrayEquals(
			new String[] {
				"Send a global command in a composer",
				"received the global command in VM"
			},
			desktop.getZkLog().toArray()
		);

		Assert.assertEquals("onClick: cmd1", desktop.query("#l2").as(Label.class).getValue());
	}

	@Test
	public void viewModelToComposerTest() {
		DesktopAgent desktop = connect("/bind/advance/Communication-2.zul");

		desktop.query("#btn2").click();

		Assert.assertArrayEquals(
			new String[] {
				"received the global command in a composer"
			},
			desktop.getZkLog().toArray()
		);

		Assert.assertEquals("onGlobalCommand: cmd2", desktop.query("#l1").as(Label.class).getValue());
	}
}
