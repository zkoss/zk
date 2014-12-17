package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class F00769Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent msg = desktop.query("#msg");
		ComponentAgent selected = desktop.query("#selected");
		ComponentAgent clean1 = desktop.query("#clean1");
		ComponentAgent clean2 = desktop.query("#clean2");
		ComponentAgent select = desktop.query("#select");
		ComponentAgent reload = desktop.query("#reload");
		ComponentAgent select0 = desktop.query("#select0");
		ComponentAgent select1 = desktop.query("#select1");
		ComponentAgent showselect = desktop.query("#showselect");
		
		desktop.query("#A-0-1").as(MultipleSelectAgent.class).select();
		assertEquals("[A-0-1]", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("[[0, 1]]", msg.as(Label.class).getValue());
		
		desktop.query("#A-1-0-1").as(MultipleSelectAgent.class).select();
		assertEquals("[A-0-1, A-1-0-1]", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("[[0, 1], [1, 0, 1]]", msg.as(Label.class).getValue());
		
		clean1.click();
		assertEquals("", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("no selection", msg.as(Label.class).getValue());
		
		select.click();
		assertEquals("[A-0-1, A-1-1-1]", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("[[0, 1], [1, 1, 1]]", msg.as(Label.class).getValue());
		
		desktop.query("#A-1-0-1").as(MultipleSelectAgent.class).select();
		assertEquals("[A-0-1, A-1-0-1, A-1-1-1]", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("[[0, 1], [1, 0, 1], [1, 1, 1]]", msg.as(Label.class).getValue());
		
		clean2.click();
		assertEquals("[]", selected.as(Label.class).getValue());
		showselect.click();
		assertEquals("no selection", msg.as(Label.class).getValue());
		
		select.click();
		select0.click();
		showselect.click();
		assertEquals("[[0, 0], [0, 1], [1, 1, 1]]", msg.as(Label.class).getValue());
		
		reload.click();
		assertEquals("[A-0-1, A-1-1-1]", selected.as(Label.class).getValue());
		assertEquals("reloaded [A-0-1, A-1-1-1]", msg.as(Label.class).getValue());
		showselect.click();
		assertEquals("[[0, 1], [1, 1, 1]]", msg.as(Label.class).getValue());
		
		select1.click();
		showselect.click();
		assertEquals("[[0, 0, 1], [0, 1], [1, 1, 1]]", msg.as(Label.class).getValue());
		
		reload.click();
		assertEquals("[A-0-1, A-1-1-1]", selected.as(Label.class).getValue());
		assertEquals("reloaded [A-0-1, A-1-1-1]", msg.as(Label.class).getValue());
		showselect.click();
		assertEquals("[[0, 1], [1, 1, 1]]", msg.as(Label.class).getValue());
	}
}
