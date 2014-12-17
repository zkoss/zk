package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;

public class F00823RadiogroupModel1Test extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent l1 = desktop.query("#l1");
		ComponentAgent l2 = desktop.query("#l2");
		ComponentAgent box1 = desktop.query("#box1");
		ComponentAgent box2 = desktop.query("#box2");
		ComponentAgent select = desktop.query("#select");
		ComponentAgent clean = desktop.query("#clean");
		
		assertEquals("-1", l1.as(Label.class).getValue());
		assertEquals("", l2.as(Label.class).getValue());
		
		List<ComponentAgent> radios = box1.queryAll("radio");
		radios.get(1).check(true);
		assertEquals("1", l1.as(Label.class).getValue());
		assertEquals("", l2.as(Label.class).getValue());
		radios.get(3).check(true);
		assertEquals("3", l1.as(Label.class).getValue());
		assertEquals("", l2.as(Label.class).getValue());
		
		clean.click();
		assertEquals("-1", l1.as(Label.class).getValue());
		assertEquals("", l2.as(Label.class).getValue());
		
		radios = box2.queryAll("radio");
		radios.get(1).check(true);
		assertEquals("-1", l1.as(Label.class).getValue());
		assertEquals("B", l2.as(Label.class).getValue());
		radios.get(3).check(true);
		assertEquals("-1", l1.as(Label.class).getValue());
		assertEquals("D", l2.as(Label.class).getValue());
		
		clean.click();
		assertEquals("-1", l1.as(Label.class).getValue());
		assertEquals("", l2.as(Label.class).getValue());
		
		select.click();
		assertEquals("0", l1.as(Label.class).getValue());
		assertEquals("A", l2.as(Label.class).getValue());
	}
}
