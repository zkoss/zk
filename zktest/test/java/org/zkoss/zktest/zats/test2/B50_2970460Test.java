package org.zkoss.zktest.zats.test2;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

import static org.junit.Assert.*;

/**
 * @author jameschu
 */
public class B50_2970460Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		Widget win = jq("$win").toWidget();
		String t = win.get("top");
		String l = win.get("left");
		String h = win.get("height");
		String w = win.get("width");
		win.set("maximized", true);
		waitResponse();
		String curTop = win.get("top");
		String curLeft = win.get("left");
		if (!t.equals(curTop))
			assertTrue((t.equals("null") || t.equals("")) && curTop.equals("0px"));
		if (!l.equals(curLeft))
			assertTrue((l.equals("null") || l.equals("")) && curLeft.equals("0px"));
		assertNotEquals(h, win.get("height"));
		assertNotEquals(w, win.get("width"));
		win.set("maximized", false);
		assertEquals(h, win.get("height"));
		assertEquals(w, win.get("width"));
	}
}