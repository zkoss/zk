/* IImagemapRichletTest.java

		Purpose:
		
		Description:
		
		History:
				Mon Feb 07 15:21:48 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link org.zkoss.zephyr.zpr.IImagemap} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Imagemap">Imagemap</a>,
 * if any.
 * @author leon
 * @see org.zkoss.zephyr.zpr.IImagemap
 */
public class IImagemapRichletTest extends WebDriverTestCase {
	@Test
	public void testExample() {
		connect("/essential_components/iimagemap/example");
		clickAt(jq(".z-imagemap>img:eq(0)"), -10, 0);
		waitResponse();
		assertEquals("left", getZKLog());
		closeZKLog();
		waitResponse();
		clickAt(jq(".z-imagemap>img:eq(0)"), 10, 0);
		waitResponse();
		assertEquals("right", getZKLog());
	}
}
