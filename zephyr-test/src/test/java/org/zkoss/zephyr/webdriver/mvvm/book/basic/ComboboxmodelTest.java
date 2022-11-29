package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class ComboboxmodelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Widget outerbox = jq("$outergrid").toWidget();
		Widget outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		// =================================delete 2rd row
		Widget outeritem = outerrows.nextSibling();
		click(jq(outeritem).find("@button").eq(1).toWidget()); // click the delete button on 2nd row
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		String[] itemLabel = {"A", "C", "D"};
		assertEquals(3, jq(outerbox).find("@rows").toWidget().nChildren());
		Widget outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			Widget combobox = jq(outerrow).find("@combobox").toWidget();
			eval(combobox + ".open()"); // to show popu first so we can find comboitem in zkmax
			waitResponse();
			JQuery comboitems = jq(combobox).find("@comboitem");
			assertEquals(2, comboitems.length());

			for (int j = 0; j <= 1; j++) {
				Widget comboitem = comboitems.eq(j).toWidget();
				assertEquals(itemLabel[i] + " " + j + "-" + j + "-" + i, comboitem.get("label"));
				assertEquals(itemLabel[i] + " " + j, comboitem.get("description"));
			}
			Widget btn = jq(outerrow).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
		// =================================add after 2rd row
		outeritem = outerrows.nextSibling();
		click(jq(outeritem).find("@button").eq(2).toWidget()); // click the add after button on 2nd row
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "C", "C1", "D"};
		assertEquals(4, jq(outerbox).find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			Widget combobox = jq(outerrow).find("@combobox").toWidget();
			eval(combobox + ".open()"); // to show popu first so we can find comboitem in zkmax
			waitResponse();
			JQuery comboitems = jq(combobox).find("@comboitem");
			assertEquals(2, comboitems.length());
			for (int j = 0; j <= 1; j++) {
				var comboitem = comboitems.eq(j).toWidget();
				assertEquals(itemLabel[i] + " " + j + "-" + j + "-" + i, comboitem.get("label"));
				assertEquals(itemLabel[i] + " " + j, comboitem.get("description"));
			}
			Widget btn = jq(outerrow).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
		// =================================add before 2rd row
		outeritem = outerrows.nextSibling().nextSibling();
		click(jq(outeritem).find("@button").eq(3).toWidget()); // click the add after button on 2nd row
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "C", "C12", "C1", "D"};
		assertEquals(5, jq(outerbox).find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			Widget combobox = jq(outerrow).find("@combobox").toWidget();
			eval(combobox + ".open()"); // to show popu first so we can find comboitem in zkmax
			waitResponse();
			JQuery comboitems = jq(combobox).find("@comboitem");
			assertEquals(2, comboitems.length());
			for (int j = 0; j <= 1; j++) {
				Widget comboitem = comboitems.eq(j).toWidget();
				assertEquals(itemLabel[i] + " " + j + "-" + j + "-" + i, comboitem.get("label"));
				assertEquals(itemLabel[i] + " " + j, comboitem.get("description"));
			}
			Widget btn = jq(outerrow).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
	}
}
