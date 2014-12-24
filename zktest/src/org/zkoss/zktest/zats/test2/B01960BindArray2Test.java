package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;

public class B01960BindArray2Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent grid = desktop.query("#grid");
		List<ComponentAgent> rows = grid.queryAll("row");
		assertEquals(4, rows.size());

		ComponentAgent tb1 = rows.get(0).query("textbox");
		ComponentAgent lb1 = rows.get(0).query("label");
		ComponentAgent tb2 = rows.get(1).query("textbox");
		ComponentAgent lb2 = rows.get(1).query("label");
		ComponentAgent tb3 = rows.get(2).query("textbox");
		ComponentAgent lb3 = rows.get(2).query("label");
		ComponentAgent tb4 = rows.get(3).query("textbox");
		ComponentAgent lb4 = rows.get(3).query("label");
		
		assertEquals("This", lb1.as(Label.class).getValue());
		assertEquals("is", lb2.as(Label.class).getValue());
		assertEquals("a", lb3.as(Label.class).getValue());
		assertEquals("Test", lb4.as(Label.class).getValue());
		
		tb1.type("yo"); //the original row will be removed and attach with a new one!!!!
		rows = grid.queryAll("row");
		lb1 = rows.get(0).query("label");
		assertEquals("yo", lb1.as(Label.class).getValue());
		
		tb2.type("yoo");
		rows = grid.queryAll("row");
		lb2 = rows.get(1).query("label");
		assertEquals("yoo", lb2.as(Label.class).getValue());
		
		tb3.type("yooo");
		rows = grid.queryAll("row");
		lb3 = rows.get(2).query("label");
		assertEquals("yooo", lb3.as(Label.class).getValue());
		
		tb4.type("yoooo");
		rows = grid.queryAll("row");
		lb4 = rows.get(3).query("label");
		assertEquals("yoooo", lb4.as(Label.class).getValue());
		
	}
}
