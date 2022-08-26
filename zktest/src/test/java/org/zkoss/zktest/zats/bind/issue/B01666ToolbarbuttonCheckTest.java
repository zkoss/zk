package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01666ToolbarbuttonCheckTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent checkedLab = desktop.query("#checkedLab");
		ComponentAgent messageLab = desktop.query("#messageLab");
		ComponentAgent btn1 = desktop.query("#btn1");
		
		btn1.check(false);
		assertEquals("false", checkedLab.as(Label.class).getValue());
		assertEquals("checked false", messageLab.as(Label.class).getValue());
		
		btn1.check(true);
		assertEquals("true", checkedLab.as(Label.class).getValue());
		assertEquals("checked true", messageLab.as(Label.class).getValue());
	}
}
