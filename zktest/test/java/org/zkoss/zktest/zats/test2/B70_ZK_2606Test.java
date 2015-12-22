package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tree;
import org.zkoss.zul.ext.TreeOpenableModel;

public class B70_ZK_2606Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent btn1 = desktop.query("#win1 #btn1");
		ComponentAgent btn2 = desktop.query("#win1 #btn2");
		ComponentAgent btn3 = desktop.query("#win1 #btn3");
		ComponentAgent btn4 = desktop.query("#win1 #btn4");
		ComponentAgent lb = desktop.query("#win1 #lb");
		
		btn2.click();
		assertEquals(true, lb.getLastChild().as(Listitem.class).isSelected());
		btn1.click();
		
		btn3.click();
		assertEquals(true, lb.getLastChild().as(Listitem.class).isSelected());
		btn1.click();
		
		btn4.click();
		assertEquals(true, lb.getLastChild().as(Listitem.class).isSelected());
		btn1.click();
		
		btn2 = desktop.query("#win2 #btn2");
		btn3 = desktop.query("#win2 #btn3");
		btn4 = desktop.query("#win2 #btn4");
		lb = desktop.query("#win2 #lb");
		
		btn2.click();
		assertEquals(true, lb.getLastChild().as(Listitem.class).isSelected());
		
		btn3.click();
		assertEquals(true, lb.getLastChild().as(Listitem.class).isSelected());
		
		btn4.click();
		assertEquals(true, lb.getLastChild().as(Listitem.class).isSelected());
	}
}
