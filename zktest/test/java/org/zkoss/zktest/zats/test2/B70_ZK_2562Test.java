package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

public class B70_ZK_2562Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent mytree = desktop.query("#mytree");
		ComponentAgent mybtn = desktop.query("#mybtn");
		
		assertEquals("Level1", ((DefaultTreeNode)mytree.as(Tree.class).getModel().getChild(new int[]{0})).getData());
		assertEquals("Level2", ((DefaultTreeNode)mytree.as(Tree.class).getModel().getChild(new int[]{0, 0})).getData());
		
		mybtn.click();
		assertEquals("4", ((DefaultTreeNode)mytree.as(Tree.class).getModel().getChild(new int[]{0})).getData());
		assertEquals("3", ((DefaultTreeNode)mytree.as(Tree.class).getModel().getChild(new int[]{0, 0})).getData());
	}
}
