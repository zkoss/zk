package org.zkoss.zktest.zats;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zul.Label;

public class B00632Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
        
        ComponentAgent lb1 = desktop.query("#lb1");
        ComponentAgent lb2 = desktop.query("#lb2");
        ComponentAgent lb3 = desktop.query("#lb3");
        ComponentAgent lb4 = desktop.query("#lb4");
        ComponentAgent l11 = desktop.query("#l11");
        ComponentAgent l12 = desktop.query("#l12");
        ComponentAgent t11 = desktop.query("#t11");
        
        assertEquals("XYZ", lb1.as(Label.class).getValue());
        assertEquals("XYZ", lb2.as(Label.class).getValue());
        assertEquals("XYZ", lb3.as(Label.class).getValue());
        assertEquals("XYZ", lb4.as(Label.class).getValue());
        assertEquals("A", l11.as(Label.class).getValue());
        assertEquals("B", l12.as(Label.class).getValue());
        
        t11.type("C");
        assertEquals("C", l11.as(Label.class).getValue());
        assertEquals("by-C", l12.as(Label.class).getValue());
	}
}
