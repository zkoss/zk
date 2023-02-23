package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class ListboxModelSelectionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/listboxmodel.zul");
		Widget msg = jq("$msg").toWidget();
		Widget outerbox = jq("$outerbox").toWidget();
		Widget outeritems = outerbox.firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		Widget outeritem = outeritems.nextSibling(); // select 2nd
		clickAt(outeritem.firstChild(), 2, 2); // click on listitem is not work if it has listbox inside, (it will click on the inside listbox);
		waitResponse();
		assertEquals("1", jq(outeritem.firstChild()).text());
		assertEquals("", msg.get("value"));
		click(jq("$btn1").toWidget());
		waitResponse();
		outeritems = outerbox.firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		outeritem = outeritems.nextSibling(); // select 2nd
		assertEquals(outeritem.uuid(), jq(outerbox).find(".z-listitem-selected").toWidget().uuid());
		assertEquals("reloaded", msg.get("value"));
	}

	protected boolean isHeadless() {
		return false;
	}
}
