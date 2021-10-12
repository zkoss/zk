package org.zkoss.zktest.test2;

import java.io.*;
import java.util.*;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class F80_ZK_3185VM implements Serializable {
	private F80_ZK_3185Bean myObj = new F80_ZK_3185Bean();
	private F80_ZK_3185Validator myValidator;

	@Init
	public void init() {
		initMyObj(2000);
		myValidator = new F80_ZK_3185Validator();
	}

	public void initMyObj(int year) {
		myObj.setName("MainObj");
		Calendar cal = new GregorianCalendar();
		cal.set(GregorianCalendar.YEAR, year);
		Date date = cal.getTime();
		myObj.setMainSubBean(new F80_ZK_3185SubBean("SubObj", date));
		myObj.setDate(date);
		Map<String, Date> remark = new HashMap<String, Date>();
		remark.put("r1", date);
		remark.put("r2", date);
		myObj.setRemark(remark);
		if (myObj.getSubBeanList().size() == 0) {
			myObj.getSubBeanList().add(new F80_ZK_3185SubBean("subName1", date));
			myObj.getSubBeanList().add(new F80_ZK_3185SubBean("subName2", date));
		} else {
			for (F80_ZK_3185SubBean b : myObj.getSubBeanList()) {
				b.setDate(date);
			}
		}
	}

	@Command
	public void addSubBean(@BindingParam("form") F80_ZK_3185Bean pForm) {
		pForm.getSubBeanList().add(new F80_ZK_3185SubBean("subName3", new Date()));
	}

	@Command
	public void validate() {
	}

	@Command
	@NotifyChange("myObj")
	public void changeObj(@ScopeParam("myProxy") Form form) {
		initMyObj(2015);
		BindUtils.postNotifyChange(null, null, form, "mainSubBean");
	}

	byte[] _bytes;
	public void doSerialize(Window win){
		try{
			doSerialize0(win);
			doDeserialize0(win);
		}catch(Exception x){
			x.printStackTrace();
			Clients.log("error :"+x.getClass()+","+x.getMessage());
		}
	}
	public void doSerialize0(Window win) throws Exception{
		Page pg = win.getPage();
		((ComponentCtrl)win).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(win);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	public void doDeserialize0(Window win) throws Exception{
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
		((ComponentCtrl)newwin).sessionDidActivate(newwin.getPage());//simulate
		Clients.log("done deserialize: "+_bytes.length);
	}

	@Command("cancel")
	public void cancel(@BindingParam("form") FormProxyObject form) {
		//dummy
	}

	public F80_ZK_3185Bean getMyObj() {
		return myObj;
	}

	public void setMyObj(F80_ZK_3185Bean myObj) {
		this.myObj = myObj;
	}

	public Validator getMyValidator() {
		return myValidator;
	}

}
