package org.zkoss.zk.ui.select.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The model representing a pseudo class in Selector
 * @since 6.0.0
 * @author simonpai
 */
public class PseudoClass {
	
	private String _name;
	private List<String> _parameters = new ArrayList<String>();
	
	public PseudoClass(String name){
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String[] getParameter() {
		return _parameters.toArray(new String[0]);
	}
	
	public void addParameter(String parameter) {
		_parameters.add(parameter);
	}
	
	@Override
	public String toString() {
		return ":"+_name + (_parameters.isEmpty()? "" : 
			"(" + join(_parameters, ",") + ")");
	}
	
	// helper //
	private static String join(List<? extends Object> objs, String joiner){
		if(objs == null || objs.isEmpty()) return "";
		Iterator<? extends Object> iter = objs.iterator();
		StringBuffer sb = new StringBuffer(iter.next().toString());
		while(iter.hasNext()) sb.append(joiner).append(iter.next());
		return sb.toString();
	}
}
