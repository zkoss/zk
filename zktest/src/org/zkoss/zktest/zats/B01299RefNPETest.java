package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01299RefNPETest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		desktop.query("#win1 #tba1").type("AA");
		assertEquals("AA", desktop.query("#win1 #lba1").as(Label.class).getValue());
		
		desktop.query("#win2 #tba2").type("BB");
		assertEquals("BB", desktop.query("#win2 #lba2").as(Label.class).getValue());
		
		desktop.query("#win3 #tba3").type("CC");
		assertEquals("CC", desktop.query("#win3 #lba31").as(Label.class).getValue());
		assertEquals("CC", desktop.query("#win3 #lba32").as(Label.class).getValue());
		assertEquals("CC", desktop.query("#win3 #lba33").as(Label.class).getValue());
		assertEquals("CC", desktop.query("#win3 #lba34").as(Label.class).getValue());
		
		desktop.query("#win4 #tbb1").type("D");
		desktop.query("#win4 #btnb1").click();
		assertEquals("D", desktop.query("#win4 #lbb1").as(Label.class).getValue());
		
		desktop.query("#win5 #tbb2").type("E");
		desktop.query("#win5 #btnb2").click();
		assertEquals("E", desktop.query("#win5 #lbb2").as(Label.class).getValue());
		
		desktop.query("#win6 #tbb3").type("F");
		desktop.query("#win6 #btnb3").click();
		assertEquals("F", desktop.query("#win6 #lbb3").as(Label.class).getValue());
		
		desktop.query("#win7 #tbb4").type("G");
		desktop.query("#win7 #btnb4").click();
		assertEquals("G", desktop.query("#win7 #lbb41").as(Label.class).getValue());
		assertEquals("G", desktop.query("#win7 #lbb42").as(Label.class).getValue());
		assertEquals("G", desktop.query("#win7 #lbb43").as(Label.class).getValue());
		assertEquals("G", desktop.query("#win7 #lbb44").as(Label.class).getValue());
		
		desktop.query("#win8 #tbb5").type("H");
		desktop.query("#win8 #btnb5").click();
		assertEquals("H", desktop.query("#win8 #lbb5").as(Label.class).getValue());
		
		desktop.query("#win9 #tbb6").type("I");
		desktop.query("#win9 #btnb6").click();
		assertEquals("I", desktop.query("#win9 #lbb61").as(Label.class).getValue());
		assertEquals("I", desktop.query("#win9 #lbb62").as(Label.class).getValue());
		assertEquals("I", desktop.query("#win9 #lbb63").as(Label.class).getValue());
		assertEquals("I", desktop.query("#win9 #lbb64").as(Label.class).getValue());
	}
}
