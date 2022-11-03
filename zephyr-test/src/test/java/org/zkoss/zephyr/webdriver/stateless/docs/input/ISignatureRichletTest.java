/* ISignatureRichletTest.java

	Purpose:

	Description:

	History:
		Tue Mar 08 16:51:34 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.stateless.docs.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.ISignature} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Signature">Signature</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ISignature
 */
public class ISignatureRichletTest extends WebDriverTestCase {
	@Test
	public void upload() {
		connect("/input/isignature/example/upload");
		String src = jq(".z-image").attr("src");
		click(jq(".z-signature-tool-button-save"));
		waitResponse();
		assertNotEquals(src, jq(".z-image").attr("src"));
	}

	@Test
	public void backgroundColor() {
		connect("/input/isignature/backgroundColor");
		String script = "zk.$('$signature').$n('canvas-bg').getContext('2d').getImageData(1, 1, 1, 1).data";
		String bgColor = WebDriverTestCase.getEval(script);
		assertFalse(bgColor.contains("255,255,255,255"));
		click(jq("@button"));
		waitResponse();
		assertNotEquals(bgColor, WebDriverTestCase.getEval(script));
	}

	@Test
	public void backgroundImage() {
		connect("/input/isignature/backgroundImage");
		String script = "zk.$('$signature').$n('canvas-bg').getContext('2d').getImageData(1, 1, 1, 1).data";
		String bgColor = WebDriverTestCase.getEval(script);
		click(jq("@button"));
		waitResponse();
		assertFalse(bgColor.contains("255,255,255,255"));
		assertNotEquals(bgColor, WebDriverTestCase.getEval(script));
	}

	@Test
	public void clearLabel() {
		connect("/input/isignature/clearLabel");
		assertEquals("clear", jq(".z-signature-tool-button-label:eq(2)").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("clear2", jq(".z-signature-tool-button-label:eq(2)").text());
	}

	@Test
	public void penColor() {
		connect("/input/isignature/penColor");
		String script = "zk.$('$signature')._penColor";
		assertEquals("blue", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("black", WebDriverTestCase.getEval(script));
	}

	@Test
	public void penSize() {
		connect("/input/isignature/penSize");
		String script = "zk.$('$signature')._penSize";
		assertEquals("2", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("1", WebDriverTestCase.getEval(script));
	}

	@Test
	public void saveLabel() {
		connect("/input/isignature/saveLabel");
		assertEquals("save", jq(".z-signature-tool-button-label:eq(1)").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("save2", jq(".z-signature-tool-button-label:eq(1)").text());
	}

	@Test
	public void saveType() {
		connect("/input/isignature/saveType");
		String script = "zk.$('$signature')._saveType";
		assertEquals("image/jpg", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("image/png", WebDriverTestCase.getEval(script));
	}

	@Test
	public void toolbarVisible() {
		connect("/input/isignature/toolbarVisible");
		assertFalse(jq(".z-signature-toolbar").isVisible());
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-signature-toolbar").isVisible());
	}

	@Test
	public void undoLabel() {
		connect("/input/isignature/undoLabel");
		assertEquals("Undo", jq(".z-signature-tool-button-label").text());
		click(jq("@button"));
		waitResponse();
		assertEquals("Undo2", jq(".z-signature-tool-button-label").text());
	}
}