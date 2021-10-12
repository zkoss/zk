package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01059DifferentTypeTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent ib1 = desktop.query("#ib1");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent ib2 = desktop.query("#ib2");
		ComponentAgent ib3 = desktop.query("#ib3");
		String exceptionMsg = "";
		try {
			ib1.type("1");
		} catch(Exception e) {
			exceptionMsg = e.getCause().toString();
		}
		assertTrue(exceptionMsg.contains("Property 'value1' not writable"));
		exceptionMsg = "";
		
		ib2.type("2");
		assertEquals("2", lb2.as(Label.class).getValue());
		try {
			ib3.type("3");
		} catch(Exception e) {
			exceptionMsg = e.getCause().toString();
		}
		assertTrue(exceptionMsg.contains("Property 'value3' not writable"));
	}
}
