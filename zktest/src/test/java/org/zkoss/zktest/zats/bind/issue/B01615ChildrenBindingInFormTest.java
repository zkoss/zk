package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

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
