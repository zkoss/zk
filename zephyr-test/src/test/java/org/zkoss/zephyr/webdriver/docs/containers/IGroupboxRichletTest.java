/* IGroupboxRichletTest.java

	Purpose:

	Description:

	History:
		Mon Apr 11 17:28:49 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.containers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IGroupbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Groupbox">Groupbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IGroupbox
 */
public class IGroupboxRichletTest extends WebDriverTestCase {
	@Test
	public void caption() {
		connect("/containers/igroupbox/caption");
		assertTrue(jq(".z-caption-content").text().contains("caption"));
	}

	@Test
	public void closable() {
		connect("/containers/igroupbox/closable");
		click(jq(".z-groupbox-title"));
		waitResponse();
		assertFalse(jq(".z-groupbox").hasClass("z-groupbox-collapsed"));
		click(jq("@button"));
		waitResponse();
		click(jq(".z-groupbox-title"));
		waitResponse();
		assertTrue(jq(".z-groupbox").hasClass("z-groupbox-collapsed"));
	}

	@Test
	public void contentSclass() {
		connect("/containers/igroupbox/contentSclass");
		assertTrue(jq(".z-groupbox-content").hasClass("z-icon-home"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-groupbox-content").hasClass("z-icon-user"));
	}

	@Test
	public void contentStyle() {
		connect("/containers/igroupbox/contentStyle");
		assertTrue(jq(".z-groupbox-content").attr("style").contains("blue"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-groupbox-content").attr("style").contains("red"));
	}

	@Test
	public void open() {
		connect("/containers/igroupbox/open");
		assertTrue(jq(".z-groupbox").hasClass("z-groupbox-collapsed"));
		click(jq("@button"));
		waitResponse();
		assertFalse(jq(".z-groupbox").hasClass("z-groupbox-collapsed"));
	}

	@Test
	public void title() {
		connect("/containers/igroupbox/title");
//		assertEquals("Fruits", jq(".z-groupbox-title-content").text());
		assertTrue(jq(".z-groupbox-title-content").text().contains("Fruits"));
		click(jq("@button"));
		waitResponse();
//		assertEquals("Fruits", jq(".z-groupbox-title-content").text());

		assertTrue(jq(".z-groupbox-title-content").text().contains("new title"));
	}
}