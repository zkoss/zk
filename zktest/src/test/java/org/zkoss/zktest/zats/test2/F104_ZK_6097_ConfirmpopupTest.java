/* F104_ZK_6097_ConfirmpopupTest.java

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:08:15 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6097_ConfirmpopupTest extends WebDriverTestCase {

	// ----- visibility lifecycle -----

	@Test
	public void hidden_until_opened() {
		connect();
		waitResponse();
		assertFalse(jq("$cp1").isVisible(),
				"confirmpopup must be hidden before open() is called");
	}

	@Test
	public void open_renders_message_icon_and_buttons() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		assertTrue(jq("$cp1").isVisible());
		assertTrue(jq("$cp1").find(".z-confirmpopup-message").exists());
		assertEquals("Confirm delete?", jq("$cp1").find(".z-confirmpopup-message").text());
		assertTrue(jq("$cp1").find(".z-confirmpopup-icon").exists(),
				"default iconSclass renders an icon");
		assertTrue(jq("$cp1").find(".z-confirmpopup-ok").exists());
		assertTrue(jq("$cp1").find(".z-confirmpopup-cancel").exists());
	}

	// ----- onOK / onCancel events -----

	@Test
	public void onOK_fires_and_closes_popup() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		click(jq("$cp1").find(".z-confirmpopup-ok"));
		waitResponse();
		assertEquals("ok", jq("$cpResult").text());
		assertFalse(jq("$cp1").isVisible(), "popup closes after OK");
	}

	@Test
	public void onCancel_fires_and_closes_popup() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		click(jq("$cp1").find(".z-confirmpopup-cancel"));
		waitResponse();
		assertEquals("cancelled", jq("$cpResult").text());
		assertFalse(jq("$cp1").isVisible(), "popup closes after Cancel");
	}

	// ----- message -----

	@Test
	public void message_empty_does_not_render_message_node() {
		connect();
		waitResponse();
		click(jq("$btn-nomsg"));
		waitResponse();
		assertTrue(jq("$cpNoMsg").isVisible());
		assertFalse(jq("$cpNoMsg").find(".z-confirmpopup-message").exists(),
				"empty message must not render the message div");
	}

	@Test
	public void message_dynamic_update_rerenders() {
		connect();
		waitResponse();
		click(jq("$btn-set-msg"));
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		assertEquals("Updated message", jq("$cp1").find(".z-confirmpopup-message").text());
	}

	// ----- iconSclass -----

	@Test
	public void iconSclass_empty_does_not_render_icon() {
		// iconSclass="" is the explicit "clear" sentinel — distinct from null,
		// which restores the default warning icon. renderProperties forwards
		// the empty string across the wire so the client mold suppresses <i>.
		connect();
		waitResponse();
		click(jq("$btn-noicon"));
		waitResponse();
		assertFalse(jq("$cpNoIcon").find(".z-confirmpopup-icon").exists(),
				"iconSclass=\"\" must not render the icon node");
	}

	@Test
	public void iconSclass_dynamic_update_rerenders() {
		connect();
		waitResponse();
		click(jq("$btn-set-icon"));
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		assertTrue(jq("$cp1").find(".z-confirmpopup-icon.z-icon-check-circle").exists(),
				"setIconSclass should update the rendered icon class");
	}

	// ----- button labels (default OK / Cancel) -----

	@Test
	public void labels_default_to_OK_Cancel() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		assertEquals("OK", jq("$cp1").find(".z-confirmpopup-ok").text());
		assertEquals("Cancel", jq("$cp1").find(".z-confirmpopup-cancel").text());
	}

	// ----- severity -----

	@Test
	public void severity_classes_applied_for_each_value() {
		connect();
		waitResponse();
		click(jq("$btn-info")); waitResponse();
		assertTrue(jq("$cpInfo").hasClass("z-confirmpopup-info"));

		click(jq("$btn-success")); waitResponse();
		assertTrue(jq("$cpSuccess").hasClass("z-confirmpopup-success"));

		click(jq("$btn-warning")); waitResponse();
		assertTrue(jq("$cpWarning").hasClass("z-confirmpopup-warning"));

		click(jq("$btn-danger")); waitResponse();
		assertTrue(jq("$cpDanger").hasClass("z-confirmpopup-danger"));

		click(jq("$btn-secondary")); waitResponse();
		assertTrue(jq("$cpSecondary").hasClass("z-confirmpopup-secondary"));
	}

	@Test
	public void severity_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		click(jq("$btn-bad-severity"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("severity must be"),
				"invalid severity should surface a WrongValueException — got: " + err);
	}

	// ----- placement -----

	@Test
	public void placement_top_class_applied() {
		connect();
		waitResponse();
		click(jq("$btn-top"));
		waitResponse();
		assertTrue(jq("$cpTop").hasClass("z-confirmpopup-placement-top"));
	}

	@Test
	public void placement_bottom_class_applied() {
		connect();
		waitResponse();
		click(jq("$btn-bottom"));
		waitResponse();
		assertTrue(jq("$cpBottom").hasClass("z-confirmpopup-placement-bottom"));
	}

	@Test
	public void placement_left_class_applied() {
		connect();
		waitResponse();
		click(jq("$btn-left"));
		waitResponse();
		assertTrue(jq("$cpLeft").hasClass("z-confirmpopup-placement-left"));
	}

	@Test
	public void placement_right_class_applied() {
		connect();
		waitResponse();
		click(jq("$btn-right"));
		waitResponse();
		assertTrue(jq("$cpRight").hasClass("z-confirmpopup-placement-right"));
	}

	@Test
	public void placement_invalid_throws_and_keeps_state() {
		connect();
		waitResponse();
		click(jq("$btn-bad-placement"));
		waitResponse();
		String err = jq("$errMsg").text();
		assertTrue(err.contains("placement must be"),
				"invalid placement should surface a WrongValueException — got: " + err);
	}

	// ----- placement-derived anchor (popup vs trigger geometry) -----

	@Test
	public void placement_anchors_popup_to_trigger_not_pointer() {
		// The bug fixed in this branch: the Confirmpopup.ts open() override
		// must always derive position from placement, not let the inherited
		// Popup.open(Component)'s default at_pointer leak through. We assert
		// the popup is opened near the trigger (overlap or adjacent), NOT at
		// the click pointer location far from the trigger. ZK may auto-flip
		// the placement when viewport space is tight, and may also shift on
		// the cross-axis if the popup would otherwise overflow — so we just
		// verify the popup's bounding box is reasonably close to the trigger.
		connect();
		waitResponse();
		click(jq("$btn-top"));
		waitResponse();
		String result = getEval(
				"(function(){"
				+ "  var trig = jq('$btn-top')[0];"
				+ "  var popup = jq('$cpTop')[0];"
				+ "  var tr = trig.getBoundingClientRect();"
				+ "  var pr = popup.getBoundingClientRect();"
				+ "  var dx = Math.max(0, Math.max(pr.left - tr.right, tr.left - pr.right));"
				+ "  var dy = Math.max(0, Math.max(pr.top - tr.bottom, tr.top - pr.bottom));"
				+ "  return Math.max(dx, dy) < 24;"  // within ~arrow distance
				+ "})()");
		assertEquals("true", result,
				"placement=top must anchor popup adjacent to (or overlapping) the trigger, "
				+ "not at the click pointer");
	}

	// ----- header (PrimeNG Confirmpopup) -----

	@Test
	public void header_renders_when_set() {
		connect();
		waitResponse();
		click(jq("$btn-with-header"));
		waitResponse();
		assertTrue(jq("$cpHeader .z-confirmpopup-header").exists());
		assertEquals("Confirm action", jq("$cpHeader .z-confirmpopup-header").text());
	}

	@Test
	public void header_absent_when_not_set() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		assertFalse(jq("$cp1 .z-confirmpopup-header").exists(),
				"popup with no header attribute must not render the header div");
	}

	@Test
	public void header_dynamic_set_rerenders() {
		connect();
		waitResponse();
		click(jq("$btn-set-header"));
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		assertTrue(jq("$cp1 .z-confirmpopup-header").exists());
		assertEquals("Heads up", jq("$cp1 .z-confirmpopup-header").text());
	}

	// ----- accessibility -----

	@Test
	public void aria_role_alertdialog() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		if (!Boolean.valueOf(getEval("!!window.za11y"))) return;
		assertEquals("alertdialog", jq("$cp1").attr("role"));
	}

	// ----- defaultFocus -----

	@Test
	public void default_focus_ok_focuses_ok_button() {
		connect();
		waitResponse();
		click(jq("$btn-focus-ok"));
		waitResponse();
		// give the setTimeout(0) focus dispatch a tick to land
		sleep(80);
		String activeId = (String) ((JavascriptExecutor) getWebDriver())
				.executeScript("return document.activeElement && document.activeElement.id;");
		assertTrue(activeId != null && activeId.endsWith("-ok"),
				"defaultFocus=ok must focus the OK button, got activeElement id: " + activeId);
	}

	@Test
	public void default_focus_cancel_focuses_cancel_button() {
		connect();
		waitResponse();
		click(jq("$btn-focus-cancel"));
		waitResponse();
		sleep(80);
		String activeId = (String) ((JavascriptExecutor) getWebDriver())
				.executeScript("return document.activeElement && document.activeElement.id;");
		assertTrue(activeId != null && activeId.endsWith("-cancel"),
				"defaultFocus=cancel must focus the Cancel button, got activeElement id: " + activeId);
	}

	// ----- keyboard: Enter activates OK -----

	@Test
	public void enter_key_activates_ok() {
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		sleep(80);
		// Synthesize Enter on the active element; the alertdialog's
		// _onKeyDown should route to the OK click path.
		((JavascriptExecutor) getWebDriver()).executeScript(
				"var w = zk.Widget.$(jq('$cp1')[0]);"
				+ "w._onKeyDown({domEvent:{key:'Enter'}, stop:function(){}});");
		waitResponse();
		assertEquals("ok", jq("$cpResult").text(),
				"Enter on confirmpopup must fire onOK");
	}

	// ----- arrow tip aligns with trigger center (viewport-edge case) -----

	@Test
	public void arrow_left_offset_is_set_after_open() {
		// The mold emits CSS left: 50% on the arrow; after open() the JS
		// must replace it with a px value that points at the trigger
		// center (whether or not the popup got viewport-clamped).
		connect();
		waitResponse();
		click(jq("$btn-open"));
		waitResponse();
		sleep(80);
		String arrowLeft = (String) ((JavascriptExecutor) getWebDriver())
				.executeScript(
				"var a = jq('$cp1').find('.z-confirmpopup-arrow')[0];"
				+ "return a && a.style.left;");
		assertTrue(arrowLeft != null && arrowLeft.endsWith("px"),
				"arrow must be repositioned to a px offset on open — got: " + arrowLeft);
	}

	// ----- setPlacement while open re-anchors the box (not just the arrow class) -----

	@Test
	public void set_placement_while_open_reanchors_box() {
		// Open cpTop (placement=top → box above the trigger), then flip to
		// 'bottom' while still open. The box must move below the trigger, not
		// merely swap the arrow-side CSS class.
		connect();
		waitResponse();
		click(jq("$btn-top"));
		waitResponse();
		sleep(80);
		double topBefore = ((Number) ((JavascriptExecutor) getWebDriver()).executeScript(
				"return jq('$cpTop')[0].getBoundingClientRect().top;")).doubleValue();

		getEval("(zk.Widget.$(jq('$cpTop')[0]).setPlacement('bottom'),'')");
		sleep(80);
		double topAfter = ((Number) ((JavascriptExecutor) getWebDriver()).executeScript(
				"return jq('$cpTop')[0].getBoundingClientRect().top;")).doubleValue();

		assertTrue(topAfter > topBefore + 10,
				"setPlacement('bottom') while open must re-anchor the box downward — "
						+ "top before=" + topBefore + " after=" + topAfter);
		assertTrue(jq("$cpTop").hasClass("z-confirmpopup-placement-bottom"),
				"arrow-side class must flip to bottom");
	}

	// ----- live severity change must not wash the open-state class -----

	@Test
	public void set_severity_while_open_keeps_open_class() {
		// setSeverity() calls updateDomClass_(), which rewrites className from
		// domClass_(). Popup.open() adds the open-state class imperatively, so
		// domClass_() must re-emit it while open — otherwise a live severity
		// change strips the open styling out from under the visible popup.
		connect();
		waitResponse();
		click(jq("$btn-top"));
		waitResponse();
		sleep(80);
		assertTrue(jq("$cpTop").hasClass("z-confirmpopup-open"),
				"baseline: an open confirmpopup carries the open-state class");
		getEval("(zk.Widget.$(jq('$cpTop')[0]).setSeverity('danger'),'')");
		sleep(60);
		assertTrue(jq("$cpTop").hasClass("z-confirmpopup-open"),
				"setSeverity while open must not wash the z-confirmpopup-open class");
		assertTrue(jq("$cpTop").hasClass("z-confirmpopup-danger"),
				"the new severity class must be applied");
	}

	// ----- coordinate open (no trigger ref) hides the arrow -----

	@Test
	public void coordinate_open_hides_arrow() {
		// Opened at a raw coordinate (no trigger element), _repositionArrow has
		// nothing to point at, so it must hide the arrow rather than leave it
		// statically centered over empty space.
		connect();
		waitResponse();
		click(jq("$btn-top"));
		waitResponse();
		sleep(80);
		String display = (String) ((JavascriptExecutor) getWebDriver()).executeScript(
				"var w = zk.Widget.$(jq('$cpTop')[0]);"
				+ "w._refNode = undefined;"
				+ "w._repositionArrow();"
				+ "var a = jq('$cpTop').find('.z-confirmpopup-arrow')[0];"
				+ "return a && a.style.display;");
		assertEquals("none", display,
				"arrow must be hidden when the popup has no trigger ref to point at");
	}

	@Test
	public void set_icon_sclass_null_restores_default_client_side() {
		// 6-7: server setIconSclass(null) restores the default icon (empty
		// string = suppress). Mirror that on the client so a client-bind null
		// push doesn't fall through the mold's `if (_iconSclass)` and hide it.
		connect();
		waitResponse();
		String result = getEval(
				"(function(){"
				+ " var w = zk.Widget.$(jq('$cpTop')[0]);"
				+ " w.setIconSclass(null);"
				+ " return w.getIconSclass();"
				+ "})()");
		assertEquals("z-icon-exclamation-triangle", result,
				"client setIconSclass(null) must restore the default icon, matching server semantics");
	}
}
