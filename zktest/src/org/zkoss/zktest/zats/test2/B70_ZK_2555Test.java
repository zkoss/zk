package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.PagingAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Group;
import org.zkoss.zul.Groupfoot;
import org.zkoss.zul.Label;

public class B70_ZK_2555Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent grid = desktop.query("#win #grid");
		List<ComponentAgent> groups = grid.queryAll("group");
		List<ComponentAgent> rows = grid.queryAll("row");
		ComponentAgent check = desktop.query("#win #check");
		
		assertEquals(3, groups.size());
		assertEquals(8, rows.size());
		
		assertEquals("fruit", groups.get(0).as(Group.class).getLabel());
		assertEquals("beers", groups.get(1).as(Group.class).getLabel());
		assertEquals("money", groups.get(2).as(Group.class).getLabel());
		
		
		assertEquals("apple", rows.get(0).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("pear", rows.get(1).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("bitburger", rows.get(2).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("augustiner", rows.get(3).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("budweiser", rows.get(4).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("dollar", rows.get(5).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("euro", rows.get(6).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("yen", rows.get(7).queryAll("label").get(0).as(Label.class).getValue());
		assertEquals("apple description", rows.get(0).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("pear description", rows.get(1).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("bitburger description", rows.get(2).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("augustiner description", rows.get(3).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("budweiser description", rows.get(4).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("dollar description", rows.get(5).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("euro description", rows.get(6).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("yen description", rows.get(7).queryAll("label").get(1).as(Label.class).getValue());
		
		check.check(true);
		rows = grid.queryAll("row");
		assertEquals(1, rows.get(0).queryAll("label").size());
		assertEquals(1, rows.get(1).queryAll("label").size());
		assertEquals(1, rows.get(2).queryAll("label").size());
		assertEquals(1, rows.get(3).queryAll("label").size());
		assertEquals(1, rows.get(4).queryAll("label").size());
		assertEquals(1, rows.get(5).queryAll("label").size());
		assertEquals(1, rows.get(6).queryAll("label").size());
		assertEquals(1, rows.get(7).queryAll("label").size());
	}
}
