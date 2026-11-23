package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_5076Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Select select = new Select(toElement(jq("$lb")));
		select.selectByVisibleText("item 2");
		waitResponse();
		select.selectByVisibleText("item 1");
		waitResponse();
		assertEquals(2, getZKLog().split("\n").length);
	}
}
