package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.json.JavaScriptValue;

public class F80_ZK_2698VM {
	
	private Map<String, Object> config;
	private String value;
	
	@Init
	public void init() {
		config = new HashMap<String, Object>();
		//config.put("btns", new JavaScriptValue("['bold', 'italic', '|', 'link']"));
		config.put("closable", true);
		//config.put("autogrow", true);
		value = "this is a demo for <b>trumbowyg</b> editor!!";
	}
	
	@Command
	@NotifyChange("value")
	public void changeValue(@BindingParam("val") String val) {
		System.out.println(val);
		this.value = val;
	}
	
	@Command
	public void printValue(@BindingParam("val") String val) {
		System.out.println(val);
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}