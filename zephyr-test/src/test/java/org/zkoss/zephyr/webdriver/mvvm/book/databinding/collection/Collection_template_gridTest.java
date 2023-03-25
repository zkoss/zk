package org.zkoss.zephyr.webdriver.mvvm.book.databinding.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class Collection_template_gridTest extends WebDriverTestCase {
	@Test
	public void test1() {
		connect("/mvvm/book/databinding/collection/collection-template-grid.zul");
		JQuery outerbox = jq("$outergrid");
		Widget outerrows = outerbox.find("@rows").toWidget().firstChild();
		String[] itemLabel = {"A", "B", "C", "D"};
		assertEquals(4, outerbox.find("@rows").toWidget().nChildren());
		var outerrow = outerrows;

		for (int i = 0; i <= 3; i++) {
			var outerl = itemLabel[i];
			var rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			// verify inner index
			var innergrid = rowkid.nextSibling();
			assertTrue(innergrid.exists());
			var innerrows = jq(innergrid).find("@row");
			assertEquals(2, innerrows.length());
			for (int j = 0; j <= 1; j++) {
				var innerrow = innerrows.eq(j).toWidget();
				rowkid = innerrow.firstChild();
				assertEquals("" + j, rowkid.get("value"));
				rowkid = rowkid.nextSibling();
				assertEquals("" + i, rowkid.get("value"));
				var innerl = itemLabel[i] + " " + j;
				rowkid = rowkid.nextSibling();
				assertEquals(innerl, rowkid.get("value"));
			}
			rowkid = outerrow.lastChild().previousSibling();
			var btn = jq(rowkid).find("@button").toWidget(); // index button
			var msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			// verify template
			rowkid = outerrow.lastChild();
			var label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
	}

	@Test
	public void test2() {
		connect("/mvvm/book/databinding/collection/collection-template-grid.zul");
		Widget outerbox = jq("$outergrid").toWidget();
		Widget outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		// =================================delete 2rd row
		Widget outeritem = outerrows.nextSibling(); // 1
		click(jq(outeritem).find("@button").eq(1)); // click the delete button on 2nd row
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		String[] itemLabel = {"A", "C", "D"};
		assertEquals(3, jq(outerbox).find("@rows").toWidget().nChildren());
		Widget outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			// verify inner index
			Widget innergrid = rowkid.nextSibling();
			assertTrue(innergrid.exists());
			JQuery innerrows = jq(innergrid).find("@row");
			assertEquals(2, innerrows.length());
			for (int j = 0; j <= 1; j++) {
				Widget innerrow = innerrows.eq(j).toWidget();
				rowkid = innerrow.firstChild();
				assertEquals("" + j, rowkid.get("value"));
				rowkid = rowkid.nextSibling();
				assertEquals("" + i, rowkid.get("value"));
				String innerl = itemLabel[i] + " " + j;
				rowkid = rowkid.nextSibling();
				assertEquals(innerl, rowkid.get("value"));
			}
			rowkid = outerrow.lastChild().previousSibling();
			Widget btn = jq(rowkid).find("@button").toWidget(); // index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			// verify template
			rowkid = outerrow.lastChild();
			Widget label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
		// =================================add after row
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		outeritem = outerrows.nextSibling(); // 1
		click(jq(outeritem).find("@button").eq(2).toWidget()); // add after 2nd row
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "C", "C1", "D"};
		assertEquals(4, jq(outerbox).find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			rowkid = outerrow.lastChild();
			Widget label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
		// =================================add after row
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		outeritem = outerrows.nextSibling().nextSibling(); // 2
		click(jq(outeritem).find("@button").eq(3).toWidget()); // add after 2nd row
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "C", "C12", "C1", "D"};
		assertEquals(5, jq(outerbox).find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			rowkid = outerrow.lastChild();
			Widget label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
	}

	@Test
	public void test3() {
		connect("/mvvm/book/databinding/collection/collection-template-grid.zul");
		Widget outerbox = jq("$outergrid").toWidget();
		Widget outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		String[] itemLabel = {"A", "B", "C", "D"};
		assertEquals(4, jq(outerbox).find("@rows").toWidget().nChildren());
		Widget outerrow = outerrows;

		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			// verify template
			rowkid = outerrow.lastChild();
			Widget label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
		// ===========================================
		click(jq(".z-button:contains(change1)"));
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"X", "A", "C", "D"};
		assertEquals(4, jq(outerbox).find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			// verify template
			rowkid = outerrow.lastChild();
			Widget label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
		// ===========================================
		click(jq(".z-button:contains(change2)"));
		waitResponse();
		outerbox = jq("$outergrid").toWidget();
		outerrows = jq(outerbox).find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "B", "C", "D"};
		assertEquals(4, jq(outerbox).find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
			// verify template
			rowkid = outerrow.lastChild();
			Widget label = jq(rowkid).find("@label").toWidget(); // index button
			if (outerl.equals("A") || i == 2) {
				assertEquals("Model1", label.get("value"));
			} else {
				assertEquals("Model2", label.get("value"));
			}
			outerrow = outerrow.nextSibling();
		}
	}
}