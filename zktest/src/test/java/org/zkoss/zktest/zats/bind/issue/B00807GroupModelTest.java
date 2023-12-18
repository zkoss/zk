package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Group;
import org.zkoss.zul.Groupfoot;
import org.zkoss.zul.Label;

public class B00807GroupModelTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent grid = desktop.query("#grid");
		List<ComponentAgent> groups = grid.queryAll("group");
		List<ComponentAgent> groupfoots = grid.queryAll("groupfoot");
		List<ComponentAgent> rows = grid.queryAll("row");
		
		assertEquals(3, groups.size());
		assertEquals(3, groupfoots.size());
		assertEquals(5, rows.size());
		
		assertEquals("Fruits", groups.get(0).as(Group.class).getLabel());
		assertEquals("Seafood", groups.get(1).as(Group.class).getLabel());
		assertEquals("Vegetables", groups.get(2).as(Group.class).getLabel());
		
		assertEquals("1", ((Label)groupfoots.get(0).as(Groupfoot.class).getFirstChild().getFirstChild()).getValue());
		assertEquals("2", ((Label)groupfoots.get(1).as(Groupfoot.class).getFirstChild().getFirstChild()).getValue());
		assertEquals("2", ((Label)groupfoots.get(2).as(Groupfoot.class).getFirstChild().getFirstChild()).getValue());
		
		assertEquals("Apples", rows.get(0).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Salmon", rows.get(1).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Shrimp", rows.get(2).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Asparagus", rows.get(3).queryAll("label").get(1).as(Label.class).getValue());
		assertEquals("Beets", rows.get(4).queryAll("label").get(1).as(Label.class).getValue());
	}
}
