/* B96_ZK_3563VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 27 12:46:50 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class B96_ZK_3563VM implements Serializable {
	private B96_ZK_3563Object paramObject = new B96_ZK_3563Object();
	private boolean template = true;
	List<String> items = new ArrayList<String>();

	public B96_ZK_3563VM() {
		items.add("A");
	}

	@Command("save")
	public void save() {
		BindUtils.postNotifyChange(null, null, this, ".");
	}

	public List<String> getItems() {
		return items;
	}

	public B96_ZK_3563Object getParamObject() {
		return paramObject;
	}

	public boolean isTemplate() {
		template = !template;
		return template;
	}

	public int getCounter1() {
		return paramObject.getCounter1();
	}

	public int getCounter2() {
		return paramObject.getCounter2();
	}

	public int getCounter3() {
		return paramObject.getCounter3();
	}

	byte[] _bytes;

	@Command
	public void doSerialize(@BindingParam Component comp) {
		try {
			doSerialize0(comp);
			doDeserialize0(comp);
		} catch(Exception x) {
			x.printStackTrace();
			Clients.log("error :" + x.getClass() + "," + x.getMessage());
		}
	}

	public void doSerialize0(Component comp) throws Exception {
		Page pg = comp.getPage();
		((ComponentCtrl) comp).sessionWillPassivate(pg); //simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(comp);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	public void doDeserialize0(Component comp) throws Exception {
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);
		Component newComp = (Component) oos.readObject();
		Page pg = comp.getPage();
		Component parent = comp.getParent();
		pg.getFellows();
		Component ref = comp.getNextSibling();
		comp.detach();
		oos.close();
		oaos.close();
		parent.insertBefore(newComp, ref);
		//for load component back.
		((ComponentCtrl) newComp).sessionDidActivate(newComp.getPage()); //simulate
		Clients.log("done deserialize: " + _bytes.length);
	}
}
