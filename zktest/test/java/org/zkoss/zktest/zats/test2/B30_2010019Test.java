package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B30_2010019Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery parentsListBox = jq("$parentsListBox");
		JQuery myBtn = jq("$myBtn");
		JQuery driversListbox = jq("$driversListbox");

		click(myBtn);
		waitResponse();
		// First validation.
		String pattern = "name\\d*";
		String nameValue = jq(parentsListBox).val();
		assertTrue(nameValue.matches(pattern));
		assertEquals("C", jq(driversListbox).val());
		click(myBtn);
		waitResponse();
		// Second validation.
		nameValue = jq(parentsListBox).val();
		assertTrue(nameValue.matches(pattern));
		assertEquals("A", jq(driversListbox).val());
		click(myBtn);
		waitResponse();
		// Third validation.
		nameValue = jq(parentsListBox).val();
		assertTrue(nameValue.matches(pattern));
		assertEquals("B", jq(driversListbox).val());
    }
}
