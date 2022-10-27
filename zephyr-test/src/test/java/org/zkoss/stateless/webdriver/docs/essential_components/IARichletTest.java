/* IARichletTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Jan 04 15:16:15 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.stateless.sul.IA;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of examples for {@link IA} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/A">A</a>,
 * if any.
 * @author leon
 * @see IA
 */
public class IARichletTest extends WebDriverTestCase {
	@Test
	public void testLabel() {
		connect("/essential_components/ia/example");
		assertEquals("Visit ZK!", jq(".z-a:eq(0)").text());
		assertEquals("jump to uri", jq(".z-a:eq(1)").text());
	}

	@Test
	public void testHref() {
		connect("/essential_components/ia/example");
		assertEquals("https://www.zkoss.org", jq(".z-a").attr("href"));
		assertEquals("uri", jq(".z-a:eq(1)").attr("href"));

		click(jq(".z-a:eq(1)"));
		waitResponse();
		assertTrue(driver.getCurrentUrl().endsWith("zephyr-test/essential_components/ia/uri"));

		click(jq("@button"));
		waitResponse();
		assertEquals("href", jq(".z-a:eq(1)").attr("href"));

		click(jq(".z-a:eq(0)"));
		waitResponse();
		assertTrue(driver.getCurrentUrl().endsWith("zephyr-test/essential_components/ia/example"));

		click(jq(".z-a:eq(0)"));
		sleep(1000);
		assertEquals("https://www.zkoss.org/", driver.getCurrentUrl());
	}

	@Test
	public void testDir() {
		connect("/essential_components/ia/dir");
		assertTrue(jq(".z-a:eq(0)").html().endsWith("normal"));
		assertTrue(jq(".z-a:eq(1)").html().startsWith("reverse"));
		click(jq("@button"));
		waitResponse();
		assertTrue(jq(".z-a:eq(0)").html().startsWith("normal"));
	}

	@Test
	public void testDirEnum() {
		connect("/essential_components/ia/dir2");
		assertTrue(jq(".z-a:eq(0)").html().endsWith("normal"));
		assertTrue(jq(".z-a:eq(1)").html().startsWith("reverse"));
	}

	@Test
	public void testDisabled() {
		connect("/essential_components/ia/disabled");
		assertEquals("disabled", jq(".z-a").attr("disabled"));

		click(jq(".z-a"));
		waitResponse();
		assertFalse(isZKLogAvailable());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-a"));
		waitResponse();
		assertEquals("doClick", getZKLog());
	}

	@Test
	public void testTarget() {
		connect("/essential_components/ia/target");
		assertEquals("_blank", jq(".z-a").attr("target"));

		click(jq("@button"));
		waitResponse();
		click(jq(".z-a"));
		assertEquals(1, driver.getWindowHandles().size());
	}

	@Test
	public void testTargetEnum() {
		connect("/essential_components/ia/target2");
		assertEquals("_blank", jq(".z-a").attr("target"));

		click(jq(".z-a"));
		waitResponse();
		assertEquals(2, driver.getWindowHandles().size());
	}

	@Test
	public void testAutodisable() {
		connect("/essential_components/ia/autodisable");
		click(jq(".z-a:eq(0)"));
		// do not wait
		assertEquals("disabled", jq(".z-a:eq(0)").attr("disabled"));
		assertEquals("disabled", jq(".z-a:eq(3)").attr("disabled"));
		waitResponse();
		assertEquals("null", jq(".z-a:eq(0)").attr("disabled"));
		assertEquals("null", jq(".z-a:eq(3)").attr("disabled"));
		click(jq("@button"));
		waitResponse();
		click(jq(".z-a:eq(0)"));
		assertEquals("null", jq(".z-a:eq(0)").attr("disabled"));
	}

	@Test
	public void testAutodisableSelf() {
		connect("/essential_components/ia/autodisable");
		click(jq(".z-a:eq(1)"));
		// do not wait
		assertEquals("disabled", jq(".z-a:eq(1)").attr("disabled"));
		assertEquals("disabled", jq(".z-a:eq(3)").attr("disabled"));
		waitResponse();
		assertEquals("null", jq(".z-a:eq(1)").attr("disabled"));
		assertEquals("null", jq(".z-a:eq(3)").attr("disabled"));
	}

	@Test
	public void testAutodisableManuallyControl() {
		connect("/essential_components/ia/autodisable");
		click(jq(".z-a:eq(2)"));
		// do not wait
		assertEquals("disabled", jq(".z-a:eq(2)").attr("disabled"));
		assertEquals("disabled", jq(".z-a:eq(3)").attr("disabled"));
		waitResponse();
		assertEquals("null", jq(".z-a:eq(1)").attr("disabled"));
		assertEquals("disabled", jq(".z-a:eq(3)").attr("disabled"));
		// do blur
		getActions().sendKeys(Keys.TAB).build().perform();
		waitResponse();
		assertTrue(isZKLogAvailable());
		assertEquals("null", jq(".z-a:eq(3)").attr("disabled"));
	}

	@Test
	public void testFocusEvent() {
		connect("/essential_components/ia/focus");
		click(jq(".z-a"));
		waitResponse();
		assertEquals("doFocus", getZKLog());
	}

	@Test
	public void testBlurEvent() {
		connect("/essential_components/ia/blur");
		click(jq(".z-a"));
		getActions().sendKeys(Keys.TAB).build().perform();
		waitResponse();
		assertEquals("doBlur", getZKLog());
	}
}
