package org.zkoss.zktest.zats;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class B01615ChildrenBindingInFormTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> labs1 = desktop.queryAll("#w1 label");
		List<ComponentAgent> labs2 = desktop.queryAll("#w2 label");
		List<ComponentAgent> labs3 = desktop.queryAll("#w3 label");

		assertEquals(3, labs1.size());
		assertEquals(3, labs2.size());
		assertEquals(3, labs3.size());
		
		assertEquals("A", labs1.get(0).as(Label.class).getValue());
		assertEquals("B", labs1.get(1).as(Label.class).getValue());
		assertEquals("C", labs1.get(2).as(Label.class).getValue());
		
		assertEquals("D", labs2.get(0).as(Label.class).getValue());
		assertEquals("E", labs2.get(1).as(Label.class).getValue());
		assertEquals("F", labs2.get(2).as(Label.class).getValue());
		
		assertEquals("X", labs3.get(0).as(Label.class).getValue());
		assertEquals("Y", labs3.get(1).as(Label.class).getValue());
		assertEquals("Z", labs3.get(2).as(Label.class).getValue());
	}
}
