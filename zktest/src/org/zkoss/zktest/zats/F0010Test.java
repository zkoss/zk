package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

public class F0010Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l0 = desktop.query("#l0");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent t0 = desktop.query("#t0");
		ComponentAgent t1 = desktop.query("#t1");
		ComponentAgent t2 = desktop.query("#t2");
		
		assertEquals("A-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("B-toUI-c1", l1.as(Label.class).getValue());
		assertEquals("C-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("A-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("B-toUI-c1", t1.as(Textbox.class).getValue());
		assertEquals("C-toUI-c2", t2.as(Textbox.class).getValue());
		
		t0.type("I");
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("B-toUI-c1", l1.as(Label.class).getValue());
		assertEquals("C-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("B-toUI-c1", t1.as(Textbox.class).getValue());
		assertEquals("C-toUI-c2", t2.as(Textbox.class).getValue());
		
		t1.type("J");
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("J-toBean-c1-toUI-c1", l1.as(Label.class).getValue());
		assertEquals("C-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("J-toBean-c1-toUI-c1", t1.as(Textbox.class).getValue());
		assertEquals("C-toUI-c2", t2.as(Textbox.class).getValue());
		
		t2.type("K");
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("J-toBean-c1-toUI-c1", l1.as(Label.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("J-toBean-c1-toUI-c1", t1.as(Textbox.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", t2.as(Textbox.class).getValue());
		
		t1.type("X");
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("X-toBean-c1-toUI-c1", l1.as(Label.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("X-toBean-c1-toUI-c1", t1.as(Textbox.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", t2.as(Textbox.class).getValue());
		
		desktop.query("#btn1").click();
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("X-toBean-c1-toUI-c3", l1.as(Label.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("X-toBean-c1-toUI-c3", t1.as(Textbox.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", t2.as(Textbox.class).getValue());
		
		t1.type("X");
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("X-toBean-c3-toUI-c3", l1.as(Label.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("X-toBean-c3-toUI-c3", t1.as(Textbox.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", t2.as(Textbox.class).getValue());
		
		desktop.query("#btn2").click();;
		assertEquals("I-toBean-c0-toUI-c0", l0.as(Label.class).getValue());
		assertEquals("X-toBean-c3-toUI-c4", l1.as(Label.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", l2.as(Label.class).getValue());
		assertEquals("I-toBean-c0-toUI-c0", t0.as(Textbox.class).getValue());
		assertEquals("X-toBean-c3-toUI-c4", t1.as(Textbox.class).getValue());
		assertEquals("K-toBean-c2-toUI-c2", t2.as(Textbox.class).getValue());	
	}
}
