/* F80_ZK_2831.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 31 11:09:38 CST 2015, Created by chunfu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.xel.Evaluators;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * 
 * @author chunfu
 */
public class F80_ZK_2831 implements Serializable {
	String title = "deferred evaluation";
	String command = "";
	Person person = new Person();

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Command
	@NotifyChange("title")
	public void updateTitle() {
		this.title = "new title";
	}

	@Command
	@NotifyChange("title")
	public void emptyTitle() {
		title = "";
	}

	@Command
	@NotifyChange("command")
	public void evaluateEL(@ContextParam(ContextType.BINDER) Binder binder,
		@ContextParam(ContextType.PAGE) Page page, @ContextParam(ContextType.COMPONENT) Component comp) {
		Object o = Evaluators.evaluate(binder.getEvaluatorX(), comp, "#{mytitle += ' in command'}", String.class);
		command = o.toString();
	}

	byte[] _bytes;
	public void doSerialize(Div win,Label msg){
		try{
			doSerialize0(win, msg);
			doDeserialize0(win, msg);
		}catch(Exception x){
			x.printStackTrace();
			msg.setValue("error :"+x.getClass()+","+x.getMessage());
		}
	}
	public void doSerialize0(Div win,Label msg) throws Exception{
		Page pg = win.getPage();
		((ComponentCtrl)win).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(win);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	public void doDeserialize0(Div win, Label msg) throws Exception{
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);

		Div newwin = (Div) oos.readObject();
		Page pg = win.getPage();
		Component parent = win.getParent();
		Component ref = win.getNextSibling();
		win.detach();
		oos.close();
		oaos.close();
		parent.insertBefore(newwin, ref);
		//for load component back.
		((ComponentCtrl)newwin).sessionDidActivate(newwin.getPage());//simulate

		((Label)newwin.getFellow("msg")).setValue("done deserialize: "+_bytes.length);
	}
	public class Person implements Serializable {
		String name = "chunfu";
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	}
}
