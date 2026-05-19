/* F110_ZK_6100_FloatingTest.java

		Purpose:

		Description:

		History:
				Tue May 19 13:17:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6100_FloatingTest extends WebDriverTestCase {

	private static final String[] PLAIN_IDS = {
		"t_textbox", "t_intbox", "t_longbox", "t_doublebox", "t_decimalbox"
	};

	private static final String[] COMPOSITE_IDS = {
		"t_combobox", "t_bandbox", "t_datebox", "t_timebox", "t_spinner", "t_doublespinner", "t_timepicker"
	};

	private static final String FIELD = "[id$=-real]";

	private void open() {
		connect("/test2/F110-ZK-6100-floating.zul");
		waitResponse();
	}

	private double rect(String id, String part, String side) {
		return Double.parseDouble(getEval(
			"jq('$" + id + "').find('" + part + "')[0].getBoundingClientRect()." + side));
	}

	private double labelCenterY(String id) {
		return (rect(id, ".z-input-label-text", "top") + rect(id, ".z-input-label-text", "bottom")) / 2;
	}

	private String labelCss(String id, String prop) {
		return jq("$" + id).find(".z-input-label-text").css(prop);
	}

	private String placeholderColor(String id) {
		return getEval("getComputedStyle(jq('$" + id + "').find('input')[0], '::placeholder').color");
	}

	private String resolvedColor(String cssVar) {
		return getEval("(function () {var d = document.createElement('div'); d.style.color = 'var(" + cssVar
			+ ")'; document.body.appendChild(d); var c = getComputedStyle(d).color; d.remove(); return c;})()");
	}

	@Test
	public void everyInputRendersLabelInsideRoot() {
		open();
		for (String[] ids : new String[][] {PLAIN_IDS, COMPOSITE_IDS, {"t_textarea"}})
			for (String id : ids) {
				assertTrue(jq("$" + id).hasClass("z-input-label-floating"), id);
				assertEquals(jq("$" + id).find(FIELD).attr("id"),
					jq("$" + id).find(".z-input-label-text").attr("for"), id);
			}
	}

	@Test
	public void emptyLabelCenteredInField() {
		open();
		for (String[] ids : new String[][] {PLAIN_IDS, COMPOSITE_IDS})
			for (String id : ids) {
				assertFalse(jq("$" + id).hasClass("z-input-label-shrunk"), id);
				double fieldCenterY = (rect(id, FIELD, "top") + rect(id, FIELD, "bottom")) / 2;
				assertEquals(fieldCenterY, labelCenterY(id), 2, id);
			}
	}

	@Test
	public void textareaLabelOnFirstLine() {
		open();
		double top = rect("t_textarea", FIELD, "top");
		double center = labelCenterY("t_textarea");
		assertTrue(center > top && center < top + 30, "center=" + center + " top=" + top);
	}

	@Test
	public void labelDoesNotCoverButton() {
		open();
		for (String id : COMPOSITE_IDS)
			assertTrue(rect(id, ".z-input-label-text", "right") <= rect(id, "[id$=-btn]", "left"), id);
		assertTrue(rect("t_long", ".z-input-label-text", "right") <= rect("t_long", "[id$=-btn]", "left"));
	}

	@Test
	public void focusFloatsLabelOntoTopBorder() {
		open();
		for (String id : new String[] {"t_combobox", "t_textbox"}) {
			String colorBefore = labelCss(id, "color");
			click(jq("$" + id).find(FIELD));
			waitResponse();
			sleep(400);
			assertTrue(jq("$" + id).hasClass("z-input-label-shrunk"), id);
			assertEquals(rect(id, FIELD, "top"), labelCenterY(id), 3, id);
			assertNotEquals(colorBefore, labelCss(id, "color"), id);
		}
	}

	@Test
	public void typeThenBlurStaysShrunk() {
		open();
		for (String id : new String[] {"t_combobox", "t_textbox"}) {
			click(jq("$" + id).find(FIELD));
			waitResponse();
			sendKeys(jq("$" + id).find(FIELD), "hello");
			waitResponse();
			click(jq("$t_xss").find(FIELD));
			waitResponse();
			assertTrue(jq("$" + id).hasClass("z-input-label-shrunk"), id);
		}
	}

	@Test
	public void blurEmptyReturnsToCenter() {
		open();
		for (String id : new String[] {"t_combobox", "t_textbox"}) {
			click(jq("$" + id).find(FIELD));
			waitResponse();
			click(jq("$t_xss").find(FIELD));
			waitResponse();
			assertFalse(jq("$" + id).hasClass("z-input-label-shrunk"), id);
		}
	}

	@Test
	public void prefilledIsShrunk() {
		open();
		assertTrue(jq("$t_prefilled").hasClass("z-input-label-shrunk"));
		assertTrue(jq("$t_tprefilled").hasClass("z-input-label-shrunk"));
	}

	@Test
	public void disabledLabelMatchesFieldBackground() {
		open();
		for (String id : new String[] {"t_disabled", "t_tdisabled"})
			assertEquals(jq("$" + id).find(FIELD).css("background-color"), labelCss(id, "background-color"), id);
	}

	@Test
	public void readonlyLabelUsesReadonlyColors() {
		open();
		String readonly = resolvedColor("--zk-input-readonly-color");
		assertNotEquals(resolvedColor("--zk-input-disabled-color"), readonly);
		for (String id : new String[] {"t_readonly", "t_treadonly"}) {
			assertTrue(jq("$" + id).hasClass("z-input-label-shrunk"), id);
			assertEquals(readonly, labelCss(id, "color"), id);
			assertEquals(jq("$" + id).find(FIELD).css("background-color"), labelCss(id, "background-color"), id);
		}
	}

	@Test
	public void invalidClassFollowsValidation() {
		open();
		for (String id : new String[] {"t_invalid", "t_tinvalid"}) {
			click(jq("$" + id).find(FIELD));
			waitResponse();
			click(jq("$t_xss").find(FIELD));
			waitResponse();
			assertTrue(jq("$" + id).hasClass("z-input-label-invalid"), id);
			click(jq("$" + id).find(FIELD));
			sendKeys(jq("$" + id).find(FIELD), "ok");
			waitResponse();
			click(jq("$t_xss").find(FIELD));
			waitResponse();
			assertFalse(jq("$" + id).hasClass("z-input-label-invalid"), id);
		}
	}

	@Test
	public void invalidStaysAfterErrorboxClosed() {
		open();
		click(jq("$t_tinvalid").find(FIELD));
		waitResponse();
		click(jq("$t_xss").find(FIELD));
		waitResponse();
		click(jq(".z-errorbox-close"));
		waitResponse();
		assertFalse(jq(".z-errorbox").isVisible());
		assertTrue(jq("$t_tinvalid").hasClass("z-input-label-invalid"));
	}

	@Test
	public void placeholderHiddenUntilShrunk() {
		open();
		assertEquals("rgba(0, 0, 0, 0)", placeholderColor("t_hint"));
		click(jq("$t_hint").find(FIELD));
		waitResponse();
		assertNotEquals("rgba(0, 0, 0, 0)", placeholderColor("t_hint"));
	}

	@Test
	public void inplaceWithValueIsShrunk() {
		open();
		assertTrue(jq("$t_inplace").hasClass("z-input-label-shrunk"));
	}

	@Test
	public void xssEscape() {
		open();
		assertEquals("<script>alert(1)</script>", jq("$t_xss").find(".z-input-label-text").text());
		assertFalse(jq("$t_xss").find(".z-input-label-text script").exists());
	}

	@Test
	public void longLabelEllipsis() {
		open();
		assertEquals("ellipsis", labelCss("t_long", "text-overflow"));
		assertEquals("true", getEval(
			"(function (n) {return n.scrollWidth > n.clientWidth;})(jq('$t_long').find('.z-input-label-text')[0])"));
	}

	@Test
	public void rtlLabel() {
		open();
		assertEquals("8px", labelCss("t_rtl", "right"));
		assertEquals("8px", labelCss("t_trtl", "right"));
	}

	@Test
	public void serverSetLabelNullRestoresPlainMarkup() {
		open();
		click(jq("$btn_unsetLabel"));
		waitResponse();
		assertFalse(jq("$t_dynamic").find(".z-input-label-text").exists());
		assertEquals("INPUT", getEval("zk.$('$t_tdynamic').$n().tagName"));
	}

	@Test
	public void serverSetPositionFloatingToLeftAndTop() {
		open();
		click(jq("$btn_setPosLeft"));
		waitResponse();
		assertTrue(jq("$t_tdynamic").hasClass("z-input-label-left"));
		click(jq("$btn_setPosTop"));
		waitResponse();
		assertTrue(jq("$t_dynamic").hasClass("z-input-label-top"));
		assertTrue(jq("$t_tdynamic").hasClass("z-input-label-top"));
	}

	@Test
	public void sclassChangeKeepsShrunk() {
		open();
		click(jq("$btn_sclass"));
		waitResponse();
		for (String id : new String[] {"t_prefilled", "t_tprefilled"}) {
			assertTrue(jq("$" + id).hasClass("changed"), id);
			assertTrue(jq("$" + id).hasClass("z-input-label-shrunk"), id);
		}
		assertTrue(jq("$t_tprefilled").find(FIELD).hasClass("z-textbox"));
	}

	@Test
	public void nullPositionFallsBackToLeft() {
		open();
		eval("zk.$('$t_dynamic').setLabelPosition(null)");
		waitResponse();
		assertTrue(jq("$t_dynamic").hasClass("z-input-label-left"));
		assertFalse(jq("$t_dynamic").hasClass("z-input-label-null"));
	}

	@Test
	public void forcedColorsRuleTargetsLabel() {
		open();
		assertEquals("true", getEval(
			"Array.from(document.styleSheets).some(function (s) {"
			+ " try { return Array.from(s.cssRules).some(function (r) {"
			+ "  return r.media && r.media.mediaText.indexOf('forced-colors') >= 0"
			+ "   && r.cssText.indexOf('z-input-label-text') >= 0; });"
			+ " } catch (e) { return false; } })"));
	}
}
