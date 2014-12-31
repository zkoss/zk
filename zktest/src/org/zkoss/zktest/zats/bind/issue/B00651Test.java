package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;

public class B00651Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
        
        ComponentAgent lb1 = desktop.query("#lb1");
        ComponentAgent intbox = desktop.query("#intbox");
        ComponentAgent doublebox = desktop.query("#doublebox");
        
        assertEquals("Non-dirty", lb1.as(Label.class).getValue());
        assertEquals(10, intbox.as(Intbox.class).getValue().intValue());
        assertEquals("10.36", doublebox.as(Doublebox.class).getText());
        
        doublebox.type("36.01");
        assertEquals("Dirty", lb1.as(Label.class).getValue());
        assertEquals("36.01", doublebox.as(Doublebox.class).getText());
	}
}
