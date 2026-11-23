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
		eval("jq('div:contains(\"item-44\")').filter((i, elem) => elem.textContent === \" item-44\")[0].scrollIntoView()");
		waitResponse();
		// Click item44 and make sure its label is appended with "-foo".
		// Note the leading space character.
		eval("jq('div:contains(\"item-44\")').filter((i, elem) => elem.textContent === \" item-44\")[0].click()");
		waitResponse();
		// Note the leading space character and that the aria label has changed.
		final String label = " item-44-foo";
		assertEquals(label, getEval("jq('div:contains(\"" + label + "\")').filter((i, elem) => elem.textContent === \"" + label + "\")[0].textContent"));
	}
}
