/* Children_SimpleTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.childrenbinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class Children_ComplexTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/databinding/childrenbinding/children-complex.zul");
		List<Node> nodes = new ArrayList<>();
		nodes.add(createNode("Item A", 0, 0));
		nodes.add(createNode("Item B", 3, 1));
		nodes.add(createNode("Item C", 2, 2));
		testComplex(nodes, jq("$vlayout").toWidget(), true);
	}

	public class Node {
		String name;
		List<Node> children = new ArrayList<>();

		public Node(String name) {
			this.name = name;
		}

		public void addChild(Node node) {
			children.add(node);
		}

		public String getName() {
			return name;
		}

		public List<Node> getChildren() {
			return children;
		}
	}

	public void testComplex(List<Node> nodes, Widget parent, boolean children1) {
		Widget children = parent.firstChild();
		assertEquals(nodes.size(), parent.nChildren());
		Widget w = children;
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			String str = "children1";
			if (!children1) {
				str = "children2";
			}
			assertEquals(str, w.get("sclass"));
			Widget l = w.firstChild();
			assertEquals(n.getName(), l.get("value"));
			Widget l2 = l.nextSibling();
			testComplex(n.getChildren(), l2, !children1);
			w = w.nextSibling();
		}
	}

	public Node createNode(String name, int childrenSize, int nestedLayer) {
		Node n = new Node(name);
		if (nestedLayer > 0) {
			for (int i = 0; i < childrenSize; i++)
				n.addChild(createNode(name + "_" + i, childrenSize, nestedLayer - 1));
		}
		return n;
	}
}
