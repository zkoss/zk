package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

public class B70_ZK_2595Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery hlayout = jq(".z-hlayout");
		JQuery hChildren = hlayout.children();
		int hChildrenSize = hChildren.length();
		int hChildWidth = hlayout.width() / hChildrenSize;
		int index = 0;
		while (index < 3) {
			JQuery t = hChildren.eq(index);
			assertTrue(t.css("width").equals(hChildWidth + "px"));
			index += 1;
		}

		Widget hbox = jq(".z-hbox").toWidget();
		Widget hboxChild = hbox.firstChild();
		int hboxChildWidth = jq(".z-hbox").width() / hbox.nChildren();
		for (int i = 0; i <= 2; i++) {
			String s = "td[id=\"" + hboxChild.uuid() + "-chdex" + "\"]";
			assertTrue(Math.abs(hboxChildWidth - jq(s).width()) < 10);
			hboxChild = hboxChild.nextSibling();
		}

		JQuery vlayout = jq(".z-vlayout");
		JQuery vChildren = vlayout.children();
		int vChildrenSize = vChildren.length();
		Object vChildHeight = vlayout.height() / vChildrenSize;
		index = 0;
		while (index < 3) {
			JQuery t = vChildren.eq(index);
			assertTrue(t.css("height").equals(vChildHeight + "px"));
			index += 1;
		}

		Widget vbox = jq(".z-vbox").toWidget();
		Widget vboxChild = vbox.firstChild();
		int vboxChildHeight = jq(".z-vbox").height() / vbox.nChildren();
		for (int i = 0; i <= 2; i++) {
			String s = "tr[id=\"" + vboxChild.uuid() + "-chdex" + "\"]";
			assertTrue(Math.abs(vboxChildHeight - jq(s).height()) < 10);
			vboxChild = vboxChild.nextSibling();
		}

		click(jq("@button"));
		waitResponse();

		hlayout = jq(".z-hlayout");
		hChildren = hlayout.children();
		hChildrenSize = hChildren.length();
		hChildWidth = hlayout.width() / hChildrenSize;
		index = 0;
		while (index < 4) {
			JQuery t = hChildren.eq(index);
			assertTrue(t.css("width").equals(hChildWidth + "px"));
			index += 1;
		}

		hbox = jq(".z-hbox").toWidget();
		hboxChild = hbox.firstChild();
		hboxChildWidth = jq(".z-hbox").width() / hbox.nChildren();
		for (int i = 0; i <= 3; i++) {
			String s = "td[id=\"" + hboxChild.uuid() + "-chdex" + "\"]";
			assertTrue(Math.abs(hboxChildWidth - jq(s).width()) < 10);
			hboxChild = hboxChild.nextSibling();
		}

		vlayout = jq(".z-vlayout");
		vChildren = vlayout.children();
		vChildrenSize = vChildren.length();
		vChildHeight = vlayout.height() / vChildrenSize;
		while (index < 4) {
			JQuery t = vChildren.eq(index);
			assertTrue(t.css("height").equals(vChildHeight + "px"));
			index += 1;
		}

		vbox = jq(".z-vbox").toWidget();
		vboxChild = vbox.firstChild();
		vboxChildHeight = jq(".z-vbox").height() / vbox.nChildren();
		for (int i = 0; i <= 3; i++) {
			String s = "tr[id=\"" + vboxChild.uuid() + "-chdex" + "\"]";
			assertTrue(Math.abs(vboxChildHeight - jq(s).height()) < 10);
			vboxChild = vboxChild.nextSibling();
		}
	}
}