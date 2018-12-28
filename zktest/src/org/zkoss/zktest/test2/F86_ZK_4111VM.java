/* F86_ZK_4111VM.java

		Purpose:
		
		Description:
		
		History:
				Tue Dec 25 16:16:41 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import java.util.HashMap;
import java.util.Map;

public class F86_ZK_4111VM {
	private Map<String, String> myAttributes;
	
	@Init
	public void init() {
		myAttributes = new HashMap<String, String>();
	}
	
	public Map<String, String> getMyAttributes() {
		return myAttributes;
	}
	
	public void setMyAttributes(Map<String, String> inputAttr) {
		this.myAttributes = inputAttr;
	}
	
	@Command("addA")
	@NotifyChange("myAttributes")
	public void addAutocorrect() {
		Map<String, String> map = new HashMap<String, String>(getMyAttributes());
		map.put("autocorrect", "off");
		setMyAttributes(map);
	}
	
	@Command("addS")
	@NotifyChange("myAttributes")
	public void addSpellcheck() {
		Map<String, String> map = new HashMap<String, String>(getMyAttributes());
		map.put("spellcheck", "true");
		setMyAttributes(map);
	}
	
	@Command("addAB")
	@NotifyChange("myAttributes")
	public void addAB() {
		Map<String, String> map = new HashMap<String, String>(getMyAttributes());
		map.put("aaa", "aaa");
		map.put("bbb", "bbb");
		setMyAttributes(map);
	}
	
	@Command("changeCD")
	@NotifyChange("myAttributes")
	public void changeCD() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ccc", "ccc");
		map.put("ddd", "ddd");
		setMyAttributes(map);
	}
	
	@Command("clear")
	@NotifyChange("myAttributes")
	public void clear() {
		Map<String, String> map = new HashMap<String, String>();
		setMyAttributes(map);
	}
	
	@Command("show")
	public void show() {
		Clients.log(getMyAttributes().toString());
	}
}
