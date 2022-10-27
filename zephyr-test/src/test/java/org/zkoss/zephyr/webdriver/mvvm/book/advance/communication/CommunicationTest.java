/* CommunicationTest.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 12:48:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class CommunicationTest extends ClientBindTestCase {
	@Test
	public void composerToViewModelTest() {
		connect();

		click(jq("$btn1"));
		waitResponse();

		assertEquals("Send a global command in a composer\nreceived the global command in VM", getZKLog());
		assertEquals("onClick: cmd1", jq("$l2").text());
	}

	@Test
	public void viewModelToComposerTest() {
		connect("/mvvm/book/advance/communication/Communication-2.zul");

		click(jq("$btn2"));
		waitResponse();

		assertEquals("received the global command in a composer", getZKLog());

		assertEquals("onGlobalCommand: cmd2", jq("$l1").text());
	}
}
