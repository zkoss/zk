package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B30_1857731Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@toolbarbutton"));
		waitResponse();
		assertFalse(hasError());
	}
}