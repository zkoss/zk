/* IDropuploadRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 18:13:28 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.statelessex.sul.IDropupload} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Dropupload">Dropupload</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IDropupload
 */
public class IDropuploadRichletTest extends WebDriverTestCase {
	@Test
	public void anchorId() {
		connect("/essential_components/iDropupload/anchorId");
		String script = "zk.$('$upload')._anchorUuid";
		assertEquals("$div", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", WebDriverTestCase.getEval(script));
	}

	@Test
	public void content() {
		connect("/essential_components/iDropupload/content");
		String script = "zk.$('$upload')._content";
		assertEquals("upload", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("upload2", WebDriverTestCase.getEval(script));
	}

	@Test
	public void detection() {
		connect("/essential_components/iDropupload/detection");
		String script = "zk.$('$upload')._detection";
		assertEquals("self", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("browser", WebDriverTestCase.getEval(script));
	}

	@Test
	public void maxFileCount() {
		connect("/essential_components/iDropupload/maxFileCount");
		String script = "zk.$('$upload')._maxFileCount";
		assertEquals("2", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("1", WebDriverTestCase.getEval(script));
	}

	@Test
	public void maxsize() {
		connect("/essential_components/iDropupload/maxsize");
		String script = "zk.$('$upload')._maxsize";
		assertEquals("5000", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("1000", WebDriverTestCase.getEval(script));
	}

	@Test
	public void suppressedErrors() {
		connect("/essential_components/iDropupload/suppressedErrors");
		String script = "zk.$('$upload')._suppressedErrors";
		assertEquals("[error]", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("[]", WebDriverTestCase.getEval(script));
	}

	@Test
	public void viewerClass() {
		connect("/essential_components/iDropupload/viewerClass");
		String script = "zk.$('$upload')._viewerClass";
		assertEquals("class", WebDriverTestCase.getEval(script));
		click(jq("@button"));
		waitResponse();
		assertEquals("null", WebDriverTestCase.getEval(script));
	}
}