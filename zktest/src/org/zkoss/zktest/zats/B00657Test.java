package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;

public class B00657Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent listbox = desktop.query("#listbox");
		ComponentAgent intbox = desktop.query("#intbox");
		assertEquals(0, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(0, intbox.as(Intbox.class).getValue().intValue());
		
		intbox.as(InputAgent.class).type("1");
		assertEquals(1, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(1, intbox.as(Intbox.class).getValue().intValue());
		
		intbox.as(InputAgent.class).type("2");
		assertEquals(2, listbox.as(Listbox.class).getSelectedIndex());
		assertEquals(2, intbox.as(Intbox.class).getValue().intValue());
	}
}
