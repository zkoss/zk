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

public class B01547NPEWhenCreateNonPageTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent btn2 = desktop.query("#btn2");
		
		try {
			btn1.click();
		} catch(Exception e) {
			assertTrue(e.getCause().toString().contains("UiException: can't find Page to resolve a view model class :'MyVM2'"));
		}
		btn2.click();
		assertEquals("A", desktop.query("#cnt #win #lb").as(Label.class).getValue());
	}
}
