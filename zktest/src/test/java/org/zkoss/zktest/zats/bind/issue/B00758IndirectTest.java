package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00758IndirectTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent grid = desktop.query("#grid"); //two window element is forbidden!!
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent btn2 = desktop.query("#btn2");
		List<ComponentAgent> rows = grid.queryAll("row");
		
		assertEquals("First0", rows.get(0).queryAll("textbox").get(0).as(Textbox.class).getValue());
		assertEquals("Last0", rows.get(0).queryAll("textbox").get(1).as(Textbox.class).getValue());
		assertEquals("First0 Last0", rows.get(0).query("label").as(Label.class).getValue());
		
		btn1.click();
		rows = grid.queryAll("row");
		assertEquals("Tom", rows.get(0).queryAll("textbox").get(0).as(Textbox.class).getValue());
		assertEquals("Last0", rows.get(0).queryAll("textbox").get(1).as(Textbox.class).getValue());
		assertEquals("Tom Last0", rows.get(0).query("label").as(Label.class).getValue());
		
		btn2.click();
		rows = grid.queryAll("row");
		assertEquals("Henri", rows.get(0).queryAll("textbox").get(0).as(Textbox.class).getValue());
		assertEquals("Chen", rows.get(0).queryAll("textbox").get(1).as(Textbox.class).getValue());
		assertEquals("Henri Chen", rows.get(0).query("label").as(Label.class).getValue());
	}
}
