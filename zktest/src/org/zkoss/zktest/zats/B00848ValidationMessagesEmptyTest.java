package org.zkoss.zktest.zats;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class B00848ValidationMessagesEmptyTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l11 = desktop.query("#l11");
		ComponentAgent l12 = desktop.query("#l12");
		ComponentAgent t21 = desktop.query("#t21");
		ComponentAgent t22 = desktop.query("#t22");
		ComponentAgent t31 = desktop.query("#t31");
		ComponentAgent t32 = desktop.query("#t32");
		ComponentAgent m31 = desktop.query("#m31");
		ComponentAgent m32 = desktop.query("#m32");
		ComponentAgent btn1 = desktop.query("#btn1");
		ComponentAgent t41 = desktop.query("#t41");
		ComponentAgent t42 = desktop.query("#t42");
		ComponentAgent m41 = desktop.query("#m41");
		ComponentAgent m42 = desktop.query("#m42");
		ComponentAgent m43 = desktop.query("#m43");
		ComponentAgent m44 = desktop.query("#m44");
		ComponentAgent m45 = desktop.query("#m45");
		ComponentAgent m46 = desktop.query("#m46");
		ComponentAgent btn2 = desktop.query("#btn2");
		ComponentAgent btn3 = desktop.query("#btn3");
		ComponentAgent vmsize = desktop.query("#vmsize");
		ComponentAgent vmempty = desktop.query("#vmempty");
		
		assertEquals("ABC", l11.as(Label.class).getValue());
		assertEquals("10", l12.as(Label.class).getValue());
		
		assertEquals("ABC", t21.as(Textbox.class).getValue());
		assertEquals(10L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("ABC", t31.as(Textbox.class).getValue());
		assertEquals(10L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("0", vmsize.as(Label.class).getValue());
		assertEquals("true", vmempty.as(Label.class).getValue());
		
		t21.type("ABCD");
		t22.type("6");
		assertEquals("ABC", l11.as(Label.class).getValue());
		assertEquals("10", l12.as(Label.class).getValue());
		
		assertEquals("ABCD", t21.as(Textbox.class).getValue());
		assertEquals(6L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("ABC", t31.as(Textbox.class).getValue());
		assertEquals(10L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("2", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		t21.type("Abc");
		t22.type("33");
		assertEquals("Abc", l11.as(Label.class).getValue());
		assertEquals("33", l12.as(Label.class).getValue());
		
		assertEquals("Abc", t21.as(Textbox.class).getValue());
		assertEquals(33L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("Abc", t31.as(Textbox.class).getValue());
		assertEquals(33L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("0", vmsize.as(Label.class).getValue());
		assertEquals("true", vmempty.as(Label.class).getValue());
		
		t31.type("XXX");
		t32.type("1");
		assertEquals("Abc", l11.as(Label.class).getValue());
		assertEquals("33", l12.as(Label.class).getValue());
		
		assertEquals("Abc", t21.as(Textbox.class).getValue());
		assertEquals(33L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("XXX", t31.as(Textbox.class).getValue());
		assertEquals(1L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("0", vmsize.as(Label.class).getValue());
		assertEquals("true", vmempty.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("Abc", l11.as(Label.class).getValue());
		assertEquals("33", l12.as(Label.class).getValue());
		
		assertEquals("Abc", t21.as(Textbox.class).getValue());
		assertEquals(33L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("XXX", t31.as(Textbox.class).getValue());
		assertEquals(1L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 1", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("2", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		t32.type("55");
		assertEquals("Abc", l11.as(Label.class).getValue());
		assertEquals("33", l12.as(Label.class).getValue());
		
		assertEquals("Abc", t21.as(Textbox.class).getValue());
		assertEquals(33L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("XXX", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 1", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("Abc", l11.as(Label.class).getValue());
		assertEquals("33", l12.as(Label.class).getValue());
		
		assertEquals("Abc", t21.as(Textbox.class).getValue());
		assertEquals(33L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("XXX", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("1", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		t31.type("aBC");
		assertEquals("Abc", l11.as(Label.class).getValue());
		assertEquals("33", l12.as(Label.class).getValue());
		
		assertEquals("Abc", t21.as(Textbox.class).getValue());
		assertEquals(33L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is XXX", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("aBC", l11.as(Label.class).getValue());
		assertEquals("55", l12.as(Label.class).getValue());
		
		assertEquals("aBC", t21.as(Textbox.class).getValue());
		assertEquals(55L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("ABC", t41.as(Textbox.class).getValue());
		assertEquals(10L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("0", vmsize.as(Label.class).getValue());
		assertEquals("true", vmempty.as(Label.class).getValue());
		
		t41.type("YYY");
		t42.type("1999");
		assertEquals("aBC", l11.as(Label.class).getValue());
		assertEquals("55", l12.as(Label.class).getValue());
		
		assertEquals("aBC", t21.as(Textbox.class).getValue());
		assertEquals(55L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("YYY", t41.as(Textbox.class).getValue());
		assertEquals(1999L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is YYY", m41.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 1999", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		assertEquals("2", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("aBC", l11.as(Label.class).getValue());
		assertEquals("55", l12.as(Label.class).getValue());
		
		assertEquals("aBC", t21.as(Textbox.class).getValue());
		assertEquals(55L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("YYY", t41.as(Textbox.class).getValue());
		assertEquals(1999L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is YYY", m41.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 1999", m42.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m43.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m44.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m45.as(Label.class).getValue());
		assertEquals("extra validation info ABC", m46.as(Label.class).getValue());
		
		assertEquals("4", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("aBC", l11.as(Label.class).getValue());
		assertEquals("55", l12.as(Label.class).getValue());
		
		assertEquals("aBC", t21.as(Textbox.class).getValue());
		assertEquals(55L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("YYY", t41.as(Textbox.class).getValue());
		assertEquals(1999L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is YYY", m41.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 1999", m42.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m43.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m44.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m45.as(Label.class).getValue());
		assertEquals("extra validation info ABC", m46.as(Label.class).getValue());
		
		assertEquals("4", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		t41.type("abc");
		t42.type("77");
		assertEquals("aBC", l11.as(Label.class).getValue());
		assertEquals("55", l12.as(Label.class).getValue());
		
		assertEquals("aBC", t21.as(Textbox.class).getValue());
		assertEquals(55L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("abc", t41.as(Textbox.class).getValue());
		assertEquals(77L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m43.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m44.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is ABC", m45.as(Label.class).getValue());
		assertEquals("extra validation info ABC", m46.as(Label.class).getValue());
		
		assertEquals("2", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("aBC", l11.as(Label.class).getValue());
		assertEquals("55", l12.as(Label.class).getValue());
		
		assertEquals("aBC", t21.as(Textbox.class).getValue());
		assertEquals(55L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("aBC", t31.as(Textbox.class).getValue());
		assertEquals(55L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("abc", t41.as(Textbox.class).getValue());
		assertEquals(77L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is abc", m43.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is abc", m44.as(Label.class).getValue());
		assertEquals("value must equals 'AbC', but is abc", m45.as(Label.class).getValue());
		assertEquals("extra validation info abc", m46.as(Label.class).getValue());
		
		assertEquals("2", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		t41.type("AbC");
		btn2.click();
		assertEquals("AbC", l11.as(Label.class).getValue());
		assertEquals("77", l12.as(Label.class).getValue());
		
		assertEquals("AbC", t21.as(Textbox.class).getValue());
		assertEquals(77L, t22.as(Intbox.class).getValue().intValue());
		
		assertEquals("AbC", t31.as(Textbox.class).getValue());
		assertEquals(77L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("", m32.as(Label.class).getValue());
		
		assertEquals("AbC", t41.as(Textbox.class).getValue());
		assertEquals(77L, t42.as(Intbox.class).getValue().intValue());
		assertEquals("", m41.as(Label.class).getValue());
		assertEquals("", m42.as(Label.class).getValue());
		assertEquals("", m43.as(Label.class).getValue());
		assertEquals("", m44.as(Label.class).getValue());
		
		t31.type("YYY");
		t32.type("2");
		btn1.click();
		assertEquals("YYY", t31.as(Textbox.class).getValue());
		assertEquals(2L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("value must equals ignore case 'abc', but is YYY", m31.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 2", m32.as(Label.class).getValue());
		
		assertEquals("2", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
		
		btn3.click();
		assertEquals("AbC", t31.as(Textbox.class).getValue());
		assertEquals(2L, t32.as(Intbox.class).getValue().intValue());
		assertEquals("", m31.as(Label.class).getValue());
		assertEquals("value must not < 10 or > 100, but is 2", m32.as(Label.class).getValue());
		
		assertEquals("1", vmsize.as(Label.class).getValue());
		assertEquals("false", vmempty.as(Label.class).getValue());
	}
}
