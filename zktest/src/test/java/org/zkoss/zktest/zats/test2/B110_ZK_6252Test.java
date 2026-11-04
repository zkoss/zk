/* B110_ZK_6252Test.java

		Purpose:

		Description:

		History:
				Wed Nov 04 11:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6252Test extends WebDriverTestCase {

	@Test
	public void testIconCentredOnFirstLine() {
		assertIconsCentredOnFirstLine();
	}

	// IceBlue-only: IceBlue's confirmpopup.less sets the icon line-height to 1.5 × its 18px icon font; Marble's confirmpopup.css uses line-height 1.
	@Test
	@Tag("IceBlueOnly")
	public void testIconCentredOnFirstLineIceBlue() {
		assertIconsCentredOnFirstLine();
	}

	private void assertIconsCentredOnFirstLine() {
		connect();
		assertIconCentredOnFirstLine("$btn", "$cp");
		assertIconCentredOnFirstLine("$btn2", "$cp2");
	}

	private void assertIconCentredOnFirstLine(String opener, String popup) {
		click(jq(opener));
		waitResponse();
		String icon = "jq('" + popup + "').find('.z-confirmpopup-icon')[0]";
		String message = "jq('" + popup + "').find('.z-confirmpopup-message')[0]";
		double offset = Double.parseDouble(getEval("(function (i, m) {"
				+ "var ir = i.getBoundingClientRect();"
				+ "var line = parseFloat(getComputedStyle(m).lineHeight);"
				+ "return (ir.top + ir.height / 2) - (m.getBoundingClientRect().top + line / 2);"
				+ "})(" + icon + ", " + message + ")"));
		// 0.5: Marble's mask ::before sits 0.25px off the line centre (vertical-align on the inline-block)
		assertEquals(0, offset, 0.5, popup + ": icon centre is " + offset + "px off the centre of the message's first line");
		assertTrue(Boolean.parseBoolean(getEval("(function (i) {"
				+ "var r = i.getBoundingClientRect();"
				+ "return document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2) === i;"
				+ "})(" + icon + ")")), popup + ": the icon is not painted at its own centre");
		click(jq(popup).find(".z-confirmpopup-cancel"));
		waitResponse();
	}
}
