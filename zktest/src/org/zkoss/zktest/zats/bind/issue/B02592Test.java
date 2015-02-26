package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B02592Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
        
        //the following five are labels
        ComponentAgent intbox = desktop.query("intbox");
        ComponentAgent textbox = desktop.query("textbox");
        ComponentAgent button = desktop.query("button");
        ComponentAgent err1 = desktop.query("#err1");
        ComponentAgent err2 = desktop.query("#err2");
		assertEquals("30", String.valueOf(intbox.as(Intbox.class).getValue()));
		assertEquals("Peter", textbox.as(Textbox.class).getValue());
		button.click();
		assertEquals("", err1.as(Label.class).getValue());
		assertEquals("", err2.as(Label.class).getValue());
		intbox.type("300");
		textbox.type("a2");
		button.click();

		assertEquals("must be less than or equal to 120",  err1.as(Label.class).getValue());
		assertEquals("size must be between 4 and 10", err2.as(Label.class).getValue());
	}
}
