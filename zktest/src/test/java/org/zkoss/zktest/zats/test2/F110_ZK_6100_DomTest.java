/* F110_ZK_6100_DomTest.java

		Purpose:

		Description:

		History:
				Tue Oct 13 16:30:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6100_DomTest extends WebDriverTestCase {

	private static final String FIELD = "[id$=-real]";

	private void open() {
		connect("/test2/F110-ZK-6100-dom.zul");
		waitResponse();
	}

	private double field(String id, String side) {
		return Double.parseDouble(getEval(
			"jq('$" + id + "').find('" + FIELD + "')[0].getBoundingClientRect()." + side));
	}

	private double popup(String id, String side) {
		return Double.parseDouble(getEval("zk.$('$" + id + "').$n('pp').getBoundingClientRect()." + side));
	}

	@Test
	public void hiddenAtRenderHidesLabel() {
		open();
		for (String id : new String[] {"t_hidden", "t_thidden"}) {
			assertTrue(jq("$" + id).find(".z-input-label-text").exists(), id);
			assertFalse(jq("$" + id).find(".z-input-label-text").isVisible(), id);
		}
	}

	@Test
	public void setVisibleTogglesLabel() {
		open();
		click(jq("$btn_hide"));
		waitResponse();
		for (String id : new String[] {"t_toggle", "t_ttoggle"})
			assertFalse(jq("$" + id).find(".z-input-label-text").isVisible(), id);
		click(jq("$btn_show"));
		waitResponse();
		for (String id : new String[] {"t_toggle", "t_ttoggle"})
			assertTrue(jq("$" + id).find(".z-input-label-text").isVisible(), id);
	}

	@Test
	public void insertBeforeLandsOutsideComponent() {
		open();
		click(jq("$btn_insert"));
		waitResponse();
		assertTrue(jq("$box_insert").children(".ins").exists());
		assertTrue(jq(".ins").next().hasClass("z-combobox"));
		assertTrue(jq("$box_insert").children(".tins").exists());
		assertTrue(jq(".tins").next().hasClass("z-input-label"));
		click(jq("$btn_unlabel"));
		waitResponse();
		assertTrue(jq(".ins").exists());
		assertTrue(jq(".tins").exists());
		assertFalse(jq("$box_insert").find(".z-input-label-text").exists());
	}

	@Test
	public void insertBeforeNativeSiblingLandsOutsideComponent() {
		open();
		click(jq("$btn_nat"));
		waitResponse();
		assertTrue(jq("$box_nat").children(".ins-nat").exists());
		assertTrue(jq(".ins-nat").prev().hasClass("z-combobox"));
		assertTrue(jq("$box_tnat").children(".tins-nat").exists());
		assertTrue(jq(".tins-nat").prev().hasClass("z-input-label"));
	}

	@Test
	public void insertBeforeDomlessSiblingLandsOutsideComponent() {
		open();
		click(jq("$btn_tim"));
		waitResponse();
		assertTrue(jq("$box_tim").children(".ins-tim").exists());
		assertTrue(jq(".ins-tim").next().hasClass("z-combobox"));
		assertTrue(jq("$box_ttim").children(".tins-tim").exists());
		assertTrue(jq(".tins-tim").next().hasClass("z-input-label"));
	}

	@Test
	public void insertIntoInputgroupLandsOutsideComponent() {
		open();
		click(jq("$btn_ig"));
		waitResponse();
		assertTrue(jq("$ig").children(".z-inputgroup-text").find(".ins-ig").exists());
		assertTrue(jq("$t_ig").prev().find(".ins-ig").exists());
		assertTrue(jq("$ig").children(".z-inputgroup-text").find(".tins-ig").exists());
		assertTrue(jq("$t_tig").prev().find(".tins-ig").exists());
	}

	@Test
	public void detachRemovesLabel() {
		open();
		click(jq("$btn_remove"));
		waitResponse();
		assertFalse(jq("$box_remove").find(".z-input-label-text").exists());
		assertFalse(jq("$box_remove").find("input").exists());
	}

	@Test
	public void widthPercentFillsContainer() {
		open();
		assertEquals(jq("$t_w0").outerWidth(), jq("$t_w").outerWidth(), 1);
		int plain = jq("$t_tw0").outerWidth();
		assertEquals(plain, jq("$t_tw").outerWidth(), 1);
		assertEquals(plain, jq("$t_twl").outerWidth(), 1);
	}

	@Test
	public void hflexInRowFillsCell() {
		open();
		assertEquals(jq("$t_row0").outerWidth(), jq("$t_row").outerWidth(), 1);
		assertEquals(jq("$t_trow0").outerWidth(), jq("$t_trow").outerWidth(), 1);
	}

	@Test
	public void disabledFieldLooksLikePlainDisabled() {
		open();
		assertEquals(jq("$t_dis0").find("input").css("background-color"),
			jq("$t_dis").find(FIELD).css("background-color"));
		assertEquals(jq("$t_tdis0").css("background-color"), jq("$t_tdis").find(FIELD).css("background-color"));
	}

	@Test
	public void focusKeptWhenLabelAdded() {
		open();
		for (String id : new String[] {"t_fl", "t_tfl"}) {
			click(jq("$" + id));
			waitResponse();
			eval("zk.$('$" + id + "').setLabel('Added')");
			waitResponse();
			assertTrue(jq("$" + id).find(".z-input-label-text").exists(), id);
			assertEquals("true", getEval("document.activeElement === zk.$('$" + id + "').getInputNode()"), id);
		}
	}

	@Test
	public void focusKeptWhenLabelRenamed() {
		open();
		for (String id : new String[] {"t_fl2", "t_tfl2"}) {
			click(jq("$" + id).find(FIELD));
			waitResponse();
			eval("zk.$('$" + id + "').setLabel('After')");
			waitResponse();
			assertEquals("After", jq("$" + id).find(".z-input-label-text").text(), id);
			assertEquals("true", getEval("document.activeElement === zk.$('$" + id + "').getInputNode()"), id);
		}
	}

	@Test
	public void labelClickFiresOnClickOnce() {
		open();
		click(jq("$t_click").find(".z-input-label-text"));
		waitResponse();
		assertEquals("1", jq("$lb_click").text());
		click(jq("$t_tclick").find(".z-input-label-text"));
		waitResponse();
		assertEquals("1", jq("$lb_tclick").text());
	}

	@Test
	public void labelClickFiresClientOnClickOnce() {
		open();
		for (String id : new String[] {"t_click", "t_tclick"}) {
			eval("(window.clicks = 0, zk.$('$" + id + "').listen({onClick: function () {window.clicks++;}}))");
			click(jq("$" + id).find(".z-input-label-text"));
			waitResponse();
			assertEquals("1", getEval("String(window.clicks)"), id);
		}
	}

	@Test
	public void spinnerStepWithoutFocusShrinksLabel() {
		open();
		eval("zk.$('$t_spin')._increase(true)");
		waitResponse();
		assertEquals("false", getEval("document.activeElement === zk.$('$t_spin').getInputNode()"));
		assertTrue(jq("$t_spin").hasClass("z-input-label-shrunk"));
	}

	@Test
	public void spinnerButtonShrinksLabel() {
		open();
		click(jq("$t_spin").find(".z-spinner-up"));
		waitResponse();
		assertNotEquals("", jq("$t_spin").find(FIELD).val());
		assertTrue(jq("$t_spin").hasClass("z-input-label-shrunk"));
	}

	@Test
	public void leftComboPopupAlignedWithField() {
		open();
		click(jq("$t_cleft").find("[id$=-btn]"));
		waitResponse();
		assertEquals(field("t_cleft", "left"), popup("t_cleft", "left"), 1);
		assertEquals(field("t_cleft", "bottom"), popup("t_cleft", "top"), 2);
	}

	@Test
	public void topComboPopupAlignedWithField() {
		open();
		click(jq("$t_ctop").find("[id$=-btn]"));
		waitResponse();
		assertEquals(field("t_ctop", "left"), popup("t_ctop", "left"), 1);
		assertEquals(field("t_ctop", "bottom"), popup("t_ctop", "top"), 2);
	}

	@Test
	public void dateboxPopupAlignedAndUnstyledByLabel() {
		open();
		click(jq("$t_dleft").find("[id$=-btn]"));
		waitResponse();
		assertEquals("false", getEval("zk.$('$t_dleft').$n('pp').className.indexOf('z-input-label') >= 0"));
		assertNotEquals("inline-flex", getEval("getComputedStyle(zk.$('$t_dleft').$n('pp')).display"));
		assertEquals(field("t_dleft", "left"), popup("t_dleft", "left"), 1);
	}
}
