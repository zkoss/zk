package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_2658Test extends WebDriverTestCase {
	private JQuery listboxBody, lastListitem;
	private Iterable<JQuery> top3Listitems;

	@Before
	public void setup() {
		connect();
		waitResponse();
		listboxBody = jq(".z-listbox-body");
		final JQuery listitems = jq("@listitem");
		lastListitem = listitems.last();
		top3Listitems = StreamSupport.stream(listitems.spliterator(), false)
				.limit(3).collect(Collectors.toUnmodifiableList());
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		// Check more than 1 list items nearby at the very top.
		top3Listitems.forEach(this::click);
		waitResponse();

		// Scroll to the bottom then scroll back.
		scrollToBottom();
		scrollToTop();

		// Should find those list items still checked.
		top3Listitems.forEach(this::assertSelected);

		// Scroll to the very bottom and check the last list item.
		scrollToBottom();
		click(lastListitem);
		waitResponse();

		// Scroll to top, and find that previously checked list items are still checked.
		// In ZK-2658, one finds that previously checked list items at the very top are incorrectly unchecked.
		scrollToTop();
		top3Listitems.forEach(this::assertSelected);

		// Scroll to the very bottom, and find that the last list item is still checked.
		// In ZK-2658, this condition doesn't hold.
		scrollToBottom();
		assertSelected(lastListitem);
	}

	void scrollToBottom() {
		listboxBody.scrollTop(Integer.parseInt(listboxBody.toElement().get("scrollHeight")));
		waitResponse();
	}

	void scrollToTop() {
		listboxBody.scrollTop(0);
		waitResponse();
	}

	void assertSelected(JQuery listitem) {
		assertTrue(listitem.hasClass("z-listitem-selected"));
	}
}
