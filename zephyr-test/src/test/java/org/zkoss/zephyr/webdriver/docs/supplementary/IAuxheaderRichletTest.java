/* IAuxheaderRichletTest.java

	Purpose:

	Description:

	History:
		Thu Feb 17 12:20:39 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.supplementary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IAuxheader;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * A set of unit test for {@link IAuxheader} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Auxheader">Auxheader</a>,
 * if any.
 *
 * @author katherine
 * @see IAuxheader
 */
public class IAuxheaderRichletTest extends WebDriverTestCase {
	@Test
	public void example() {
		connect("/supplementary/iauxheader/example");
		// with two auxheader
		JQuery auxhead1 = jq(".z-auxhead:eq(0) .z-auxheader");
		assertEquals("6", auxhead1.eq(0).attr("colspan"));
		assertFalse(auxhead1.eq(2).exists());

		// with four auxheader
		JQuery auxhead2 = jq(".z-auxhead:eq(1) .z-auxheader");
		assertEquals("3", auxhead2.eq(0).attr("colspan"));
		assertFalse(auxhead2.eq(4).exists());

		// with twelve auxheader
		JQuery auxhead3 = jq(".z-auxhead:eq(2) .z-auxheader");
		assertFalse(auxhead3.eq(12).exists());
	}

	@Test
	public void limitationOfRowspan() {
		connect("/supplementary/iauxheader/limitationOfRowspan");
		assertEquals(0, jq(".z-columns .z-column-content:eq(1)").width());
	}

	@Test
	public void workaroundForLimitationOfRowspan() {
		connect("/supplementary/iauxheader/workaroundForLimitationOfRowspan");

		JQuery auxhead1 = jq(".z-auxhead:eq(0) .z-auxheader");
		JQuery auxhead2 = jq(".z-auxhead:eq(1) .z-auxheader");
		assertEquals("A", auxhead1.eq(0).text());
		assertEquals("BC", auxhead1.eq(1).text());
		assertEquals("D", auxhead1.eq(2).text());
		assertEquals("B", auxhead2.eq(0).text());
		assertEquals("C", auxhead2.eq(1).text());
	}

	@Test
	public void image() {
		connect("/supplementary/iauxheader/image");
		assertTrue(jq(".z-auxhead img").attr("src").contains("ZK-Logo"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-auxhead img").attr("src").contains("ZK-Logo-old"));
	}

	@Test
	public void colspan() {
		connect("/supplementary/iauxheader/colspan");
		assertEquals("2", jq(".z-auxheader").attr("colspan"));
		click(jq("@button"));
		waitResponse();
		assertEquals("1", jq(".z-auxheader").attr("colspan"));
	}

	@Test
	public void rowspan() {
		connect("/supplementary/iauxheader/rowspan");
		assertEquals("2", jq(".z-auxheader").attr("rowspan"));
		click(jq("@button"));
		waitResponse();
		assertEquals("1", jq(".z-auxheader").attr("rowspan"));
	}
}