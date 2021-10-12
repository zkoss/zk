package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class B01366RadioSelectedItemTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent rg1 = desktop.query("#radiogroup1");
		ComponentAgent rg2 = desktop.query("#radiogroup2");
		ComponentAgent lb1 = desktop.query("#lb1");
		
		rg1.query("radio").check(true);
		assertEquals(true, rg2.queryAll("radio").get(0).as(Radio.class).isChecked());
		assertEquals(false, rg2.queryAll("radio").get(1).as(Radio.class).isChecked());
		assertEquals(false, rg2.queryAll("radio").get(2).as(Radio.class).isChecked());
		assertEquals("name 0", lb1.as(Label.class).getValue());
		
		rg2.queryAll("radio").get(1).check(true);
		assertEquals(false, rg1.queryAll("radio").get(0).as(Radio.class).isChecked());
		assertEquals(true, rg1.queryAll("radio").get(1).as(Radio.class).isChecked());
		assertEquals(false, rg1.queryAll("radio").get(2).as(Radio.class).isChecked());
		assertEquals("name 1", lb1.as(Label.class).getValue());
	}
}
