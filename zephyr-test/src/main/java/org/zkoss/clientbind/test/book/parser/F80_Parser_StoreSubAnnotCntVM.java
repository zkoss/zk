package org.zkoss.clientbind.test.book.parser;

import java.util.*;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

public class F80_Parser_StoreSubAnnotCntVM {

	@Wire("#w1")
	private Window w1;
	@Wire("#w2")
	private Window w2;

	private String label1 = "test1";
	private String label2 = "test2";
	private String label3 = "test3";

	public String getLabel1() {
		return label1;
	}

	public String getLabel2() {
		return label2;
	}

	public String getLabel3() {
		return label3;
	}

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	@NotifyChange("label1")
	public void func1() {
		label1 += "<f1>";
	}

	@Command
	public void detach_attach() {
		w2.detach();
		w1.appendChild(w2);
	}

	@Command
	@NotifyChange("label2")
	public void func2() {
		label2 += "<f2>";
	}

	@Command
	@NotifyChange("label3")
	public void func3() {
		label3 += "<f3>";
	}

	@Init
	public void init() {
		nodes = new ArrayList<Node>();
		nodes.add(createNode("Item A", 0, 0));
		nodes.add(createNode("Item B", 3, 1));
		nodes.add(createNode("Item C", 2, 2));
	}

	List<Node> nodes;

	public List<Node> getNodes() {
		return nodes;
	}

	@DependsOn("nodes")
	public Collection<Node> getNodes2() {
		return new AbstractCollection<Node>() {
			public Iterator<Node> iterator() {
				return nodes.iterator();
			}

			public int size() {
				return nodes.size();
			}
		};
	}

	Node createNode(String name, int children, int nested) {
		Node n = new Node(name);
		if (nested > 0) {
			for (int i = 0; i < children; i++) {
				n.addChild(createNode(name + "_" + i, children, nested - 1));
			}
		}
		return n;
	}

	static public class Node {
		List<Node> children;
		String name;

		public Node(String name) {
			this.name = name;
			children = new ArrayList<Node>();
		}

		public void addChild(Node node) {
			children.add(node);
		}

		public List<Node> getChildren() {
			return children;
		}

		public String getName() {
			return name;
		}

	}

}
