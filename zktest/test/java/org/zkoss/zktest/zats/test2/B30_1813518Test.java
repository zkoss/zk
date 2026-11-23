package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B30_1813518Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/test2/B30-1813518.zhtml");
		String borderCss = jq("@rows:eq(0) td").eval("get(0).style.border");
		assertTrue("1px solid blue1px solid #0000ffblue 1px solid".contains(borderCss));
		assertTrue("rgb(51, 51, 51)#333333".contains(jq("@rows:eq(0) td").css("color")));
		assertTrue("rgb(227, 235, 246)#e3ebf6".contains(jq("@rows:eq(0) td").css("backgroundColor")));
		assertEquals("", jq("@rows:eq(1) td").eval("get(1).style.border"));
		String color = jq("@rows:eq(1) td").css("color");
		assertTrue("rgba(0, 0, 0, 0.9)#000000".contains(color));
		assertTrue("transparent|rgb(255, 255, 255)|rgba(0, 0, 0, 0)|#ffffff"
				.contains(jq("@rows:eq(1) td").css("backgroundColor")));
	}
}
