/* ICenterRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:55:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.layouts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.zpr.ICenter;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ICenter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/Center">Center</a>,
 * if any.
 *
 * @author katherine
 * @see ICenter
 */
public class ICenterRichletTest extends WebDriverTestCase {
	@Test
	public void collapsible() {
		connect("/data/iCenter/collapsible");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void height() {
		connect("/data/iCenter/height");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void maxsize() {
		connect("/data/iCenter/maxsize");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void minsize() {
		connect("/data/iCenter/minsize");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void open() {
		connect("/data/iCenter/open");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void slidable() {
		connect("/data/iCenter/slidable");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void slide() {
		connect("/data/iCenter/slide");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void splittable() {
		connect("/data/iCenter/splittable");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void visible() {
		connect("/data/iCenter/visible");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}

	@Test
	public void width() {
		connect("/data/iCenter/width");
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
		assertTrue(jq(".z-label:contains(readonly)").exists());
	}
}