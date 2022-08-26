package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01066IncorrectFormValueTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent tb1 = desktop.query("#tb1");
		ComponentAgent save = desktop.query("#save");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");
		
		assertEquals("A", lb1.as(Label.class).getValue());
		assertEquals("A", lb2.as(Label.class).getValue());
		
		tb1.type("Abc");
		assertEquals("A", lb1.as(Label.class).getValue());
		assertEquals("A", lb2.as(Label.class).getValue());
		
		save.click();
		assertEquals("Abc", lb1.as(Label.class).getValue());
		assertEquals("Abc", lb2.as(Label.class).getValue());
	}
}
