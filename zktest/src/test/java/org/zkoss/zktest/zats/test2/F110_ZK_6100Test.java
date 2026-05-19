/* F110_ZK_6100Test.java

		Purpose:

		Description:

		History:
				Tue May 19 13:17:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6100Test extends WebDriverTestCase {

	private static final String[] PLAIN_IDS = {
		"t_textbox", "t_textarea", "t_intbox", "t_longbox", "t_doublebox", "t_decimalbox"
	};

	private static final String[] COMPOSITE_IDS = {
		"t_combobox", "t_bandbox", "t_datebox", "t_timebox", "t_spinner", "t_doublespinner", "t_timepicker"
	};

	private static final String FIELD = "[id$=-real]";

	private double rect(String id, String part, String side) {
		return Double.parseDouble(getEval(
			"jq('$" + id + "').find('" + part + "')[0].getBoundingClientRect()." + side));
	}

	private String[] allIds() {
		String[] ids = new String[PLAIN_IDS.length + COMPOSITE_IDS.length];
		System.arraycopy(PLAIN_IDS, 0, ids, 0, PLAIN_IDS.length);
		System.arraycopy(COMPOSITE_IDS, 0, ids, PLAIN_IDS.length, COMPOSITE_IDS.length);
		return ids;
	}

	@Test
	public void everyInputRendersLabelInsideRoot() {
		connect();
		waitResponse();
		for (String id : allIds()) {
			assertTrue(jq("$" + id).hasClass("z-input-label-left"), id);
			assertEquals(1, jq("$" + id).find(".z-input-label-text").length(), id);
		}
	}

	@Test
	public void labelForLinkage() {
		connect();
		waitResponse();
		for (String id : allIds())
			assertEquals(jq("$" + id).find(FIELD).attr("id"),
				jq("$" + id).find(".z-input-label-text").attr("for"), id);
	}

	@Test
	public void labelSitsLeftOfField() {
		connect();
		waitResponse();
		for (String id : allIds())
			assertTrue(rect(id, ".z-input-label-text", "right") <= rect(id, FIELD, "left"), id);
	}

	@Test
	public void buttonStaysOnField() {
		connect();
		waitResponse();
		for (String id : COMPOSITE_IDS) {
			assertEquals(rect(id, FIELD, "top"), rect(id, "[id$=-btn]", "top"), 1, id);
			assertEquals(rect(id, FIELD, "right"), rect(id, "[id$=-btn]", "right"), 1, id);
		}
	}

	@Test
	public void unlabelledPlainInputMarkupUnchanged() {
		connect();
		waitResponse();
		assertEquals("INPUT", getEval("zk.$('$t_nolabel').$n().tagName"));
		assertEquals("true", getEval("!zk.$('$t_nolabel').$n('real')"));
		assertEquals("z-textbox", jq("$t_nolabel").attr("class"));
	}

	@Test
	public void emptyLabelNotRendered() {
		connect();
		waitResponse();
		assertFalse(jq("$t_unset").hasClass("z-input-label"));
		assertFalse(jq("$t_unset").find(".z-input-label-text").exists());
	}

	@Test
	public void sclassOnRootZclassOnField() {
		connect();
		waitResponse();
		assertTrue(jq("$t_sclass").hasClass("custom"));
		assertFalse(jq("$t_sclass").hasClass("z-textbox"));
		assertTrue(jq("$t_sclass").find(FIELD).hasClass("z-textbox"));
		assertFalse(jq("$t_sclass").find(FIELD).hasClass("custom"));
	}

	@Test
	public void stateClassesStayWithZclass() {
		connect();
		waitResponse();
		assertTrue(jq("$t_tdisabled").find(FIELD).is(":disabled"));
		assertTrue(jq("$t_tdisabled").find(FIELD).hasClass("z-textbox-disabled"));
		assertTrue(jq("$t_treadonly").find(FIELD).hasClass("z-textbox-readonly"));
		assertTrue(jq("$t_disabled").hasClass("z-combobox-disabled"));
		assertTrue(jq("$t_readonly").hasClass("z-combobox-readonly"));
	}

	@Test
	public void sclassChangeKeepsFieldClass() {
		connect();
		waitResponse();
		click(jq("$btn_sclass"));
		waitResponse();
		assertTrue(jq("$t_tprefilled").hasClass("changed"));
		assertTrue(jq("$t_tprefilled").hasClass("z-input-label-left"));
		assertTrue(jq("$t_tprefilled").find(FIELD).hasClass("z-textbox"));
	}

	@Test
	public void leftHasNoFloatingState() {
		connect();
		waitResponse();
		click(jq("$t_textbox").find(FIELD));
		waitResponse();
		assertFalse(jq("$t_textbox").hasClass("z-input-label-shrunk"));
	}

	@Test
	public void invalidErrorboxAppears() {
		connect();
		waitResponse();
		click(jq("$t_tinvalid").find(FIELD));
		waitResponse();
		click(jq("$t_textbox").find(FIELD));
		waitResponse();
		assertTrue(jq(".z-errorbox").exists());
		assertTrue(jq("$t_tinvalid").find(FIELD).hasClass("z-textbox-invalid"));
	}

	@Test
	public void xssEscape() {
		connect();
		waitResponse();
		assertEquals("<script>alert(1)</script>", jq("$t_xss").find(".z-input-label-text").text());
		assertFalse(jq("$t_xss").find(".z-input-label-text script").exists());
	}

	@Test
	public void rtlLabelOnRight() {
		connect();
		waitResponse();
		for (String id : new String[] {"t_rtl", "t_trtl"})
			assertTrue(rect(id, ".z-input-label-text", "left") >= rect(id, FIELD, "right"), id);
	}

	@Test
	public void serverSetLabel() {
		connect();
		waitResponse();
		click(jq("$btn_setLabel"));
		waitResponse();
		assertEquals("Updated", jq("$t_dynamic").find(".z-input-label-text").text());
		assertEquals("Updated", jq("$t_tdynamic").find(".z-input-label-text").text());
	}

	@Test
	public void serverSetLabelNullRestoresPlainMarkup() {
		connect();
		waitResponse();
		click(jq("$btn_unsetLabel"));
		waitResponse();
		assertFalse(jq("$t_dynamic").find(".z-input-label-text").exists());
		assertEquals("INPUT", getEval("zk.$('$t_tdynamic').$n().tagName"));
		assertTrue(jq("$t_tdynamic").hasClass("z-textbox"));
	}

	@Test
	public void serverSetPositionLeftToTop() {
		connect();
		waitResponse();
		click(jq("$btn_setPosTop"));
		waitResponse();
		for (String id : new String[] {"t_dynamic", "t_tdynamic"}) {
			assertTrue(jq("$" + id).hasClass("z-input-label-top"), id);
			assertTrue(rect(id, ".z-input-label-text", "bottom") <= rect(id, FIELD, "top"), id);
		}
	}

	@Test
	public void serverSetPositionLeftToFloating() {
		connect();
		waitResponse();
		click(jq("$btn_setPosFloating"));
		waitResponse();
		assertTrue(jq("$t_dynamic").hasClass("z-input-label-floating"));
		assertTrue(jq("$t_tdynamic").hasClass("z-input-label-floating"));
	}
}
