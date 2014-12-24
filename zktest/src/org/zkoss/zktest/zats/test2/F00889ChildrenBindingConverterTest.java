package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Selectbox;

public class F00889ChildrenBindingConverterTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent  item1 = desktop.query("#item1");
		ComponentAgent set1 = desktop.query("#set1");
		ComponentAgent list1 = desktop.query("#list1");
		ComponentAgent array1 = desktop.query("#array1");
		ComponentAgent enum1 = desktop.query("#enum1");
		ComponentAgent  item2 = desktop.query("#item2");
		ComponentAgent set2 = desktop.query("#set2");
		ComponentAgent list2 = desktop.query("#list2");
		ComponentAgent array2 = desktop.query("#array2");
		ComponentAgent enum2 = desktop.query("#enum2");
		
		List<ComponentAgent> ls = item1.queryAll("label");
		assertEquals(1, ls.size());
		assertEquals("A", ls.get(0).as(Label.class).getValue());
		
		ls = set1.queryAll("label");
		assertEquals(3, ls.size());
		
		ls = list1.queryAll("label");
		assertEquals(3, ls.size());
		assertEquals("A", ls.get(0).as(Label.class).getValue());
		assertEquals("B", ls.get(1).as(Label.class).getValue());
		assertEquals("C", ls.get(2).as(Label.class).getValue());
		
		ls = array1.queryAll("label");
		assertEquals(3, ls.size());
		assertEquals("A", ls.get(0).as(Label.class).getValue());
		assertEquals("B", ls.get(1).as(Label.class).getValue());
		assertEquals("C", ls.get(2).as(Label.class).getValue());
		
		ls = enum1.queryAll("label");
		assertEquals(3, ls.size());
		assertEquals("A", ls.get(0).as(Label.class).getValue());
		assertEquals("B", ls.get(1).as(Label.class).getValue());
		assertEquals("C", ls.get(2).as(Label.class).getValue());
		
		ls = item2.queryAll("label");
		assertEquals(1, ls.size());
		assertEquals("D", ls.get(0).as(Label.class).getValue());
		
		ls = set2.queryAll("label");
		assertEquals(3, ls.size());
		
		ls = list2.queryAll("label");
		assertEquals(3, ls.size());
		assertEquals("D", ls.get(0).as(Label.class).getValue());
		assertEquals("E", ls.get(1).as(Label.class).getValue());
		assertEquals("F", ls.get(2).as(Label.class).getValue());
		
		ls = array2.queryAll("label");
		assertEquals(3, ls.size());
		assertEquals("D", ls.get(0).as(Label.class).getValue());
		assertEquals("E", ls.get(1).as(Label.class).getValue());
		assertEquals("F", ls.get(2).as(Label.class).getValue());
		
		ls = enum2.queryAll("label");
		assertEquals(3, ls.size());
		assertEquals("D", ls.get(0).as(Label.class).getValue());
		assertEquals("E", ls.get(1).as(Label.class).getValue());
		assertEquals("F", ls.get(2).as(Label.class).getValue());
	}
}
