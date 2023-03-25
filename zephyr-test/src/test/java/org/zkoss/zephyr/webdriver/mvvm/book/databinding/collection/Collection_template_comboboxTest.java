package org.zkoss.zephyr.webdriver.mvvm.book.databinding.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class Collection_template_comboboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/databinding/collection/collection-template-combobox.zul");
		Widget outerbox = jq("$outergrid").toWidget();
		Widget outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		String[] itemLabel = {"A", "B", "C", "D"};
		assertEquals(4, jq(outerbox).find("@rows").toWidget().nChildren());
		Widget outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			Widget combobox = jq(outerrow).find("@combobox").toWidget();
			eval(combobox + ".open()"); // to show popu first so we can find comboitem in zkmax
			waitResponse(true);
			JQuery comboitems = jq(combobox).find("@comboitem");
			assertEquals(4, comboitems.length());
			for (int j = 0; j <= 1; j++) {
				Widget comboitem = comboitems.eq(j).toWidget();
				if (j == 0 || j == 2) {
					assertEquals("Model1-" + itemLabel[i] + " " + j + "-" + j + "-" + i, comboitem.get("label"));
				} else {
					assertEquals("Model2-" + itemLabel[i] + " " + j + "-" + j + "-" + i, comboitem.get("label"));
				}
				assertEquals(itemLabel[i] + " " + j, comboitem.get("description"));
			}
			Widget btn = jq(outerrow).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse(true);
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
	}
}