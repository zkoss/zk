package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;

public class B01908DatabindingOnPagingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent pg = desktop.query("#pg");
		ComponentAgent lab1 = desktop.query("#lab1");
		assertEquals(3L, pg.as(Paging.class).getActivePage());
		assertEquals(10L, pg.as(Paging.class).getPageSize());
		assertEquals(100L, pg.as(Paging.class).getTotalSize());
		assertEquals("3", lab1.as(Label.class).getValue());
	}
}
