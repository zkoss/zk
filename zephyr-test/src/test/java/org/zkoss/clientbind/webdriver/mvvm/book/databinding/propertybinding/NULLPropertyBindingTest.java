package org.zkoss.clientbind.webdriver.mvvm.book.databinding.propertybinding;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author jameschu
 */
public class NULLPropertyBindingTest extends ClientBindTestCase {
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
