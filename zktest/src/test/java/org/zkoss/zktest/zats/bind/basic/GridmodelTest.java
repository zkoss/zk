package org.zkoss.zktest.zats.bind.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class GridmodelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/gridmodel.zul");
		JQuery outerbox = jq("$outergrid");
		Widget outerrows = outerbox.find("@rows").toWidget().firstChild();
		// =================================delete 2rd row
		Widget outeritem = outerrows.nextSibling();
		click(jq(outeritem).find("@button").eq(1).toWidget()); // click the delete button on 2nd row
		waitResponse();
		outerbox = jq("$outergrid");
		outerrows = outerbox.find("@rows").toWidget().firstChild();
		String[] itemLabel = {"A", "C", "D"};
		assertEquals(3, outerbox.find("@rows").toWidget().nChildren());
		Widget outerrow = outerrows;

		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
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
			rowkid = outerrow.lastChild();
			Widget btn = jq(rowkid).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
		// =================================add after row
		outeritem = outerrows.nextSibling();
		click(jq(outeritem).find("@button").eq(2).toWidget()); // add after 2nd row
		waitResponse();
		outerbox = jq("$outergrid");
		outerrows = outerbox.find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "C", "C1", "D"};
		assertEquals(4, outerbox.find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
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
			rowkid = outerrow.lastChild();
			Widget btn = jq(rowkid).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
		// =================================add before row
		outeritem = outerrows.nextSibling().nextSibling();
		click(jq(outeritem).find("@button").eq(3).toWidget()); // add before 3nd row
		waitResponse();
		outerbox = jq("$outergrid");
		outerrows = outerbox.find("@rows").toWidget().firstChild();
		itemLabel = new String[]{"A", "C", "C12", "C1", "D"};
		assertEquals(5, outerbox.find("@rows").toWidget().nChildren());
		outerrow = outerrows;
		for (int i = 0, length = itemLabel.length; i < length; i++) {
			String outerl = itemLabel[i];
			Widget rowkid = outerrow.firstChild();
			assertEquals("" + i, rowkid.get("value")); // verify the index on label
			rowkid = rowkid.nextSibling();
			assertEquals(outerl, rowkid.get("value")); // verify the label on label
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
			rowkid = outerrow.lastChild();
			Widget btn = jq(rowkid).find("@button").toWidget();// index button
			Widget msg = jq("$msg").toWidget();
			click(btn);
			waitResponse();
			assertEquals("item index " + i, msg.get("value"));
			outerrow = outerrow.nextSibling();
		}
	}
}
