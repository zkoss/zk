package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B01185CollectionBindingNPETest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		desktop.query("#addPersonBtn").click();
		desktop.query("#delPerson_0").click();
		desktop.query("#addUrlBtn").click();
		desktop.query("#delUrl_0").click();
		ComponentAgent widget = desktop.query("#delUrl_0");
		assertFalse(widget != null);
	}
}
