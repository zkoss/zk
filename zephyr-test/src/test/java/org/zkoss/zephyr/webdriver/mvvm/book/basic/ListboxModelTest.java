package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class ListboxModelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/listboxmodel.zul");
		waitResponse();
		JQuery outerbox = jq("$outerbox");
		// =================================delete 2rd row
		Widget outeritems = outerbox.toWidget().firstChild();// include header
		outeritems = outeritems.nextSibling(); // don't care header
		Widget outeritem = outeritems.nextSibling();
		click(jq(outeritem).find("@button").eq(1).toWidget()); // click the delete button on 2nd row
		waitResponse();
		outerbox = jq("$outerbox");
		outeritems = outerbox.toWidget().firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		String[] itemLabel = {"A", "C", "D"};
		assertEquals(3, outerbox.toWidget().nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			JQuery innerbox = jq(outeritem).find("@listbox");
			assertTrue(innerbox.toWidget().exists());
			JQuery inneritems = jq(innerbox).find("@listitem");
			assertEquals(2, inneritems.length());
			for (int j = 0; j <= 1; j++) {
				Widget inneritem = inneritems.eq(j).toWidget();
				cell = inneritem.firstChild();
				assertEquals("" + j, cell.get("label"));
				cell = cell.nextSibling();
				assertEquals("" + i, cell.get("label"));
				String innerl = itemLabel[i] + " " + j;
				cell = cell.nextSibling();
				assertEquals(innerl, cell.get("label"));
			}
			cell = outeritem.lastChild();
			JQuery btn = jq(cell).find("@button");// index button
			click(btn.toWidget());
			waitResponse();
			JQuery msg = jq("$msg");
			assertEquals("item index " + i, msg.toWidget().get("value"));
			outeritem = outeritem.nextSibling();
		}
		// ===============================add after row
		outeritems = outerbox.toWidget().firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		outeritem = outeritems.nextSibling();
		click(jq(outeritem).find("@button").eq(2).toWidget()); // click the add after button on 2nd row
		waitResponse();
		outerbox = jq("$outerbox");
		outeritems = outerbox.toWidget().firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		itemLabel = new String[]{"A", "C", "C1", "D"};
		assertEquals(4, outerbox.toWidget().nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			JQuery innerbox = jq(outeritem).find("@listbox");
			assertTrue(innerbox.toWidget().exists());
			JQuery inneritems = jq(innerbox).find("@listitem");
			assertEquals(2, inneritems.length());

			for (int j = 0; j <= 1; j++) {
				Widget inneritem = inneritems.eq(j).toWidget();
				cell = inneritem.firstChild();
				assertEquals("" + j, cell.get("label"));
				cell = cell.nextSibling();
				assertEquals("" + i, cell.get("label"));
				String innerl = itemLabel[i] + " " + j;
				cell = cell.nextSibling();
				assertEquals(innerl, cell.get("label"));
			}
			cell = outeritem.lastChild();
			JQuery btn = jq(cell).find("@button");// index button
			click(btn.toWidget());
			waitResponse();
			JQuery msg = jq("$msg");
			assertEquals("item index " + i, msg.toWidget().get("value"));
			outeritem = outeritem.nextSibling();
		}
		// ===============================add add before row
		outeritems = outerbox.toWidget().firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		outeritem = outeritems.nextSibling().nextSibling();
		click(jq(outeritem).find("@button").eq(3).toWidget()); // click the add before button on 2nd row
		waitResponse();
		outerbox = jq("$outerbox");
		outeritems = outerbox.toWidget().firstChild(); // include header
		outeritems = outeritems.nextSibling(); // don't care header
		itemLabel = new String[]{"A", "C", "C12", "C1", "D"};
		assertEquals(5, outerbox.toWidget().nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			JQuery innerbox = jq(outeritem).find("@listbox");
			assertTrue(innerbox.toWidget().exists());
			JQuery inneritems = jq(innerbox).find("@listitem");
			assertEquals(2, inneritems.length());
			for (int j = 0; j <= 1; j++) {
				Widget inneritem = inneritems.eq(j).toWidget();
				cell = inneritem.firstChild();
				assertEquals("" + j, cell.get("label"));
				cell = cell.nextSibling();
				assertEquals("" + i, cell.get("label"));
				String innerl = itemLabel[i] + " " + j;
				cell = cell.nextSibling();
				assertEquals(innerl, cell.get("label"));
			}
			cell = outeritem.lastChild();
			JQuery btn = jq(cell).find("@button");// index button
			click(btn.toWidget());
			waitResponse();
			JQuery msg = jq("$msg");
			assertEquals("item index " + i, msg.toWidget().get("value"));
			outeritem = outeritem.nextSibling();
		}
	}
}
