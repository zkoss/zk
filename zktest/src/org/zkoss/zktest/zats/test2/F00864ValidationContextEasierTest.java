package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;

public class F00864ValidationContextEasierTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent msg1 = desktop.query("#msg1");
		ComponentAgent msg2 = desktop.query("#msg2");
		ComponentAgent inp1 = desktop.query("#inp1");
		ComponentAgent inp2 = desktop.query("#inp2");
		ComponentAgent save1 = desktop.query("#save1");
		ComponentAgent err = desktop.query("#err");
		
		assertEquals("", err.as(Label.class).getValue());
		inp1.type("Dennis");
		inp2.type("100");
		save1.click();
		
		assertEquals("", err.as(Label.class).getValue());
		assertEquals("Dennis", msg1.as(Label.class).getValue());
		assertEquals("100", msg2.as(Label.class).getValue());
	}
}
