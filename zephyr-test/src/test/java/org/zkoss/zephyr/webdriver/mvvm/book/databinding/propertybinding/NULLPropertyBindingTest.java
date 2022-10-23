package org.zkoss.zephyr.webdriver.mvvm.book.databinding.propertybinding;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author jameschu
 */
public class NULLPropertyBindingTest extends ZephyrClientMVVMTestCase {
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
		assertTrue(hasError());
		assertEquals("Property 'sss' does not exist.", jq(".z-error .messages").text());
	}

	@Test
	public void testUndefined2() {
		connect("/mvvm/book/databinding/propertybinding/NULLPropertyBinding-2.zul");
		assertTrue(hasError());
		assertEquals("Property 'user1' does not exist.", jq(".z-error .messages").text());
	}
}
