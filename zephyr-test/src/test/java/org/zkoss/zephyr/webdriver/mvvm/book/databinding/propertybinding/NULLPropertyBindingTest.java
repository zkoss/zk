package org.zkoss.zephyr.webdriver.mvvm.book.databinding.propertybinding;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogType;

import org.zkoss.test.webdriver.WebDriverTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

/**
 * @author jameschu
 */
public class NULLPropertyBindingTest extends WebDriverTestCase {
	@Test
	public void testNULL() {
		connect();
		assertEquals("test", jq("$l1").text());
		assertEquals("", jq("$l2").text());
		assertEquals("", jq("$l3").text());
		assertEquals("", jq("$l4").text());
		assertEquals("", jq("$l5").text());
	}

	@Test
	public void testUndefined1() {
		connect("/mvvm/book/databinding/propertybinding/NULLPropertyBinding-1.zul");
		assertFalse(driver.manage().logs().get(LogType.BROWSER).getAll().stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue()).findFirst()
				.isEmpty());
	}

	@Test
	public void testUndefined2() {
		connect("/mvvm/book/databinding/propertybinding/NULLPropertyBinding-2.zul");
		assertFalse(driver.manage().logs().get(LogType.BROWSER).getAll().stream()
				.filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue()).findFirst()
				.isEmpty());
	}
}
