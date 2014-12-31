package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01887DetachAttachTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		assertEquals("./B01887DetachAttachInner1.zul", desktop.query("#inc1").query("#lab1").as(Label.class).getValue());
		assertEquals(null, desktop.query("#inc2"));
		
		desktop.query("#btn2").click();
		assertEquals("./B01887DetachAttachInner2.zul", desktop.query("#inc2").query("#lab2").as(Label.class).getValue());
		assertEquals(null, desktop.query("#inc1"));
		
		desktop.query("#btn1").click();
		assertEquals("./B01887DetachAttachInner1.zul", desktop.query("#inc1").query("#lab1").as(Label.class).getValue());
		assertEquals(null, desktop.query("#inc2"));
	}
}
