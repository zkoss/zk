package org.zkoss.zktest.bind.basic;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;

public class ChildrenSimpleVM {

	
	List<Node> nodes;
	
	public ChildrenSimpleVM(){
		nodes = new ArrayList<Node>();
		nodes.add(createNode("Item A",0,0));
		nodes.add(createNode("Item B",3,1));
		nodes.add(createNode("Item C",2,2));
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
	
	@DependsOn("nodes")
	public Collection<Node> getNodes2(){
		return new AbstractCollection<ChildrenSimpleVM.Node>() {

			@Override
			public Iterator<Node> iterator() {
				return nodes.iterator();
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return nodes.size();
			}
			
		};
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
	
	@Command 
	public void cmd3(@ContextParam(ContextType.BINDER)Binder binder){
		nodes.get(0).name="Item X";
		binder.notifyChange(nodes.get(0), "name");
		nodes.get(1).name="Item A";
		binder.notifyChange(nodes.get(1), "name");
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
