package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01344DeletingEntryTest extends ZATSTestCase{
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		for (int i = 10; i > 0; i--) {
			assertEquals(i + "", desktop.query("#lb1").as(Label.class).getValue());
			desktop.query("#btn1").click();
		}
		assertEquals("0", desktop.query("#lb1").as(Label.class).getValue());
	}
}
