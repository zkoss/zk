/* F111_ZK_6250Test.java

        Purpose:

        Description:

        History:
                Wed Nov 04 15:52:56 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F111_ZK_6250Test extends WebDriverTestCase {

	// Jcrop binds after the image loads, which can be later than the AU response.
	private void waitForJcrop(String... croppers) {
		for (String cropper : croppers)
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(d -> Boolean.parseBoolean(getEval("!!zk.Widget.$('$" + cropper + "')._jcrop")));
	}

	private void connectAndWait() {
		connect();
		waitForJcrop("natural", "display", "naturalAspect", "displayAspect");
	}

	private void connectToFixesAndWait() {
		connect("/test2/F111-ZK-6250-2.zul");
		waitForJcrop("plain", "empty", "cors");
	}

	private boolean selectionVisible(String cropper) {
		return Boolean.parseBoolean(getEval("zk.Widget.$('$" + cropper + "')._jcrop.ui.selection.is(':visible')"));
	}

	private String scaled(String cropper) {
		return getEval("(function () {var c = zk.Widget.$('$" + cropper + "')._jcrop.tellScaled();"
				+ "return [c.x, c.y, c.w, c.h].join(',');})()");
	}

	private String waitForText(String label) {
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !jq("$" + label).text().isEmpty());
		return jq("$" + label).text();
	}

	private void clickAndWait(String button) {
		click(jq("$" + button));
		waitResponse();
	}

	private void drag(String cropper, int fromX, int fromY, int toX, int toY) {
		JQuery holder = jq("$" + cropper + " .z-cropper-holder");
		getActions().moveToElement(toElement(holder), fromX - holder.width() / 2, fromY - holder.height() / 2)
				.clickAndHold()
				.moveByOffset((toX - fromX) / 2, (toY - fromY) / 2)
				.moveByOffset(toX - fromX - (toX - fromX) / 2, toY - fromY - (toY - fromY) / 2)
				.release()
				.perform();
		waitResponse();
	}

	private void dragSelectionAwayAndBack(String cropper) {
		JQuery holder = jq("$" + cropper + " .z-cropper-holder");
		getActions().moveToElement(toElement(holder), 80 - holder.width() / 2, 50 - holder.height() / 2)
				.clickAndHold()
				.moveByOffset(20, 0)
				.moveByOffset(-20, 0)
				.release()
				.perform();
		waitResponse();
	}

	@Test
	public void initialSelectionIsInNaturalPixels() {
		connectAndWait();
		assertEquals("40,19,80,61", scaled("natural"));
		assertEquals("40,19,80,61", scaled("display"));
		assertEquals("", jq("$naturalLog").text(), "binding must not send a changed selection back");
	}

	@Test
	public void cropAtNaturalResolution() {
		connectAndWait();
		clickAndWait("crop");
		assertEquals("200x152", waitForText("naturalLog"));
		assertEquals("80x61", waitForText("displayLog"));
	}

	@Test
	public void dragReportsNaturalPixels() {
		connectAndWait();
		drag("natural", 10, 10, 110, 60);
		assertEquals("25,25,250,125", waitForText("naturalLog"));
		assertEquals("25,25,250,125", jq("$changingLog").text());
	}

	@Test
	public void dragToTheEdgeStaysInsideTheImage() {
		connectAndWait();
		drag("natural", 1, 1, 250, 190);
		assertEquals("3,3,597,447", waitForText("naturalLog"));
	}

	@Test
	public void maxWidthIsInNaturalPixels() {
		connectAndWait();
		clickAndWait("maxWidth");
		assertEquals("100,48,100,152", waitForText("naturalLog"));
		assertEquals("40,19,40,61", scaled("natural"));
	}

	@Test
	public void maxHeightIsInNaturalPixels() {
		connectAndWait();
		clickAndWait("maxHeight");
		assertEquals("100,48,200,100", waitForText("naturalLog"));
		assertEquals("40,19,80,40", scaled("natural"));
	}

	@Test
	public void resizeKeepsNaturalPixels() {
		connectAndWait();
		clickAndWait("shrink");
		drag("natural", 10, 10, 60, 35);
		assertEquals("50,50,250,125", waitForText("naturalLog"));
	}

	@Test
	public void switchToNaturalSize() {
		connectAndWait();
		clickAndWait("toggle");
		waitForJcrop("display");
		assertEquals("16,8,32,24", scaled("display"));
	}

	@Test
	public void oversizedCanvasFallsBackToDisplayedSize() {
		connectAndWait();
		eval("(function () {var toBlob = HTMLCanvasElement.prototype.toBlob;"
				+ "HTMLCanvasElement.prototype.toBlob = function (callback, type) {"
				+ "if (this.width > 100) callback(null); else toBlob.call(this, callback, type);};})()");
		clickAndWait("crop");
		assertEquals("80x61", waitForText("naturalLog"));
	}

	@Test
	public void serverSetSelectionStaysExact() {
		connectAndWait();
		clickAndWait("move");
		clickAndWait("coords");
		assertEquals("101,49,200,151", jq("$coordLog").text());
		assertEquals("", jq("$naturalLog").text(), "a selection set from the server must not come back rounded");
		clickAndWait("crop");
		assertEquals("200x151", waitForText("naturalLog"));
	}

	@Test
	public void serverSetSelectionAfterCancelStaysExact() {
		connectAndWait();
		clickAndWait("cancel");
		clickAndWait("place");
		clickAndWait("coords");
		assertEquals("101,49,200,152", jq("$coordLog").text());
		assertEquals("40,20,80,60", scaled("natural"));
	}

	@Test
	public void serverSetSelectionPastTheImageIsClamped() {
		connectAndWait();
		clickAndWait("overflow");
		clickAndWait("coords");
		assertEquals("400,48,200,152", jq("$coordLog").text());
	}

	@Test
	public void serverSetSelectionPastMaxWidthIsClamped() {
		connectAndWait();
		clickAndWait("maxWidth");
		clickAndWait("wider");
		clickAndWait("coords");
		assertEquals("100,48,100,152", jq("$coordLog").text());
	}

	@Test
	public void dragInDisplayPixels() {
		connectAndWait();
		drag("display", 10, 10, 110, 60);
		assertEquals("10,10,100,50", waitForText("displayLog"));
	}

	@Test
	public void unscaledImageGivesTheSameValuesInBothModes() {
		connectAndWait();
		// with aspectRatio 1.3 this drag gives Jcrop a width of 110.5
		drag("naturalAspect", 10, 10, 120, 90);
		drag("displayAspect", 10, 10, 120, 90);
		assertEquals("10,10,110,85", waitForText("displayAspectLog"));
		assertEquals("10,10,110,85", waitForText("naturalAspectLog"));
	}

	@Test
	public void dragAwayAndBackResyncsTheServer() {
		connectAndWait();
		dragSelectionAwayAndBack("natural");
		waitForText("changingLog");
		assertEquals("100,48,200,152", waitForText("naturalLog"));
		clickAndWait("coords");
		assertEquals("100,48,200,152", jq("$coordLog").text());
	}

	@Test
	public void clickOutsideTheSelectionClearsIt() {
		connectToFixesAndWait();
		JQuery holder = jq("$plain .z-cropper-holder");
		getActions().moveToElement(toElement(holder), 200 - holder.width() / 2, 150 - holder.height() / 2)
				.click()
				.perform();
		waitResponse();
		clickAndWait("coords");
		assertEquals("0,0,0,0", jq("$coordLog").text());
	}

	@Test
	public void moveAndResizeAlongOneAxis() {
		connectToFixesAndWait();
		clickAndWait("nudge");
		clickAndWait("coords");
		assertEquals("50,19,100,61", jq("$coordLog").text());
	}

	@Test
	public void serverSetSelectionAfterCancelInDisplayPixels() {
		connectToFixesAndWait();
		clickAndWait("cancel");
		clickAndWait("place");
		clickAndWait("coords");
		assertEquals("41,20,80,61", jq("$coordLog").text());
	}

	@Test
	public void sizeLimitWithoutSelectionSelectsNothing() {
		connectToFixesAndWait();
		clickAndWait("minWidth");
		assertEquals("", jq("$emptyLog").text());
		assertFalse(jq("$empty .z-cropper-toolbar").isVisible());
	}

	@Test
	public void serverSetSelectionWithMinWidthInDisplayPixels() {
		connectToFixesAndWait();
		clickAndWait("minWidth");
		clickAndWait("placeEmpty");
		clickAndWait("emptyCoords");
		assertEquals("41,20,120,61", jq("$coordLog").text());
		assertEquals("41,20,120,61", scaled("empty"));
		assertEquals("", jq("$emptyLog").text());
	}

	@Test
	public void serverResizeToZeroClearsTheSelection() {
		connectToFixesAndWait();
		clickAndWait("clear");
		assertFalse(selectionVisible("plain"));
		assertFalse(jq("$plain .z-cropper-toolbar").isVisible());
	}

	@Test
	public void crossOriginFollowsTheServer() {
		connectToFixesAndWait();
		String crossOrigin = "zk.Widget.$('$cors').$n('img').getAttribute('crossorigin')";
		clickAndWait("credentials");
		assertEquals("use-credentials", getEval(crossOrigin));
		clickAndWait("anonymous");
		assertEquals("anonymous", getEval(crossOrigin));
	}

	@Test
	public void dragAwayAndBackInDisplayPixels() {
		connectToFixesAndWait();
		dragSelectionAwayAndBack("plain");
		waitForText("changingLog");
		clickAndWait("coords");
		assertEquals("40,19,80,61", jq("$coordLog").text());
	}

	@Test
	public void cropAndCancelBeforeTheImageLoads() {
		connectToFixesAndWait();
		clickAndWait("cropBroken");
		clickAndWait("cancelBroken");
		assertEquals("0", jq("$broken").toWidget().get("w"));
		assertNoJSError();
	}

	@Test
	public void detachBeforeTheImageLoads() {
		connectToFixesAndWait();
		clickAndWait("detach");
		assertFalse(jq("$broken").exists());
		assertNoJSError();
	}
}
