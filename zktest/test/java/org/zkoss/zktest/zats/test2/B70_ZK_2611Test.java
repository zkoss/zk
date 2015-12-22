package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Tree;
import org.zkoss.zul.ext.TreeOpenableModel;

public class B70_ZK_2611Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent tree = desktop.query("#tree");
		ComponentAgent removeBtn = desktop.query("#removeButton");
		ComponentAgent addBtn = desktop.query("#addButton");
		
		List<ComponentAgent> treeitems = tree.queryAll("treeitem");
		treeitems.get(1).as(OpenAgent.class).open(true);
		treeitems.get(2).as(OpenAgent.class).open(true);
		treeitems.get(4).as(OpenAgent.class).open(true);
		tree.queryAll("treeitem").get(2).as(OpenAgent.class).open(true);
		TreeOpenableModel model = ((TreeOpenableModel)tree.as(Tree.class).getModel());
		assertEquals("[[1], [2], [4], [1, 0]]", logPaths(model.getOpenPaths()));
		
		addBtn.click();
		assertEquals("[[2], [3], [5], [2, 0]]", logPaths(model.getOpenPaths()));
		
		removeBtn.click();
		removeBtn.click();
		assertEquals("[[0], [1], [3], [0, 0]]", logPaths(model.getOpenPaths()));
		
		addBtn.click();
		assertEquals("[[0], [2], [4], [0, 0]]", logPaths(model.getOpenPaths()));
		
		removeBtn.click();
		assertEquals("[[1], [3]]", logPaths(model.getOpenPaths()));
	}
	
	public String logPaths(int[][] paths) {
		List<String> pathTexts = new ArrayList<String>();
		for(int i = 0; i < paths.length; i++) {
			pathTexts.add(Arrays.toString(paths[i]));
		}
		return pathTexts.toString();
	}
}
