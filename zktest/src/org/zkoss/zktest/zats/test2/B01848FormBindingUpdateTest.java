package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B01848FormBindingUpdateTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent combobox = desktop.query("#combobox");
		ComponentAgent lb1 = desktop.query("#lb1");
		ComponentAgent lb2 = desktop.query("#lb2");
				
		combobox.type("Item 1");
		assertTrue(lb1.as(Label.class).getValue().contains("org.zkoss.zktest.bind.issue.B01848FormBindingUpdate"));
		assertEquals("Item 1", lb2.as(Label.class).getValue());

	}
}
