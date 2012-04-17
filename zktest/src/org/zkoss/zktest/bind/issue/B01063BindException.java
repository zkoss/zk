package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;

public class B01063BindException {

	String value;
	Map<String, Object> map = new HashMap<String, Object>();
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Map<String, Object> getMap() {
		return map;
	}
}
