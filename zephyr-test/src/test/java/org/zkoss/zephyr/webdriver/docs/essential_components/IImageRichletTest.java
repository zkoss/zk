/* IImageRichletTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Jan 11 15:08:44 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.stateless.zpr.IImage;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IImage} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Image">Image</a>,
 * if any.
 * @author leon
 * @see IImage
 */
public class IImageRichletTest extends WebDriverTestCase {
	@Test
	public void testSrc() {
		connect("/essential_components/iimage/src");
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-image:eq(0)").attr("src"));
		click(jq("@button"));
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-image:eq(0)").attr("src"));
	}

	@Test
	public void testContent() {
		connect("/essential_components/iimage/content");
		String content = jq(".z-image:eq(0)").attr("src");
		assertTrue(content.startsWith("blob:"));
		click(jq("@button"));
		waitResponse();
		assertNotEquals(content, jq(".z-image:eq(0)").attr("src"));
	}

	@Test
	public void testRenderedImage() {
		connect("/essential_components/iimage/renderedimage");
		assertTrue( jq(".z-image:eq(0)").attr("src").startsWith("blob:"));
		assertEquals(400, jq(".z-image:eq(0)").width());
		assertEquals(300, jq(".z-image:eq(0)").height());
	}

	@Test
	public void testHover() {
		connect("/essential_components/iimage/hover");
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-image:eq(0)").attr("src"));
		Actions act = getActions();
		// mouse in
		act.moveToElement(toElement(jq(".z-image:eq(0)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-image:eq(0)").attr("src"));
		// mouse out
		act.moveToElement(toElement(jq(".z-label:eq(0)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo.gif", jq(".z-image:eq(0)").attr("src"));
		click(jq("@button"));
		waitResponse();
		// mouse in
		act.moveToElement(toElement(jq(".z-image:eq(0)"))).build().perform();
		waitResponse();
		assertEquals("/zephyr-test/zephyr/ZK-Logo-old.gif", jq(".z-image:eq(0)").attr("src"));
	}

	@Test
	public void testLocaleDependentImage() {
		connect("/essential_components/iimage/locale");
		assertEquals("/zephyr-test/zephyr/ZK-Logo_en_US.gif", jq(".z-image:eq(0)").attr("src"));
	}

	@Test
	public void testPreloadImage() {
		connect("/essential_components/iimage/preload");
		closeZKLog();
		click(jq(".z-image:eq(0)"));
		waitResponse();
		assertEquals("true", getZKLog());
		closeZKLog();
		click(jq("@button"));
		waitResponse();
		assertEquals("null", getZKLog());
	}

	@Test
	public void testSize() {
		connect("/essential_components/iimage/size");
		assertEquals(72, jq(".z-image:eq(0)").width());
		assertEquals(64, jq(".z-image:eq(0)").height());
	}
}
