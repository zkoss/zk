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

public class B01795NestedTemplateTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		List<ComponentAgent> box1s = desktop.queryAll("grid label");
		assertEquals("[AJAX]", box1s.get(0).as(Label.class).getValue());
		assertEquals("[AJAX]", box1s.get(1).as(Label.class).getValue());
		assertEquals("[AJAX]", box1s.get(2).as(Label.class).getValue());
		assertEquals("[Java, C]", box1s.get(3).as(Label.class).getValue());
		assertEquals("[Java, C]", box1s.get(4).as(Label.class).getValue());
		assertEquals("[Java, C]", box1s.get(5).as(Label.class).getValue());
	}
}
