package org.zkoss.zktest.zats.bind.issue;

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
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

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
