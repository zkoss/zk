package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B00993IncludeReloadTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent l1 = desktop.query("#inc1 #win2 #l1");
		ComponentAgent l2 = desktop.query("#inc1 #win2 #l2");
		ComponentAgent reload = desktop.query("#reload");
		
		String val1;
		String val2;
		
		val1 = l1.as(Label.class).getValue();
		val2 = l2.as(Label.class).getValue();
		reload.click();
		l1 = desktop.query("#inc1 #win2 #l1");
		l2 = desktop.query("#inc1 #win2 #l2");
		assertFalse(val1.equals(l1.as(Label.class).getValue()));
		assertFalse(val2.equals(l2.as(Label.class).getValue()));

		try {
			Thread.sleep(100);
		} catch (Exception e) {
			
		}
		val1 = l1.as(Label.class).getValue();
		val2 = l2.as(Label.class).getValue();
		reload.click();
		l1 = desktop.query("#inc1 #win2 #l1");
		l2 = desktop.query("#inc1 #win2 #l2");
		assertFalse(val1.equals(l1.as(Label.class).getValue()));
		assertFalse(val2.equals(l2.as(Label.class).getValue()));

		try {
			Thread.sleep(100);
		} catch (Exception e) {
			
		}
		val1 = l1.as(Label.class).getValue();
		val2 = l2.as(Label.class).getValue();
		reload.click();
		l1 = desktop.query("#inc1 #win2 #l1");
		l2 = desktop.query("#inc1 #win2 #l2");
		assertFalse(val1.equals(l1.as(Label.class).getValue()));
		assertFalse(val2.equals(l2.as(Label.class).getValue()));
		
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			
		}
		val1 = l1.as(Label.class).getValue();
		val2 = l2.as(Label.class).getValue();
		reload.click();
		l1 = desktop.query("#inc1 #win2 #l1");
		l2 = desktop.query("#inc1 #win2 #l2");
		assertFalse(val1.equals(l1.as(Label.class).getValue()));
		assertFalse(val2.equals(l2.as(Label.class).getValue()));
		
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			
		}
		val1 = l1.as(Label.class).getValue();
		val2 = l2.as(Label.class).getValue();
		reload.click();
		l1 = desktop.query("#inc1 #win2 #l1");
		l2 = desktop.query("#inc1 #win2 #l2");
		assertFalse(val1.equals(l1.as(Label.class).getValue()));
		assertFalse(val2.equals(l2.as(Label.class).getValue()));
	}
}
