package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00995ValidationMessagesTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent t1 = desktop.query("#t1");
		ComponentAgent msg11 = desktop.query("#msg11");
		ComponentAgent msg12 = desktop.query("#msg12");
		ComponentAgent msg13 = desktop.query("#msg13");
		ComponentAgent msg21 = desktop.query("#msg21");
		ComponentAgent msg22 = desktop.query("#msg22");
		ComponentAgent msg31 = desktop.query("#msg31");
		ComponentAgent msg32 = desktop.query("#msg32");
		ComponentAgent msg33 = desktop.query("#msg33");
		ComponentAgent msg34 = desktop.query("#msg34");
		ComponentAgent msg41 = desktop.query("#msg41");
		ComponentAgent msg42 = desktop.query("#msg42");
		ComponentAgent msg43 = desktop.query("#msg43");
		ComponentAgent msg44 = desktop.query("#msg44");
		ComponentAgent msggrid1 = desktop.query("#msggrid1");
		ComponentAgent msggrid2 = desktop.query("#msggrid2");
		ComponentAgent msggrid3 = desktop.query("#msggrid3");
		
		assertEquals("ABC", l1.as(Label.class).getValue());
		assertEquals("ABC", t1.as(Textbox.class).getValue());
		assertEquals("", msg11.as(Label.class).getValue());
		assertEquals("", msg12.as(Label.class).getValue());
		assertEquals("", msg13.as(Label.class).getValue());
		assertEquals("", msg21.as(Label.class).getValue());
		assertEquals("", msg22.as(Label.class).getValue());
		assertEquals("true", msg31.as(Label.class).getValue());
		assertEquals("true", msg32.as(Label.class).getValue());
		assertEquals("false", msg33.as(Label.class).getValue());
		assertEquals("false", msg34.as(Label.class).getValue());
		assertEquals("", msg41.as(Label.class).getValue());
		assertEquals("", msg42.as(Label.class).getValue());
		assertEquals("", msg43.as(Label.class).getValue());
		assertEquals("", msg44.as(Label.class).getValue());
		assertEquals(0, msggrid1.queryAll("row").size());
		assertEquals(0, msggrid2.queryAll("row").size());
		assertEquals(0, msggrid3.queryAll("row").size());
		
		t1.type("ABCa");
		assertEquals("ABC", l1.as(Label.class).getValue());
		assertEquals("ABCa", t1.as(Textbox.class).getValue());
		assertEquals("[1] value must equals ignore case 'abc', but is ABCa", msg11.as(Label.class).getValue());
		assertEquals("[1] value must equals ignore case 'abc', but is ABCa", msg12.as(Label.class).getValue());
		assertEquals("[1] value must equals ignore case 'abc', but is ABCa", msg13.as(Label.class).getValue());
		assertEquals("", msg21.as(Label.class).getValue());
		assertEquals("", msg22.as(Label.class).getValue());
		assertEquals("false", msg31.as(Label.class).getValue());
		assertEquals("false", msg32.as(Label.class).getValue());
		assertEquals("true", msg33.as(Label.class).getValue());
		assertEquals("true", msg34.as(Label.class).getValue());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg41.as(Label.class).getValue());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg42.as(Label.class).getValue());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg43.as(Label.class).getValue());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg44.as(Label.class).getValue());
		assertEquals(3, msggrid1.queryAll("row").size());
		assertEquals(3, msggrid2.queryAll("row").size());
		assertEquals(3, msggrid3.queryAll("row").size());
		
		int i = 1;
		List<ComponentAgent> rows = msggrid1.queryAll("row");
		for (int j = 0; j < rows.size(); j++) {
			ComponentAgent row = rows.get(j);
			assertEquals("[" + i + "] value must equals ignore case 'abc', but is ABCa", row.query("label").as(Label.class).getValue());
			i++;
		}
		
		i = 1;
		rows = msggrid2.queryAll("row");
		for (int j = 0; j < rows.size(); j++) {
			ComponentAgent row = rows.get(j);
			assertEquals("[" + i + "] value must equals ignore case 'abc', but is ABCa", row.query("label").as(Label.class).getValue());
			i++;
		}
		
		i = 1;
		rows = msggrid3.queryAll("row");
		for (int j = 0; j < rows.size(); j++) {
			ComponentAgent row = rows.get(j);
			assertEquals("[" + i + "] value must equals ignore case 'abc', but is ABCa", row.query("label").as(Label.class).getValue());
			i++;
		}
	}
}
