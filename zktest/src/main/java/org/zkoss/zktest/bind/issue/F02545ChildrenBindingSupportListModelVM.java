package org.zkoss.zktest.bind.issue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Window;
public class F02545ChildrenBindingSupportListModelVM implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private List<Node> nodes_l;
	private String[] nodes_a1D = new String[] {
			"Apple", "Orange", "Mango"};
	private String[][] nodes_a2D = new String[][] {
			{"Apple", "10kg"},
            {"Orange", "20kg"},
            {"Mango", "12kg"}
    	};
	private Map<String, Node> nodes_m;
	private Set<Object> nodes_s;
	private ListModelList<Node> model_l;
	private ListModelArray<Object> model_a1D;
	private ListModelArray<Object> model_a2D;
	private ListModelMap<String, Node> model_m;
	private ListModelSet<Object> model_s;
	
	public Map<String, Node> getNodes_m() {
		return nodes_m;
	}
	public void setNodes_m(Map<String, Node> nodes_m) {
		this.nodes_m = nodes_m;
	}
	public Set<Object> getNodes_s() {
		return nodes_s;
	}
	public void setNodes_s(Set<Object> nodes_s) {
		this.nodes_s = nodes_s;
	}
	public List<Node> getNodes_l() {
		return nodes_l;
	}
	public void setNodes_l(List<Node> nodes_l) {
		this.nodes_l = nodes_l;
	}
	public String[] getNodes_a1D() {
		return nodes_a1D;
	}
	public void setNodes_a1D(String[] nodes_a1D) {
		this.nodes_a1D = nodes_a1D;
	}
	public String[][] getNodes_a2D() {
		return nodes_a2D;
	}
	public void setNodes_a2D(String[][] nodes_a2D) {
		this.nodes_a2D = nodes_a2D;
	}
	public ListModelArray<Object> getModel_a1D() {
		return model_a1D;
	}
	public void setModel_a1D(ListModelArray<Object> model_a1D) {
		this.model_a1D = model_a1D;
	}
	public ListModelArray<Object> getModel_a2D() {
		return model_a2D;
	}
	public void setModel_a2D(ListModelArray<Object> model_a2D) {
		this.model_a2D = model_a2D;
	}
	public ListModelList<Node> getModel_l() {
		return model_l;
	}
	public void setModel_l(ListModelList<Node> model_l) {
		this.model_l = model_l;
	}
	public ListModelMap<String, Node> getModel_m() {
		return model_m;
	}
	public void setModel_m(ListModelMap<String, Node> model_m) {
		this.model_m = model_m;
	}
	public ListModelSet<Object> getModel_s() {
		return model_s;
	}
	public void setModel_s(ListModelSet<Object> model_s) {
		this.model_s = model_s;
	}
	
	@Init
	public void init() {
		nodes_l = new ArrayList<Node>();
		for (int i = 0; i < 5; i++) {
			nodes_l.add(new Node("" + i, "Node#" + i));
		}
		model_l = new ListModelList<Node>(nodes_l, true);
		model_a1D = new ListModelArray<Object>(nodes_a1D);
		model_a2D = new ListModelArray<Object>(nodes_a2D);
		nodes_m = new HashMap<String, Node>();
		for (int i = 0; i < 5; i++) {
			nodes_m.put("item " + i, new Node("" + i, "Node#" + i));
		}
		model_m = new ListModelMap<String, Node>(nodes_m, true);
		nodes_s = new HashSet<Object>();
		for (int i = 0; i < 5; i++) {
			nodes_s.add(new Node("" + i, "Node#" + i));
		}
		model_s = new ListModelSet<Object>(nodes_s, true);
	}
	
	byte[] _bytes;
	public void doSerialize(Window win,Label msg){
		try{
			doSerialize0(win, msg);
			doDeserialize0(win, msg);
		}catch(Exception x){
			x.printStackTrace();
			msg.setValue("error :"+x.getClass()+","+x.getMessage());
		}
	}
	
	public void doSerialize0(Window win, Label msg) throws Exception{
		Page pg = win.getPage();
		((ComponentCtrl)win).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(win);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}
	
	public void doDeserialize0(Window win, Label msg) throws Exception{
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);
		Window newwin = (Window) oos.readObject();
		oos.close();
		oaos.close();
		replaceComponent(win, newwin);
		//for load component back.
		((ComponentCtrl)newwin).sessionDidActivate(newwin.getPage());//simulate
		msg.setValue("done deserialize: "+_bytes.length);	
	}
	
	public void clone(Window win) {
		System.out.println("@Command - clone");
		replaceComponent(win, (Component) win.clone());
	}
	
	private void replaceComponent (Component oc, Component nc) {
		Component parent = oc.getParent();
		Component ref = oc.getNextSibling();
		oc.detach();
		parent.insertBefore(nc, ref);
	}
	
	@Command
	@NotifyChange({"model_l","model_a1D","model_a2D","model_m","model_s"})
	public void change_model(Window win) {
		nodes_l = new ArrayList<Node>();
		for (int i = 0; i < 3; i++) {
			nodes_l.add(new Node("" + i, "New Node#" + i));
		}
		model_l = new ListModelList<Node>(nodes_l, true);
		String[] nodes_a1D_new = new String[] {
				"New Apple", "New Orange", "New Mango", "New Banana"};
		String[][] nodes_a2D_new = new String[][] {
				{"New Apple", "10.1kg"},
	            {"New Orange", "20.1kg"},
	            {"New Mango", "12.1kg"},
	            {"New Banana", "11.1kg"}
	    };
		model_a1D = new ListModelArray<Object>(nodes_a1D_new);
		model_a2D = new ListModelArray<Object>(nodes_a2D_new);
		Map<String,Node> nodes_m_new = new HashMap<String, Node>();
		for (int i = 0; i < 3; i++) {
			nodes_m_new.put("new item " + i, new Node("" + i, "New Node#" + i));
		}
		model_m = new ListModelMap<String, Node>(nodes_m_new, true);
		Set<Object> nodes_s_new = new HashSet<Object>();
		for (int i = 0; i < 5; i++) {
			nodes_s_new.add(new Node("" + i, "New Node#" + i));
		}
		model_s = new ListModelSet<Object>(nodes_s_new, true);
	}
	
	@Command
	public void add() {
		nodes_l.add(new Node("5", "Node#5 - node")); //Because of live model
		model_l.add(new Node("6", "Node#6 - model"));
		nodes_m.put("new item 5", new Node("5", "Node#5 - node"));
		model_m.put("new item 6", new Node("6", "Node#6 - model"));
		nodes_s.add(new Node("5", "Node#5 - node"));
		model_s.add(new Node("6", "Node#6 - model"));
	}
	
	@Command
	public void update() {
		Node n = nodes_l.get(0);
		n.setName("<model node change>");
		model_l.set(0, n);
		nodes_a1D[0] += "<model node change>";
		model_a1D.set(0, nodes_a1D[0]);
		nodes_a2D[0][0] += "<model node change>";
		nodes_a2D[0][1] += "<model node change>";
		model_a2D.set(0, nodes_a2D[0]);
		n = (Node) model_m.getElementAt(0).getValue();
		model_m.put(model_m.getElementAt(0).getKey(), new Node(n.getId(), "<model node change>"));
	}
	
	@Command
	public void remove() {
		model_l.remove(model_l.getSize() - 1);
		model_m.remove(model_m.getElementAt(model_m.getSize() - 1).getKey());
		model_s.remove(model_s.getElementAt(model_s.getSize() - 1));
	}
	
	public class Node implements Serializable{
		private static final long serialVersionUID = 1L;
		String id;
		String name;
		
		public Node (String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
