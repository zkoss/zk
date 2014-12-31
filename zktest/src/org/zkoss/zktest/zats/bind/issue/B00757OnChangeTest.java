package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

public class B00757OnChangeTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent t1 = desktop.query("#t1");
		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent t2 = desktop.query("#t2");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent t3 = desktop.query("#t3");
		ComponentAgent l31 = desktop.query("#l31");
		ComponentAgent l32 = desktop.query("#l32");
		ComponentAgent t4 = desktop.query("#t4");
		ComponentAgent l4 = desktop.query("#l4");
		
		t1.type("A");
		assertEquals("A-X", l1.as(Label.class).getValue());
		
		t2.type("A");
		assertEquals("null-Y", l2.as(Label.class).getValue());
		
		t2.type("B");
		assertEquals("B-Y", l2.as(Label.class).getValue());
		
		t2.type("C");
		assertEquals("B-Y", l2.as(Label.class).getValue());
		
		t3.type("A");
		assertEquals("A", l31.as(Label.class).getValue());
		assertEquals("", l32.as(Label.class).getValue());
		assertEquals("", l4.as(Label.class).getValue());
		
		t4.type("C");
		assertEquals("A", l31.as(Label.class).getValue());
		assertEquals("", l32.as(Label.class).getValue());
		assertEquals("", l4.as(Label.class).getValue());
		
		t3.type("B");
		assertEquals("B", l31.as(Label.class).getValue());
		assertEquals("B-I", l32.as(Label.class).getValue());
		assertEquals("C-J", l4.as(Label.class).getValue());
	}
}
