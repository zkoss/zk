/* F85_ZK_3816VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 26 11:00:32 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import java.io.*;

/**
 * @author jameschu
 */
@AutoNotifyChange
public class F85_ZK_3816VM implements Serializable {
	private String label;

	@Init
	public void init() {
		System.out.println("F85_ZK_3816VM init");
		label = "";
	}

	@Command
	public void expandLabelAuto() {
		System.out.println("F85_ZK_3816VM expandLabelAuto");
		setLabel(getLabel() + "+");
	}

	@Command
	@NotifyChange("label")
	@Transient
	public void expandLabelOrigin() {
		System.out.println("F85_ZK_3816VM expandLabel");
		label += "*";
	}

	@Command
	@NotifyChange("label")
	public void expandLabel() {
		System.out.println("F85_ZK_3816VM expandLabel");
		setLabel(getLabel() + "-");
	}

	@Command
	@NotifyChange("str")
	public void concatString() {
		System.out.println("F85_ZK_3816VM concatString");
		setLabel(getLabel() + "$");
	}

	public void doSerialize(Window win, Label msg) {
		try {
			doSerialize0(win, msg);
			doDeserialize0(win, msg);
		} catch (Exception x) {
			x.printStackTrace();
			msg.setValue("error :" + x.getClass() + "," + x.getMessage());
		}
	}

	byte[] _bytes;

	public void doSerialize0(Window win, Label msg) throws Exception {
		Page pg = win.getPage();
		((ComponentCtrl) win).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(win);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	public void doDeserialize0(Window win, Label msg) throws Exception {
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);

		Window newwin = (Window) oos.readObject();
		Page pg = win.getPage();
		Component parent = win.getParent();
		Component ref = win.getNextSibling();
		win.detach();
		oos.close();
		oaos.close();
		parent.insertBefore(newwin, ref);
		//for load component back.
		((ComponentCtrl) newwin).sessionDidActivate(newwin.getPage());//simulate

		((Label) newwin.getFellow("msg")).setValue("done deserialize: " + _bytes.length);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	public String getStr() {
		return getLabel() + " 123";
	}
}
