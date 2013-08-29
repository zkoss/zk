package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

public class B01787NotifyChangeTreeCase {
	
	Node nodeA;
	Node nodeB;
	Node nodeC;
	
	private DefaultTreeModel collection;
	private List<Node> path = new ArrayList();

	@Init
	public void init() {
		
		Node root = new Node("root");
		
		nodeA = new Node("Item A");
		nodeB = new Node("Item B");
		
		path.add(nodeA);
		path.add(nodeB);
		
		collection = new DefaultTreeModel(root);
		
		root.add(nodeA);
		root.add(nodeB);
		
		nodeA.add(new Node("Item A1"));
		nodeA.add(new Node("Item A2"));
		
		nodeB.add(new Node("Item B1"));
		nodeB.add(new Node("Item B2"));
	}

	HashMap<Object,Integer> counts1 = new HashMap<Object,Integer>();
	HashMap<Object,Integer> counts1x = new HashMap<Object,Integer>();
	HashMap<Object,Integer> counts2 = new HashMap<Object,Integer>();
	HashMap<Object,Integer> counts3 = new HashMap<Object,Integer>();
	
	public String get1(final Object n){
		Wrap r = new Wrap(n);
		Integer c = counts1.get(r);
		if(c==null){
			counts1.put(r, c = 0);
		}else{
			counts1.put(r, c = c+1);
		}
//		System.out.println(">>get count1 of "+n+":"+c);
		return n+":"+c;
	}
	public String get1x(final Object n){
		Wrap r = new Wrap(n);
		Integer c = counts1x.get(r);
		if(c==null){
			counts1x.put(r, c = 0);
		}else{
			counts1x.put(r, c = c+1);
		}
//		System.out.println(">>get counts1x of "+n+":"+c);
		return n+":x:"+c;
	}
	public String get2(final Object n){
		Wrap r = new Wrap(n);
		Integer c = counts2.get(r);
		if(c==null){
			counts2.put(r, c = 0);
		}else{
			counts2.put(r, c = c+1);
		}
//		System.out.println(">>get count2 of "+n+":"+c);
		return n+":"+c;
	}
	public String get3(final Object n){
		Wrap r = new Wrap(n);
		Integer c = counts3.get(r);
		if(c==null){
			counts3.put(r, c = 0);
		}else{
			counts3.put(r, c = c+1);
		}
//		System.out.println(">>get count3 of "+n+":"+c);
		return n+":"+c;
	}
	
	
	

	public Node getNodeA() {
		return nodeA;
	}


	public Node getNodeB() {
		return nodeB;
	}

	public Node getNodeC() {
		return nodeC;
	}

	public List<Node> getPath() {
		return path;
	}
	
	public DefaultTreeModel<Node> getCollection() {
		return collection;
	}
	
	@Command
	@NotifyChange({"path","collection"})
	public void clear() {
		path.clear();
	}
	
	@Command
	@NotifyChange("path")
	public void updatePath() {
//		path.add(new Node("New "+count++));
	}
	@Command
	@NotifyChange("collection")
	public void updateCollection() {

	}	
	
	@Command
	public void notifyChangeA() {
		nodeA.type="type2";
		nodeA.name = "Item A.*";
		BindUtils.postNotifyChange(null, null, nodeA, "*");
	}
	
	@Command
	public void notifyChangeAName() {
		nodeA.type="type2";//change type, but notify name change, it shouldn't trigger template reload
		nodeA.name = "Item A.name";
		BindUtils.postNotifyChange(null, null, nodeA, "name");
	}
	
	@Command
	public void notifyChangeB() {
		nodeB.type="type2";
		nodeB.name = "Item B.*";
		BindUtils.postNotifyChange(null, null, nodeB, "*");
	}
	
	@Command
	public void notifyChangeBName() {
		nodeB.type="type2";
		nodeB.name = "Item B.name";
		BindUtils.postNotifyChange(null, null, nodeB, "name");
	}	

	public static class Node extends DefaultTreeNode{
		String name;
		String type = "type1";
		public Node(String name) {
			super(name, new ArrayList());
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public String getType(){
			return type;
		}
		public String toString(){
			return getClass().getSimpleName()+"@"+System.identityHashCode(this);
		}
	}
	
	static class Wrap {
		Object w;
		public Wrap(Object w){
			this.w = w;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((w == null) ? 0 : w.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Wrap other = (Wrap) obj;
			if (w == null) {
				if (other.w != null)
					return false;
			} else if (w != other.w)
				return false;
			return true;
		}
	}
	
	public String toString(){
		return getClass().getSimpleName()+"@"+System.identityHashCode(this);
	}
}