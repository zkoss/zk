package org.zkoss.zktest.bind.issue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zktest.bind.issue.F02545ChildrenBindingMapTestVM.Node;
import org.zkoss.zul.ArrayComparator;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.GroupsListModel;
public class F02545ChildrenBindingMapTestVM implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> nodes1;
	private Map<String, Node> nodes2;
	
	
	public Map<String, String> getNodes1() {
		return nodes1;
	}

	public void setNodes1(Map<String, String> nodes1) {
		this.nodes1 = nodes1;
	}

	public Map<String, Node> getNodes2() {
		return nodes2;
	}

	public void setNodes2(Map<String, Node> nodes2) {
		this.nodes2 = nodes2;
	}

	@Init
	public void init() {
		nodes1 = new HashMap<String, String>();
		for (int i = 0; i < 5; i++) {
			nodes1.put("item " + i, "Node#");
		}
		nodes2 = new HashMap<String, Node>();
		for (int i = 0; i < 5; i++) {
			nodes2.put("item " + i, new Node("" + i, "Node#" + i));
		}
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
		System.out.println("doSerialize");
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
		System.out.println("doDeserialize");
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
