package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.FocusAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;

public class B00762Combobox2Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent outerbox = desktop.query("#outerbox");
		ComponentAgent min = desktop.query("#min");
		ComponentAgent clean = desktop.query("#clean");
		ComponentAgent select = desktop.query("#select");
		ComponentAgent showselect = desktop.query("#showselect");
		
		outerbox.queryAll("comboitem").get(0).as(SelectAgent.class).select();
		showselect.click();
		assertEquals("0", min.as(Label.class).getValue());
		
		outerbox.queryAll("comboitem").get(2).as(SelectAgent.class).select();
		showselect.click();
		assertEquals("2", min.as(Label.class).getValue());
		
		clean.click();
		assertEquals("", outerbox.as(Combobox.class).getValue());
		showselect.click();
		assertEquals("-1", min.as(Label.class).getValue());
		
		select.click();
		assertEquals("B", outerbox.as(Combobox.class).getValue());
		showselect.click();
		assertEquals("1", min.as(Label.class).getValue());
	}
}
