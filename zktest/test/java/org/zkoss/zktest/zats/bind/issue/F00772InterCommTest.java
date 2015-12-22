package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class F00772InterCommTest extends ZATSTestCase {
	
	@Test
	public void test() {
		DesktopAgent desktop = connect("/bind/issue/F00772-inter-comm.zul");
		
		ComponentAgent t11 = desktop.query("#inc1 #inc1win #t11");
		ComponentAgent l21 = desktop.query("#inc2 #inc2win #l21");
		ComponentAgent l31 = desktop.query("#inc3 #inc3win #l31");
		ComponentAgent l41 = desktop.query("#inc4 #inc4win #l41");
		ComponentAgent postx = desktop.query("#postx");
		ComponentAgent posty = desktop.query("#posty");
		ComponentAgent postz = desktop.query("#postz");
		ComponentAgent postmy = desktop.query("#postmy");
		ComponentAgent globalx = desktop.query("#inc1 #inc1win #globalx");
		ComponentAgent globaly = desktop.query("#inc2 #inc2win #globaly");
		ComponentAgent globalz = desktop.query("#inc3 #inc3win #globalz");
		
		assertEquals("", t11.as(Textbox.class).getValue());
		assertEquals("", l21.as(Label.class).getValue());
		assertEquals("", l31.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		
		postx.click();
		assertEquals("postX-X1", l21.as(Label.class).getValue());
		assertEquals("postX-X2", l31.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		
		posty.click();
		assertEquals("postX-X1", l21.as(Label.class).getValue());
		assertEquals("postY-X2", l31.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		
		postz.click();
		assertEquals("postE-X1", l21.as(Label.class).getValue());
		assertEquals("postZ-X3", l31.as(Label.class).getValue());
		assertEquals("", l41.as(Label.class).getValue());
		
		postmy.click();
		assertEquals("postE-X1", l21.as(Label.class).getValue());
		assertEquals("postZ-X3", l31.as(Label.class).getValue());
		assertEquals("postMy-XMy", l41.as(Label.class).getValue());
		
		t11.type("A");
		globalx.click();
		assertEquals("A-local-X1", l21.as(Label.class).getValue());
		assertEquals("A-local-X2", l31.as(Label.class).getValue());
		assertEquals("postMy-XMy", l41.as(Label.class).getValue());
		
		globaly.click();
		assertEquals("A-local-X1", l21.as(Label.class).getValue());
		assertEquals("A-local-X1-X2", l31.as(Label.class).getValue());
		assertEquals("postMy-XMy", l41.as(Label.class).getValue());
		
		globalz.click();
		assertEquals("postE-X1", l21.as(Label.class).getValue());
		assertEquals("A-local-X1-X2-X3", l31.as(Label.class).getValue());
		assertEquals("postMy-XMy", l41.as(Label.class).getValue());
	}
}
