package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class F01416DefaultCommandTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent btn1 = desktop.query("#myWin1 #btn1");
		ComponentAgent btn2 = desktop.query("#myWin1 #btn2");
		ComponentAgent btng1 = desktop.query("#myWin1 #btng1");
		ComponentAgent btng2 = desktop.query("#myWin1 #btng2");
		ComponentAgent lb1 = desktop.query("#myWin1 #lb1");
		ComponentAgent btn3 = desktop.query("#myWin2 #btn3");
		ComponentAgent btn4 = desktop.query("#myWin2 #btn4");
		ComponentAgent btng3 = desktop.query("#myWin2 #btng3");
		ComponentAgent btng4 = desktop.query("#myWin2 #btng4");
		ComponentAgent lb2 = desktop.query("#myWin2 #lb2");

		assertEquals("Dennis", lb1.as(Label.class).getValue());
		assertEquals("Dennis", lb2.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("do command1", lb1.as(Label.class).getValue());
		assertEquals("Dennis", lb2.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("do command cmd2", lb1.as(Label.class).getValue());
		assertEquals("Dennis", lb2.as(Label.class).getValue());
		
		btng1.click();
		assertEquals("do globa-command1", lb1.as(Label.class).getValue());
		assertEquals("do globa-command1", lb2.as(Label.class).getValue());
		
		btng2.click();
		assertEquals("do globa-command gcmd2", lb1.as(Label.class).getValue());
		assertEquals("do globa-command gcmd2", lb2.as(Label.class).getValue());
		
		btn3.click();
		assertEquals("do globa-command gcmd2", lb1.as(Label.class).getValue());
		assertEquals("do command3", lb2.as(Label.class).getValue());
		
		btn4.click();
		assertEquals("do globa-command gcmd2", lb1.as(Label.class).getValue());
		assertEquals("do command cmd4", lb2.as(Label.class).getValue());
		
		btng3.click();
		assertEquals("do globa-command3", lb1.as(Label.class).getValue());
		assertEquals("do globa-command3", lb2.as(Label.class).getValue());
		
		btng4.click();
		assertEquals("do globa-command gcmd4", lb1.as(Label.class).getValue());
		assertEquals("do globa-command gcmd4", lb2.as(Label.class).getValue());
	}
}
