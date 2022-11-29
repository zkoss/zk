package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Tree_selectionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery msg = jq("$msg");
		JQuery selected = jq("$selected");
		JQuery clean = jq("$clean");
		JQuery select = jq("$select");
		JQuery reload = jq("$reload");
		JQuery select0 = jq("$select0");
		JQuery select1 = jq("$select1");
		JQuery showselect = jq("$showselect");
		click(jq("$A-0-1").toWidget().firstChild()); // treeitem->treerow
		waitResponse();
		assertEquals("A-0-1", selected.toWidget().get("value"));
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[0, 1]", msg.toWidget().get("value"));
		click(jq("$A-1-0-1").toWidget().firstChild()); // treeitem->treerow
		waitResponse();
		assertEquals("A-1-0-1", selected.toWidget().get("value"));
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[1, 0, 1]", msg.toWidget().get("value"));
		click(clean.toWidget());
		waitResponse();
		assertEquals("", selected.toWidget().get("value"));
		click(showselect.toWidget());
		waitResponse();
		assertEquals("no selection", msg.toWidget().get("value"));
		click(select.toWidget());
		waitResponse();
		assertEquals("A-1-1-1", selected.toWidget().get("value"));
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[1, 1, 1]", msg.toWidget().get("value"));
		click(select0.toWidget());
		waitResponse();
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[0, 1]", msg.toWidget().get("value"));
		click(reload.toWidget());
		waitResponse();
		assertEquals("A-1-1-1", selected.toWidget().get("value"));
		assertEquals("reloaded A-1-1-1", msg.toWidget().get("value"));
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[1, 1, 1]", msg.toWidget().get("value"));
		click(select1.toWidget());
		waitResponse();
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[0, 1, 1]", msg.toWidget().get("value"));
		click(reload.toWidget());
		waitResponse();
		assertEquals("A-1-1-1", selected.toWidget().get("value"));
		assertEquals("reloaded A-1-1-1", msg.toWidget().get("value"));
		click(showselect.toWidget());
		waitResponse();
		assertEquals("[1, 1, 1]", msg.toWidget().get("value"));
	}
}