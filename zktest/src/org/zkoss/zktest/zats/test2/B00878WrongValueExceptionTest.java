package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00878WrongValueExceptionTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent msgName = desktop.query("#msgName");
		ComponentAgent msgAge = desktop.query("#msgAge");
		ComponentAgent msgScore = desktop.query("#msgScore");
		ComponentAgent inpName = desktop.query("#inpName");
		ComponentAgent inpAge = desktop.query("#inpAge");
		ComponentAgent inpScore = desktop.query("#inpScore");
		ComponentAgent save = desktop.query("#save");
		
		assertEquals("", msgName.as(Label.class).getValue());
		assertEquals("0", msgAge.as(Label.class).getValue());
		assertEquals("0", msgScore.as(Label.class).getValue());
		
		inpName.type("Chen");
		inpAge.type("3");
		inpScore.type("-1");
		save.click();
		assertEquals("", msgName.as(Label.class).getValue());
		assertEquals("0", msgAge.as(Label.class).getValue());
		assertEquals("0", msgScore.as(Label.class).getValue());
		//should test error msg, but not support
		
		inpName.type("Lin");
		inpAge.type("5");
		inpScore.type("-2");
		save.click();
		assertEquals("", msgName.as(Label.class).getValue());
		assertEquals("0", msgAge.as(Label.class).getValue());
		assertEquals("0", msgScore.as(Label.class).getValue());
		//should test error msg, but not support
		
		inpName.type("Lin");
		inpAge.type("24");
		inpScore.type("-3");
		save.click();
		assertEquals("", msgName.as(Label.class).getValue());
		assertEquals("0", msgAge.as(Label.class).getValue());
		assertEquals("0", msgScore.as(Label.class).getValue());
		//should test error msg, but not support
		
		inpName.type("Lin");
		inpAge.type("24");
		inpScore.type("34");
		save.click();
		assertEquals("Lin", msgName.as(Label.class).getValue());
		assertEquals("24", msgAge.as(Label.class).getValue());
		assertEquals("34", msgScore.as(Label.class).getValue());
		//should test error msg, but not support
		
		/*
		var errorPopup = jq(".z-errorbox")
		
		verifyEquals(3, errorPopup.length())
		
		verifyEquals(2, errorPopup.length())
		
		verifyEquals(1, errorPopup.length())
		
		verifyEquals(0, errorPopup.length())*/
	}
}
