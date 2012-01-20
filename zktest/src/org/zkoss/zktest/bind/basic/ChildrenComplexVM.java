package org.zkoss.zktest.bind.basic;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class ChildrenComplexVM {

	
	List<Node> nodes;
	
	public ChildrenComplexVM(){
		nodes = new ArrayList<Node>();
		nodes.add(createNode("Item A",0,0));
		nodes.add(createNode("Item B",3,1));
		nodes.add(createNode("Item C",2,2));
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
	
	Node createNode(String name,int children,int nested){
		Node n = new Node(name);
		if(nested>0){
			for(int i=0;i<children;i++){
				n.addChild(createNode(name+"_"+i,children,nested-1));
			}
		}
		return n;
	}
	
	@Command @NotifyChange("nodes")
	public void cmd1(){
		nodes.add(createNode("Item D",2,2));
	}
	
	@Command
	public void cmd2(){
		nodes.add(createNode("Item E",2,2));
	}
	
	@Command @NotifyChange("nodes")
	public void clear(){
		nodes.clear();
	}
	
	static public class Node{
		List<Node> children;
		String name;
		
		public Node(String name){
			this.name = name;
			children = new ArrayList<Node>();
		}
		
		public void addChild(Node node){
			children.add(node);
		}
		
		public List<Node> getChildren(){
			return children;
		}
		
		public String getName(){
			return name;
		}
		
	}
	
}
