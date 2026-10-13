/* B110_ZK_6186Test.java

        Purpose:

        Description:

        History:
                Tue Oct 13 10:41:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6186Test extends WebDriverTestCase {

	private boolean groupDisabled(String id) {
		return widget(id).is("disabled");
	}

	private boolean inputDisabled(String script) {
		return Boolean.parseBoolean(getEval(script + ".$n('real').disabled"));
	}

	private void assertRadios(boolean disabled, String... ids) {
		for (String id : ids)
			assertEquals(disabled, inputDisabled("zk.Widget.$('" + id + "')"),
					id + (disabled ? " must be disabled" : " must be enabled"));
	}

	// the browser must show each radio as the server has it, and the server must have it as expected
	private void assertSameAsServer(boolean disabled, String... ids) {
		click(jq("$check"));
		waitResponse();
		List<String> server = Arrays.asList(jq("$srv").text().trim().split("\\s+"));
		for (String id : ids) {
			boolean client = inputDisabled("zk.Widget.$('$" + id + "')");
			assertTrue(server.contains(id + "=" + client),
					id + " is " + (client ? "disabled" : "enabled") + " in the browser, the server has " + server);
			assertEquals(disabled, client, id + (disabled ? " must be disabled" : " must be enabled"));
		}
	}

	@Test
	public void testSetDisabledReachesTheWidget() {
		connect();
		assertFalse(groupDisabled("$rg"));

		click(jq("$disable"));
		waitResponse();
		assertTrue(groupDisabled("$rg"), "the radiogroup widget must report the server-side disabled state");
		assertRadios(true, "$cash", "$credit", "$ext");

		click(jq("$enable"));
		waitResponse();
		assertFalse(groupDisabled("$rg"), "the radiogroup widget must report the server-side enabled state");
		assertRadios(false, "$cash", "$credit", "$ext");
	}

	@Test
	public void testRadiosAddedLaterFollowTheGroup() {
		connect();
		click(jq("$disable"));
		waitResponse();

		click(jq("$add"));
		waitResponse();
		assertRadios(true, "$added");

		// a radio joining on the client takes the group's state from the widget (Radiogroup#_fixOnAdd)
		eval("zk.Widget.$('$rg').appendChild(window.clientRadio = new zul.wgt.Radio({label: 'Client'}))");
		assertTrue(inputDisabled("window.clientRadio"), "a radio added on the client must be disabled");

		click(jq("$enable"));
		waitResponse();
		assertRadios(false, "$added");
		assertFalse(inputDisabled("window.clientRadio"), "a radio added on the client must be enabled again");
	}

	@Test
	public void testInitialRenderCarriesDisabled() {
		connect();
		assertTrue(groupDisabled("$rg2"));
		assertRadios(true, "$initial");
	}

	// a group rendered disabled, enabled in the request that appends a radio: both radios end enabled
	@Test
	public void testUpdateAfterADisabledRender() {
		connect();
		click(jq("$enable2"));
		waitResponse();
		assertFalse(groupDisabled("$rg2"));
		assertSameAsServer(false, "initial", "late");
	}

	// the group's update must not overwrite the state of a radio attached in the same request
	@Test
	public void testRadioEnabledInTheSameRequestStaysEnabled() {
		connect();
		click(jq("$disableAddEnabled"));
		waitResponse();
		assertTrue(groupDisabled("$rg"));
		assertSameAsServer(false, "same");
		assertSameAsServer(true, "cash", "credit", "ext");
	}

	@Test
	public void testRadioDisabledInTheSameRequestStaysDisabled() {
		connect();
		click(jq("$disable"));
		waitResponse();
		click(jq("$enableAddDisabled"));
		waitResponse();
		assertFalse(groupDisabled("$rg"));
		assertSameAsServer(true, "locked");
		assertSameAsServer(false, "cash", "credit", "ext");
	}

	// joining a disabled group on the client must not override the state the server rendered
	@Test
	public void testRadioEnabledInALaterRequestStaysEnabled() {
		connect();
		click(jq("$disable"));
		waitResponse();
		click(jq("$addEnabled"));
		waitResponse();
		assertSameAsServer(false, "later");
	}

	@Test
	public void testNestedRadioMatchesTheServer() {
		connect();
		click(jq("$disable"));
		waitResponse();
		click(jq("$addNested"));
		waitResponse();
		assertSameAsServer(true, "nested");
	}

	@Test
	public void testRedrawnExternalRadioMatchesTheServer() {
		connect();
		click(jq("$disable"));
		waitResponse();
		click(jq("$redrawExt"));
		waitResponse();
		assertSameAsServer(false, "ext");
	}
}
