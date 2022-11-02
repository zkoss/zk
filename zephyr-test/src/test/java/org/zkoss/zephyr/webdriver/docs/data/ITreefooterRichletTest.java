/* ITreefooterRichletTest.java

	Purpose:

	Description:

	History:
		Fri Feb 18 18:04:14 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.ITreefooter;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link ITreefooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treefooter">Treefooter</a>,
 * if any.
 *
 * @author katherine
 * @see ITreefooter
 */
public class ITreefooterRichletTest extends WebDriverTestCase {

	@Test
	public void image() {
		connect("/data/itree/iTreefooter/image");
		assertTrue(jq(".z-treefooter img").attr("src").contains("ZK-Logo.gif"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-treefooter img").attr("src").contains("ZK-Logo-old.gif"));
	}
}