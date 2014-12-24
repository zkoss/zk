package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.MultipleSelectAgent;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

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
