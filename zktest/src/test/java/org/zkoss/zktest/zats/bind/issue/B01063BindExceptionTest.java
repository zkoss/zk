package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01063BindExceptionTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent lb2 = desktop.query("#lb2");
		ComponentAgent tb2 = desktop.query("#tb2");
		ComponentAgent tb3 = desktop.query("#tb3");
		tb1.type("1");
		assertEquals("1", lb1.as(Label.class).getValue());
		tb2.type("2");
		assertEquals("2", lb2.as(Label.class).getValue());
		String exceptionMsg = "";
		try {
			tb3.type("3");
		} catch(Exception e) {
			exceptionMsg = e.getCause().getCause().getCause().toString();
		}
		assertTrue(exceptionMsg.contains("Property 'valuex' not found"));
	}
}
