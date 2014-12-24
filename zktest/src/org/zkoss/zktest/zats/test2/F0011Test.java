package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F0011Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date now = new Date();
		String today = sdf.format(now).toString();
		String yesterday = sdf.format(new Date(now.getTime() - 1000 * 60 * 60 * 24));
		String tomorrow = sdf.format(new Date(now.getTime() + 1000 * 60 * 60 * 24));
		ComponentAgent db1 = desktop.query("#db1");
		ComponentAgent lb11 = desktop.query("#lb11");
		ComponentAgent lb12 = desktop.query("#lb12");
		assertEquals(today, db1.as(Datebox.class).getText());
		assertEquals(today, lb11.as(Label.class).getValue());
		assertEquals("", lb12.as(Label.class).getValue());
		
		db1.type(tomorrow);
		assertEquals(tomorrow, db1.as(Datebox.class).getText());
		assertEquals(today, lb11.as(Label.class).getValue());
		assertEquals("date bday1 must small than today", lb12.as(Label.class).getValue());
		
		db1.type(yesterday);
		assertEquals(yesterday, db1.as(Datebox.class).getText());
		assertEquals(yesterday, lb11.as(Label.class).getValue());
		assertEquals("", lb12.as(Label.class).getValue());
		
		ComponentAgent db2 = desktop.query("#db2");
		ComponentAgent lb21 = desktop.query("#lb21");
		ComponentAgent lb22 = desktop.query("#lb22");
		assertEquals("", db2.as(Datebox.class).getText());
		assertEquals("", lb21.as(Label.class).getValue());
		assertEquals("", lb22.as(Label.class).getValue());
		
		db2.type(yesterday);
		assertEquals(yesterday, db2.as(Datebox.class).getText());
		assertEquals("", lb21.as(Label.class).getValue());
		assertEquals("date bday2 must large than today", lb22.as(Label.class).getValue());
		
		db2.type(tomorrow);
		assertEquals(tomorrow, db2.as(Datebox.class).getText());
		assertEquals(tomorrow, lb21.as(Label.class).getValue());
		assertEquals("", lb22.as(Label.class).getValue());
		
		ComponentAgent tb31 = desktop.query("#tb31");
		ComponentAgent tb32 = desktop.query("#tb32");
		ComponentAgent lb31 = desktop.query("#lb31");
		ComponentAgent lb32 = desktop.query("#lb32");
		
		assertEquals("", tb31.as(Textbox.class).getValue());
		assertEquals("", tb32.as(Textbox.class).getValue());
		assertEquals("", lb31.as(Label.class).getValue());
		assertEquals("", lb32.as(Label.class).getValue());
		
		ComponentAgent btn1 = desktop.query("#btn1");
		btn1.click();
		assertEquals("value1 is empty", lb32.as(Label.class).getValue());
		
		tb31.type("abc");
		assertEquals("", lb31.as(Label.class).getValue());
		assertEquals("value1 is empty", lb32.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("value2 must euqlas to value 1", lb32.as(Label.class).getValue());
		
		tb32.type("abc");
		assertEquals("", lb31.as(Label.class).getValue());
		assertEquals("value2 must euqlas to value 1", lb32.as(Label.class).getValue());
		
		btn1.click();
		assertEquals("abc", lb31.as(Label.class).getValue());
		assertEquals("do Command1", lb32.as(Label.class).getValue());

		ComponentAgent tb41 = desktop.query("#tb41");
		ComponentAgent tb42 = desktop.query("#tb42");
		ComponentAgent lb41 = desktop.query("#lb41");
		ComponentAgent lb42 = desktop.query("#lb42");
		
		assertEquals("", tb41.as(Textbox.class).getValue());
		assertEquals("", tb42.as(Textbox.class).getValue());
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("", lb42.as(Label.class).getValue());
		
		ComponentAgent btn2 = desktop.query("#btn2");
		btn2.click();
		assertEquals("value3 is empty", lb42.as(Label.class).getValue());
		
		tb41.type("abc");
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("", lb42.as(Label.class).getValue());
		
		tb41.type("");
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("value3 is empty", lb42.as(Label.class).getValue());
		
		tb41.type("abc");
		btn2.click();
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("value4 is empty", lb42.as(Label.class).getValue());
		
		tb42.type("def");
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("", lb42.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("value4 must euqlas to value 3", lb42.as(Label.class).getValue());
		
		tb42.type("abc");
		assertEquals("", lb41.as(Label.class).getValue());
		assertEquals("", lb42.as(Label.class).getValue());
		
		btn2.click();
		assertEquals("abc", lb41.as(Label.class).getValue());
		assertEquals("do Command2", lb42.as(Label.class).getValue());
		
		ComponentAgent tb51 = desktop.query("#tb51");
		ComponentAgent tb52 = desktop.query("#tb52");
		ComponentAgent lb51 = desktop.query("#lb51");
		ComponentAgent lb52 = desktop.query("#lb52");
		
		assertEquals("abc", tb51.as(Textbox.class).getValue());
		assertEquals("abc", tb52.as(Textbox.class).getValue());
		assertEquals("abc", lb51.as(Label.class).getValue());
		assertEquals("", lb52.as(Label.class).getValue());
		
		tb51.type("");
		tb52.type("");
		ComponentAgent btn3 = desktop.query("#btn3");
		btn3.click();
		assertEquals("do Command3", lb52.as(Label.class).getValue());
		
		tb51.type("abc");
		assertEquals("", lb51.as(Label.class).getValue());
		assertEquals("do Command3", lb52.as(Label.class).getValue());
		
		btn3.click();
		assertEquals("value2 must euqlas to value 1", lb52.as(Label.class).getValue());
		
		tb52.type("def");
		btn3.click();
		assertEquals("", lb51.as(Label.class).getValue());
		assertEquals("value2 must euqlas to value 1", lb52.as(Label.class).getValue());
		
		tb52.type("abc");
		btn3.click();
		assertEquals("abc", lb51.as(Label.class).getValue());
		assertEquals("do Command3", lb52.as(Label.class).getValue());

	}
}
