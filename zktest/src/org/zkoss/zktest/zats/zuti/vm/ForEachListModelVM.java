/** ForEachListModelVM.java.

	Purpose:
		
	Description:
		
	History:
		02:15:14 PM Jan 26, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

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
import org.zkoss.zktest.zats.zuti.bean.Person;

/**
 * @author JamesChu
 *
 */
public class ForEachListModelVM implements Serializable{
	private static final long serialVersionUID = -1757483024000975324L;
	
	private List<Person> dataList_l;
	private String[] dataList_a1D = new String[] {
			"Apple", "Orange", "Mango"};
	private String[][] dataList_a2D = new String[][] {
			{"Apple", "10kg"},
            {"Orange", "20kg"},
            {"Mango", "12kg"}
    	};
	private Map<String, Person> dataList_m;
	private Set<Object> dataList_s;
	private ListModelList<Person> model_l;
	private ListModelArray<Object> model_a1D;
	private ListModelArray<Object> model_a2D;
	private ListModelMap<String, Person> model_m;
	private ListModelSet<Object> model_s;
	
	
	public ListModelList<Person> getModel_l() {
		return model_l;
	}

	public void setModel_l(ListModelList<Person> model_l) {
		this.model_l = model_l;
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

	public ListModelMap<String, Person> getModel_m() {
		return model_m;
	}

	public void setModel_m(ListModelMap<String, Person> model_m) {
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
		dataList_l = new ArrayList<Person>();
		for (int i = 0; i < 5; i++) {
			dataList_l.add(new Person("Person#" + i, "Addr#" + i));
		}
		model_l = new ListModelList<Person>(dataList_l, true);
		model_a1D = new ListModelArray<Object>(dataList_a1D);
		model_a2D = new ListModelArray<Object>(dataList_a2D);
		dataList_m = new HashMap<String, Person>();
		for (int i = 0; i < 5; i++) {
			dataList_m.put("Person#" + i, new Person("Person#" + i, "Addr#" + i));
		}
		model_m = new ListModelMap<String, Person>(dataList_m, true);
		dataList_s = new HashSet<Object>();
		for (int i = 0; i < 5; i++) {
			dataList_s.add(new Person("Person#" + i, "Addr#" + i));
		}
		model_s = new ListModelSet<Object>(dataList_s, true);
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
	
	private void replaceComponent(Component oc, Component nc) {
		Component parent = oc.getParent();
		Component ref = oc.getNextSibling();
		oc.detach();
		parent.insertBefore(nc, ref);
	}
	
	@Command
	@NotifyChange({"model_l","model_a1D","model_a2D","model_m","model_s"})
	public void change_model() {
		dataList_l = new ArrayList<Person>();
		for (int i = 0; i < 3; i++) {
			dataList_l.add(new Person("New Person#" + i, "Addr#" + i));
		}
		model_l = new ListModelList<Person>(dataList_l, true);
		String[] dataList_a1D_new = new String[] {
				"New Apple", "New Orange", "New Mango", "New Banana"};
		String[][] dataList_a2D_new = new String[][] {
				{"New Apple", "10.1kg"},
	            {"New Orange", "20.1kg"},
	            {"New Mango", "12.1kg"},
	            {"New Banana", "11.1kg"}
	    };
		model_a1D = new ListModelArray<Object>(dataList_a1D_new);
		model_a2D = new ListModelArray<Object>(dataList_a2D_new);
		Map<String,Person> dataList_m_new = new HashMap<String, Person>();
		for (int i = 0; i < 3; i++) {
			dataList_m_new.put("New Person#" + i, new Person("Person#" + i, "Addr#" + i));
		}
		model_m = new ListModelMap<String, Person>(dataList_m_new, true);
		Set<Object> dataList_s_new = new HashSet<Object>();
		for (int i = 0; i < 5; i++) {
			dataList_s_new.add(new Person("Person#" + i, "Addr#" + i));
		}
		model_s = new ListModelSet<Object>(dataList_s_new, true);
	}
	
	@Command
	public void add() {
		dataList_l.add(new Person("Person#5", "Addr#5 - person")); //Because of live model
		model_l.add(new Person("Person#6", "Addr#6 - model"));
		dataList_m.put("New Person#5", new Person("Person#5", "Addr#5 - person"));
		model_m.put("New Person#6", new Person("Person#6", "Addr#6 - model"));
		dataList_s.add(new Person("Person#5", "Addr#5 - person"));
		model_s.add(new Person("Person#6", "Addr#6 - model"));
	}
	
	@Command
	public void update() {
		Person p = dataList_l.get(0);
		p.setName("<model person change>");
		model_l.set(0, p);
		dataList_a1D[0] += "<model person change>";
		model_a1D.set(0, dataList_a1D[0]);
		dataList_a2D[0][0] += "<model person change>";
		dataList_a2D[0][1] += "<model person change>";
		model_a2D.set(0, dataList_a2D[0]);
		p = (Person) model_m.getElementAt(0).getValue();
		model_m.put(model_m.getElementAt(0).getKey(), new Person(p.getName(), "Addr#<model person change>"));
	}
	
	@Command
	public void remove() {
		model_l.remove(model_l.getSize() - 1);
		model_m.remove(model_m.getElementAt(model_m.getSize() - 1).getKey());
		model_s.remove(model_s.getElementAt(model_s.getSize() - 1));
	}
}
