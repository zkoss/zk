package org.zkoss.zktest.zats.bind.issue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B00992SubModelTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent combobox = desktop.query("#combobox");
		ComponentAgent lab = desktop.query("#lab");
		
		combobox.as(InputAgent.class).type("9");
		//System.out.println("*********" + combobox.as(Combobox.class).getValue());
		//assertEquals("9", lab.as(Label.class).getValue());

		//can't OpenAgent.open()!!!!!! why!!!!
		combobox.as(InputAgent.class).typing("9");
		ComponentAgent w = combobox.queryAll("comboitem").get(10);
		//assertEquals("99", w.as(Comboitem.class).getLabel());
		
		w.as(SelectAgent.class).select();
		//assertEquals("99", lab.as(Label.class).getValue());

	}
}
