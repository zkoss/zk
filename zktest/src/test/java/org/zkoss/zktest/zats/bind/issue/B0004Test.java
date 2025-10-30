package org.zkoss.zktest.zats.bind.issue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
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
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		ComponentAgent btn1 = desktop.query("#btn1");
		btn1.click();
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("value 1 have to large than 10", msg1.as(Label.class).getValue());
		Assertions.assertEquals("", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		ComponentAgent t21 = desktop.query("#t21");
		t21.type("32");
		//t21.blur(); // no need to blur?
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		btn1.click();
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("value 2 is not valid For input string: \"\"", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		ComponentAgent t22 = desktop.query("#t22");
		t22.type("13");
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("value 2 have to large than 20", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		t22.type("22");
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		btn1.click();
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("value 2 have to large than value 1", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		t22.type("42");
		Assertions.assertEquals("0", l11.as(Label.class).getValue());
		Assertions.assertEquals("", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("", msg2.as(Label.class).getValue());
		Assertions.assertEquals("", msg3.as(Label.class).getValue());

		btn1.click();
		Assertions.assertEquals("32", l11.as(Label.class).getValue());
		Assertions.assertEquals("42", l12.as(Label.class).getValue());
		Assertions.assertEquals("", msg1.as(Label.class).getValue());
		Assertions.assertEquals("", msg2.as(Label.class).getValue());
		Assertions.assertEquals("execute command 1", msg3.as(Label.class).getValue());
	}
}
