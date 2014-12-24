package org.zkoss.zktest.zats.test2;

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
import org.zkoss.zul.Label;

public class B0004Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
        
        //the following five are labels
        ComponentAgent l11 = desktop.query("#l11");
        ComponentAgent l12 = desktop.query("#l12");
        ComponentAgent msg1 = desktop.query("#msg1");
        ComponentAgent msg2 = desktop.query("#msg2");
        ComponentAgent msg3 = desktop.query("#msg3");
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		ComponentAgent btn1 = desktop.query("#btn1");
		btn1.click();
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("value 1 have to large than 10", msg1.as(Label.class).getValue());
		Assert.assertEquals("", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		ComponentAgent t21 = desktop.query("#t21");
		t21.type("32");
		//t21.blur(); // no need to blur?
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		btn1.click();
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("value 2 is not valid For input string: \"\"", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		ComponentAgent t22 = desktop.query("#t22");
		t22.type("13");
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("value 2 have to large than 20", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		t22.type("22");
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		btn1.click();
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("value 2 have to large than value 1", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		t22.type("42");
		Assert.assertEquals("0", l11.as(Label.class).getValue());
		Assert.assertEquals("", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("", msg2.as(Label.class).getValue());
		Assert.assertEquals("", msg3.as(Label.class).getValue());

		btn1.click();
		Assert.assertEquals("32", l11.as(Label.class).getValue());
		Assert.assertEquals("42", l12.as(Label.class).getValue());
		Assert.assertEquals("", msg1.as(Label.class).getValue());
		Assert.assertEquals("", msg2.as(Label.class).getValue());
		Assert.assertEquals("execute command 1", msg3.as(Label.class).getValue());
	}
}
