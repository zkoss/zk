package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01640CommandSend2WrongViewModelTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		ComponentAgent showChildBtn = desktop.query("#showChildBtn");
		
		showChildBtn.click();
		ComponentAgent lab = desktop.query("#workContent #childWin #lab");
		assertEquals("initialized", lab.as(Label.class).getValue());
		
		ComponentAgent outerBtn = desktop.query("#workContent #childWin #outerBtn");
		outerBtn.click();
		assertEquals("do outerGridCommand", lab.as(Label.class).getValue());
		
		ComponentAgent innerBtn = desktop.query("#workContent #childWin #innerBtn");
		innerBtn.click();
		assertEquals("do innerGridCommand A", lab.as(Label.class).getValue());
	}
}
