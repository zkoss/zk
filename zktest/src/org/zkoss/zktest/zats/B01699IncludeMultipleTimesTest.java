package org.zkoss.zktest.zats;

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
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class B01699IncludeMultipleTimesTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent include = desktop.query("include");
		ComponentAgent btn = desktop.query("#btn");
		String includeSrc = include.as(Include.class).getSrc().substring(1);
		DesktopAgent desktop2 = connect(includeSrc);
		ComponentAgent lab1 = desktop2.query("#lb1");
		ComponentAgent lab2 = desktop2.query("#lb2");
		
		assertEquals("Foo_1", lab1.as(Label.class).getValue());
		assertEquals("Bar_1", lab2.as(Label.class).getValue());
		
		btn.click();
		includeSrc = include.as(Include.class).getSrc().substring(1);
		desktop2 = connect(includeSrc);
		lab1 = desktop2.query("#lb1");
		lab2 = desktop2.query("#lb2");

		assertEquals("FOO_1", lab1.as(Label.class).getValue());
		assertEquals("BAR_1", lab2.as(Label.class).getValue());
	}
}
