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
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;

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
