/* B110_ZK_6133Test.java

        Purpose:

        Description:

        History:
                Mon Jul 20 12:30:42 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6133Test extends WebDriverTestCase {

	/**
	 * Initial render: the daterangebox root element carries BOTH its zclass
	 * and the user-supplied sclass, exactly like the sibling datebox control.
	 */
	@Test
	public void testInitialRenderCarriesSclass() {
		connect();
		waitResponse();

		// sanity control: if this fails, the harness/page is broken, not the bug
		assertTrue(jq("$db").hasClass("z-datebox"),
				"control: datebox root must carry its zclass");
		assertTrue(jq("$db").hasClass("qa-sclass-probe"),
				"control: datebox root must render the user sclass");

		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"daterangebox root must carry its zclass");
		assertTrue(jq("$drb").hasClass("qa-sclass-probe"),
				"daterangebox root must render the user sclass alongside the zclass");
	}

	/**
	 * Dynamic update: setSclass on the server at runtime replaces the old
	 * sclass with the new one on the root, and the zclass survives.
	 */
	@Test
	public void testSetSclassAtRuntime() {
		connect();
		waitResponse();

		// The old sclass must be on the root BEFORE the swap, otherwise the
		// assertFalse below is vacuous — setSclass has always gone through base
		// Widget#domClass_, so every assertion after the click already held with
		// the bug present.
		assertTrue(jq("$drb").hasClass("qa-sclass-probe"),
				"precondition: the initial render must carry the sclass being replaced");

		click(jq("$btnChange"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("qa-sclass-updated"),
				"daterangebox root must render the new sclass after setSclass");
		assertFalse(jq("$drb").hasClass("qa-sclass-probe"),
				"the old sclass must be removed after setSclass");
		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"the zclass must survive an sclass update");
	}

	/**
	 * sclass removal: setting sclass to null removes the custom class but
	 * keeps the zclass.
	 */
	@Test
	public void testClearSclassKeepsZclass() {
		connect();
		waitResponse();

		// Same reason as testSetSclassAtRuntime: without this the removal
		// assertion passes on a root that never rendered the class at all.
		assertTrue(jq("$drb").hasClass("qa-sclass-probe"),
				"precondition: the initial render must carry the sclass being removed");

		click(jq("$btnClear"));
		waitResponse();

		assertFalse(jq("$drb").hasClass("qa-sclass-probe"),
				"the custom sclass must be removed after setSclass(null)");
		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"the zclass must survive sclass removal");
	}

	/**
	 * State classes survive a runtime setSclass: setSclass resets the root
	 * className via updateDomClass_, so the imperatively-added state class
	 * (z-daterangebox-disabled) must be re-carried by domClass_ — otherwise a
	 * disabled daterangebox silently loses its disabled styling until the next
	 * state toggle. The reference datebox achieves this via
	 * zul.inp.InputWidget#domClass_.
	 */
	@Test
	public void testSetSclassPreservesDisabledStateClass() {
		connect();
		waitResponse();

		click(jq("$btnDisable"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-disabled"),
				"precondition: disabled daterangebox must carry its disabled state class");

		click(jq("$btnChange"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("z-daterangebox-disabled"),
				"the disabled state class must survive a runtime setSclass");
		assertTrue(jq("$drb").hasClass("qa-sclass-updated"),
				"the new sclass must be applied after setSclass");
		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"the zclass must survive an sclass update");
	}

	/**
	 * The readonly state class is likewise folded into domClass_, so it must
	 * survive a runtime setSclass — same contract as disabled.
	 */
	@Test
	public void testSetSclassPreservesReadonlyStateClass() {
		connect();
		waitResponse();

		click(jq("$btnReadonly"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-readonly"),
				"precondition: readonly daterangebox must carry its readonly state class");

		click(jq("$btnChange"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("z-daterangebox-readonly"),
				"the readonly state class must survive a runtime setSclass");
		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"the zclass must survive an sclass update");
	}

	/**
	 * The -invalid mark can be pushed by the server range-rejection path
	 * (snap-back AuInvoke) with parseable text in both inputs, so it cannot be
	 * re-derived from input text and must be re-carried from a durable field.
	 * "mark invalid" fires the same AuInvoke the server sends; a subsequent
	 * setSclass resets the root className, and the invalid mark must survive.
	 */
	@Test
	public void testSetSclassPreservesInvalidStateClass() {
		connect();
		waitResponse();

		click(jq("$btnMarkInvalid"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: a server-pushed invalid mark must be on the root");

		click(jq("$btnChange"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"the server-pushed invalid mark must survive a runtime setSclass");
		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"the zclass must survive an sclass update");
	}

	/**
	 * A server-pushed invalid mark must survive a rerender. setZclass triggers
	 * redraw + rebind on the client, and the mark is only reachable from the
	 * durable field — the inputs both hold parseable text, so re-deriving it
	 * from their contents on bind would silently drop it.
	 */
	@Test
	public void testInvalidStateSurvivesRerender() {
		connect();
		waitResponse();

		click(jq("$btnMarkInvalid"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: a server-pushed invalid mark must be on the root");

		click(jq("$btnChangeZclass"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("qa-zcls"),
				"precondition: the new zclass must be applied");
		assertTrue(jq("$drb").hasClass("qa-zcls-invalid"),
				"the server-pushed invalid mark must survive a rerender");
	}

	/**
	 * The text-derived half of the invalid mark must not be cached, or a
	 * server-pushed value that replaces the bad text leaves a red border that
	 * a later restyle re-emits from the stale field.
	 */
	@Test
	public void testInvalidMarkNotStaleAfterServerValuePush() {
		connect();
		waitResponse();

		type(jq(".z-daterangebox-begin"), "abc");
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: unparseable text must mark the root invalid");

		click(jq("$btnSetBegin"));
		waitResponse();
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"),
				"a server-pushed value replaces the bad text, so the mark must clear");

		click(jq("$btnChange"));
		waitResponse();
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"),
				"the mark must not be re-emitted from a stale field on restyle");
	}

	/**
	 * A value pushed from the server supersedes the server's own earlier
	 * rejection, so the mark must clear — the assertion is only clearable by the
	 * server, and this is one of the places it says so.
	 */
	@Test
	public void testInvalidMarkClearsWhenServerPushesValueOverRejection() {
		connect();
		waitResponse();

		click(jq("$btnMarkInvalid"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: a server-pushed rejection must mark the root invalid");

		click(jq("$btnSetBegin"));
		waitResponse();
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"),
				"a server-pushed value supersedes the rejection, so the mark must clear");
	}

	/**
	 * A locale change rewrites both input texts, so the per-side flags no longer
	 * describe what is on screen and the mark must be re-derived with them.
	 */
	@Test
	public void testInvalidMarkClearsAfterLocaleChange() {
		connect();
		waitResponse();

		type(jq(".z-daterangebox-begin"), "abc");
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: unparseable text must mark the root invalid");

		click(jq("$btnSetLocale"));
		waitResponse();
		assertFalse(jq("$drb").hasClass("z-daterangebox-invalid"),
				"the locale change cleared the bad text, so the mark must clear too");
	}

	/**
	 * The focus ring is applied imperatively on focus but must also be re-carried
	 * by domClass_, or a restyle triggered while the picker is open erases it
	 * under a still-focused input.
	 */
	@Test
	public void testFocusClassSurvivesSetSclass() {
		connect();
		waitResponse();

		// focusing opens the popup, whose onOpen listener calls setSclass on the
		// server — the className reset happens while focus is still held
		click(jq(".z-daterangebox-begin"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("qa-sclass-onopen"),
				"precondition: the onOpen listener must have restyled the box");
		assertTrue(jq("$drb").hasClass("z-daterangebox-focused"),
				"the focus ring must survive the className reset");
	}

	/**
	 * CSS actually applies: an sclass-scoped CSS rule visibly styles the
	 * daterangebox root, identically to the datebox control with the same
	 * sclass — this guards the real user intent (styling), not just the
	 * class attribute string.
	 */
	@Test
	public void testSclassScopedCssApplies() {
		connect();
		waitResponse();

		// Compare against the sibling datebox control rather than a literal
		// rgb string, so a driver that serializes as rgba(...) doesn't fail a
		// correct fix — the control anchors the expected serialization.
		String control = jq("$db").css("background-color");
		assertTrue(control.startsWith("rgb"),
				"control: sclass-scoped CSS rule must style the datebox root, was: " + control);
		assertEquals(control, jq("$drb").css("background-color"),
				"sclass-scoped CSS rule must style the daterangebox root like the datebox control");
	}
}
