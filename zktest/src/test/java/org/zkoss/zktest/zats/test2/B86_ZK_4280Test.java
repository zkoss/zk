package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Window;

import static org.junit.Assert.assertEquals;

public class B86_ZK_4280Test extends ZATSTestCase {

    @Test
    public void test() {
		DesktopAgent da = connect();
		ComponentAgent btn = da.query("#toggle");
		ComponentAgent win = da.query("#win");
		ComponentAgent de = win.query("#detach");
		btn.click();
		btn.click();
		de.click();
		btn.click();
		assertEquals("true", win.as(Window.class).getTitle());
    }
}
