/* F70_ZK_2089Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 11:56:41 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_2089Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testListbox(jq("@tabpanel:visible @listbox:eq(0)"), true, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(1)"), false, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(2)"), false, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(3)"), false, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(4)"), true, true);
		testListbox(jq("@tabpanel:visible @listbox:eq(5)"), false, true);

		click(jq("@tab:eq(1)"));
		waitResponse();
		testListbox(jq("@tabpanel:visible @listbox:eq(0)"), true, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(1)"), false, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(2)"), false, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(3)"), false, false);
		testListbox(jq("@tabpanel:visible @listbox:eq(4)"), true, true);
		testListbox(jq("@tabpanel:visible @listbox:eq(5)"), false, true);

		click(jq("@tab:eq(2)"));
		waitResponse();
		click(widget("@tabpanel:visible @tree:eq(0) @treerow:eq(1)").$n("open"));
		waitResponse();
		testTree(jq("@tabpanel:visible @tree:eq(0)"), true, false);
		testTree(jq("@tabpanel:visible @tree:eq(1)"), false, false);
		testTree(jq("@tabpanel:visible @tree:eq(2)"), true, true);
		testTree(jq("@tabpanel:visible @tree:eq(3)"), false, true);

		click(jq("@tab:eq(3)"));
		waitResponse();
		testChosenbox(jq("@tabpanel:visible @chosenbox"));
		testSelectbox(jq("@tabpanel:visible @selectbox"));
		testCombobox(jq("@tabpanel:visible @combobox"));
	}

	private void testListbox(JQuery lb, boolean hasModel, boolean isPagingMold) {
		click(lb.find("@listitem:eq(0)"));
		waitResponse();
		getActions().keyDown(Keys.SHIFT)
				.click(toElement(lb.find("@listitem:eq(2)")))
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		click(lb.find("@listitem:eq(0)"));
		waitResponse();
		if (isPagingMold && hasModel) // Spec: https://www.zkoss.org/wiki/ZK_Component_Reference/Events/SelectEvent#Get_the_Unselected_Items
			Assertions.assertEquals("Unselection items: []", jq(widget(lb).nextSibling()).text());
		else
			assertThat(jq(widget(lb).nextSibling()).text(), matchesRegex("Unselection items: \\[<Listitem \\w{5,}>]"));

		if (hasModel)
			Assertions.assertEquals("Unselection objects: [test1]", jq(widget(lb).nextSibling().nextSibling().nextSibling()).text());
		else
			Assertions.assertEquals("Unselection objects: []", jq(widget(lb).nextSibling().nextSibling().nextSibling()).text());
	}

	private void testTree(JQuery tr, boolean hasModel, boolean isPagingMold) {
		click(tr.find("@treerow:eq(0)"));
		waitResponse();
		getActions().keyDown(Keys.SHIFT)
				.click(toElement(tr.find("@treerow:eq(2)")))
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		click(tr.find("@treerow:eq(0)"));
		waitResponse();
		if (isPagingMold && hasModel) // Spec: https://www.zkoss.org/wiki/ZK_Component_Reference/Events/SelectEvent#Get_the_Unselected_Items
			Assertions.assertEquals("Unselection items: []", jq(widget(tr).nextSibling()).text());
		else
			assertThat(jq(widget(tr).nextSibling()).text(), matchesRegex("Unselection items: \\[<Treeitem \\w{5,}>]"));

		if (hasModel)
			Assertions.assertEquals("Unselection objects: [test 1]", jq(widget(tr).nextSibling().nextSibling().nextSibling()).text());
		else
			Assertions.assertEquals("Unselection objects: []", jq(widget(tr).nextSibling().nextSibling().nextSibling()).text());
	}

	private void testChosenbox(JQuery cb) {
		click(cb.find("input"));
		waitResponse(true);
		click(jq(".z-chosenbox-popup .z-chosenbox-option:visible:eq(0)"));
		waitResponse();

		click(jq(".z-chosenbox-item:eq(0) .z-chosenbox-delete"));
		waitResponse();
		Assertions.assertEquals("Unselection items: []", jq(widget(cb).nextSibling()).text());
		Assertions.assertEquals("Unselection objects: [test1]", jq(widget(cb).nextSibling().nextSibling().nextSibling()).text());
	}

	private void testSelectbox(JQuery sb) {
		Select select = new Select(toElement(sb));
		select.selectByVisibleText("test1");
		waitResponse();
		select.selectByVisibleText("test2");
		waitResponse();
		Assertions.assertEquals("Unselection items: []", jq(widget(sb).nextSibling()).text());
		Assertions.assertEquals("Unselection objects: [test1]", jq(widget(sb).nextSibling().nextSibling().nextSibling()).text());
	}

	private void testCombobox(JQuery cb) {
		selectComboitem(cb.toWidget(), 0);
		selectComboitem(cb.toWidget(), 1);
		assertThat(jq(widget(cb).nextSibling()).text(), matchesRegex("Unselection items: \\[<Comboitem \\w{5,}>]"));
		Assertions.assertEquals("Unselection objects: [test1]", jq(widget(cb).nextSibling().nextSibling().nextSibling()).text());
	}
}
