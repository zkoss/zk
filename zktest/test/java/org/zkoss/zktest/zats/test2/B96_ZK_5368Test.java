package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_5368Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		final JQuery treeBody = jq(".z-tree-body");
		// Scroll to bottom to get item44 into DOM.
		treeBody.scrollTop(Integer.parseInt(treeBody.toElement().get("scrollHeight")));
		waitResponse();
		// Then, scroll item44 into view.
		eval("jq('[aria-label=\" item-44\"]')[0].scrollIntoView()");
		waitResponse();
		// Click item44 and make sure its label is appended with "-foo".
		click(selectByAriaLabel(" item-44")); // Note the leading space character.
		waitResponse();
		// Note the leading space character and that the aria label has changed.
		final String label = " item-44-foo";
		assertEquals(label, selectByAriaLabel(label).text());
	}

	private JQuery selectByAriaLabel(String ariaLabel) {
		return jq("[aria-label=\"" + ariaLabel + "\"]");
	}
}
