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
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6133Test extends WebDriverTestCase {

	/** The root renders the user sclass alongside its zclass, like the datebox control. */
	@Test
	public void testInitialRenderCarriesSclass() {
		connect();
		waitResponse();

		assertTrue(jq("$db").hasClass("z-datebox"),
				"control: datebox root must carry its zclass");
		assertTrue(jq("$db").hasClass("qa-sclass-probe"),
				"control: datebox root must render the user sclass");

		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"daterangebox root must carry its zclass");
		assertTrue(jq("$drb").hasClass("qa-sclass-probe"),
				"daterangebox root must render the user sclass alongside the zclass");
	}

	/** A runtime setSclass replaces the old sclass and keeps the zclass. */
	@Test
	public void testSetSclassAtRuntime() {
		connect();
		waitResponse();

		// without this precondition the assertFalse below is vacuous
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

	/** setSclass(null) removes the custom class but keeps the zclass. */
	@Test
	public void testClearSclassKeepsZclass() {
		connect();
		waitResponse();

		// without this the removal assertion passes on a root that never had the class
		assertTrue(jq("$drb").hasClass("qa-sclass-probe"),
				"precondition: the initial render must carry the sclass being removed");

		click(jq("$btnClear"));
		waitResponse();

		assertFalse(jq("$drb").hasClass("qa-sclass-probe"),
				"the custom sclass must be removed after setSclass(null)");
		assertTrue(jq("$drb").hasClass("z-daterangebox"),
				"the zclass must survive sclass removal");
	}

	/** The sclass-scoped CSS rule visibly styles the root, like the datebox control. */
	@Test
	public void testSclassScopedCssApplies() {
		connect();
		waitResponse();

		// compared against the datebox, not a literal rgb: drivers may serialize rgba()
		String control = jq("$db").css("background-color");
		assertTrue(control.startsWith("rgb"),
				"control: sclass-scoped CSS rule must style the datebox root, was: " + control);
		assertEquals(control, jq("$drb").css("background-color"),
				"sclass-scoped CSS rule must style the daterangebox root like the datebox control");
	}

	/** The readonly modifier must survive setSclass's wholesale className rewrite. */
	@Test
	public void testSetSclassKeepsReadonlyMark() {
		connect();
		waitResponse();

		click(jq("$btnReadonly"));
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-readonly"),
				"precondition: setReadonly must mark the root");

		click(jq("$btnChange"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("qa-sclass-updated"),
				"precondition: the setSclass must have gone through");
		assertTrue(jq("$drb").hasClass("z-daterangebox-readonly"),
				"the readonly modifier must survive a setSclass className rewrite");
	}

	/** Same for the invalid mark, which has no server state to repaint it. */
	@Test
	public void testSetSclassKeepsInvalidMark() {
		connect();
		waitResponse();

		type(jq(".z-daterangebox-begin"), "not-a-date");
		waitResponse();
		// Focusing an input opens the popup, which would cover the buttons below.
		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"precondition: unparseable text must mark the root invalid");

		click(jq("$btnChange"));
		waitResponse();

		assertTrue(jq("$drb").hasClass("qa-sclass-updated"),
				"precondition: the setSclass must have gone through");
		assertTrue(jq("$drb").hasClass("z-daterangebox-invalid"),
				"the invalid mark must survive a setSclass className rewrite");
	}
}
