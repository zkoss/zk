package org.zkoss.zephyr.webdriver.mvvm.book.databinding.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class Collection_template_listboxTest extends WebDriverTestCase {
	@Test
	public void test1() {
		connect("/mvmv/book/databinding/collection/collection-template-listbox.zul");
		JQuery outerbox = jq("$outerbox");
		Widget outeritems = outerbox.toWidget().firstChild();// include header
		outeritems = outeritems.nextSibling(); // don't care header
		String[] itemLabel = {"A", "B", "C", "D"};
		assertEquals(4, outerbox.toWidget().nChildren() - 1);
		var outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			Widget innerbox = jq(outeritem).find("@listbox").toWidget();
			assertTrue(innerbox.exists());
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
			cell = outeritem.lastChild().previousSibling();
			Widget btn = jq(cell).find("@button").toWidget(); // index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			// verify template
			cell = outeritem.lastChild();
			if (i == 0 || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
			outeritem = outeritem.nextSibling();
		}
	}

	@Test
	public void test2() {
		connect("/mvmv/book/databinding/collection/collection-template-listbox.zul");
		Widget outerbox = jq("$outerbox").toWidget();
		Widget outeritems = outerbox.firstChild(); // include header
		// =================================delete 2rd row
		Widget outeritem = outeritems.nextSibling().nextSibling(); // header -> 0 -> 1
		click(jq(outeritem).find("@button").eq(1)); // click the delete button on 2nd row
		waitResponse();
		outerbox = jq("$outerbox").toWidget();
		outeritems = outerbox.firstChild(); // include header
		String[] itemLabel = {"A", "C", "D"};
		assertEquals(3, outerbox.nChildren() - 1);
		outeritem = outeritems;

		for (int i = 0, length = itemLabel.length; i < length; i++) {
			outeritem = outeritem.nextSibling();
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			Widget innerbox = jq(outeritem).find("@listbox").toWidget();
			assertTrue(innerbox.exists());
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
			cell = outeritem.lastChild().previousSibling();
			Widget btn = jq(cell).find("@button").toWidget(); // index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			// verify template
			cell = outeritem.lastChild();
			if (i == 0 || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
		}
		// ===============================add after row
		outeritems = outerbox.firstChild();
		outeritem = outeritems.nextSibling().nextSibling(); // header -> 0 -> 1
		click(jq(outeritem).find("@button").eq(2)); // click the add after button on 2nd row
		waitResponse();
		outerbox = jq("$outerbox").toWidget();
		outeritems = outerbox.firstChild(); // include header
		itemLabel = new String[]{"A", "C", "C1", "D"};
		assertEquals(4, outerbox.nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			outeritem = outeritem.nextSibling();
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			// verify template
			cell = outeritem.lastChild();
			if (i == 0 || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
		}
		// ===============================add add before row
		outeritems = outerbox.firstChild();
		outeritem = outeritems.nextSibling().nextSibling().nextSibling(); // header -> 0 -> 1 -> 2
		click(jq(outeritem).find("@button").eq(3)); // click the add before button on 2nd row
		waitResponse();
		outerbox = jq("$outerbox").toWidget();
		outeritems = outerbox.firstChild(); // include header
		itemLabel = new String[]{"A", "C", "C12", "C1", "D"};
		assertEquals(5, outerbox.nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			outeritem = outeritem.nextSibling();
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			// verify template
			cell = outeritem.lastChild();
			if (i == 0 || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
		}
	}

	@Test
	public void test3() {
		connect("/mvmv/book/databinding/collection/collection-template-listbox.zul");
		Widget outerbox = jq("$outerbox").toWidget();
		Widget outeritems = outerbox.firstChild(); // include header
		String[] itemLabel = {"A", "B", "C", "D"};
		assertEquals(4, outerbox.nChildren() - 1);
		Widget outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			outeritem = outeritem.nextSibling();
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			// verify template
			cell = outeritem.lastChild();
			if (i == 0 || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
		}
		// ==============================================
		click(jq(".z-button:contains(change1)"));
		waitResponse();
		outerbox = jq("$outerbox").toWidget();
		outeritems = outerbox.firstChild(); // include header
		itemLabel = new String[]{"X", "A", "C", "D"};
		assertEquals(4, outerbox.nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			outeritem = outeritem.nextSibling();
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			// verify template
			cell = outeritem.lastChild();
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
		}
		// ==============================================
		click(jq(".z-button:contains(change2)"));
		waitResponse();
		outerbox = jq("$outerbox").toWidget();
		outeritems = outerbox.firstChild(); // include header
		itemLabel = new String[]{"A", "B", "C", "D"};
		assertEquals(4, outerbox.nChildren() - 1);
		outeritem = outeritems;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			outeritem = outeritem.nextSibling();
			String outerl = itemLabel[i];
			Widget cell = outeritem.firstChild();
			assertEquals("" + i, cell.get("label")); // verify the index
			cell = cell.nextSibling();
			assertEquals(outerl, cell.get("label")); // verify the label
			// verify template
			cell = outeritem.lastChild();
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", cell.get("label"));
			} else {
				assertEquals("Model2", cell.get("label"));
			}
		}
	}
}