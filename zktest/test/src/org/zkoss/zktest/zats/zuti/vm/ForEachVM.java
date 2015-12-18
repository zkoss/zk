/** ForEachVM.java.

	Purpose:
		
	Description:
		
	History:
		11:41:14 AM Nov 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zktest.zats.zuti.bean.Person;

/**
 * @author jumperchen
 *
 */
public class ForEachVM {
	private int begin = 1;
	private int end = 9;
	private int step = 2;
	private String var = "each";
	private java.util.List list = Arrays.asList("1","2","3");
	public void setDataList(List array) { list = array;}
	public List getDataList() {
		return list;
	}
	@SuppressWarnings("serial")
	private List<Person> small = new ArrayList<Person>() {{
		for (int i = 0; i < 3; i++)
			add(new Person(String.valueOf(i), i +"@zkoss.org"));
	}};
	@SuppressWarnings("serial")
	private List<Person> standard = new ArrayList<Person>() {{
		for (int i = 0; i < 6; i++)
			add(new Person(String.valueOf(i), i +"@zkoss.org"));
	}};
	@SuppressWarnings("serial")
	private List<Person> large = new ArrayList<Person>() {{
		for (int i = 0; i < 15; i++)
			add(new Person(String.valueOf(i), i +"@zkoss.org"));
	}};
	public List<Person> getSmallItems() {
		return small;
	}
	public void setSmallItems(List<Person> small) {
		this.small = small;
	}
	public List<Person> getStandardItems() {
		return standard;
	}
	public void setStandardItems(List<Person> standard) {
		this.standard = standard;
	}
	public List<Person> getLargeItems() {
		return large;
	}
	public void setLargeItems(List<Person> large) {
		this.large = large;
	}
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public int getStep() {
		return step;
	}
	public void setVar(String var) {
		this.var = var;
	}
	public String getVar() {
		return var;
	}
	public void setTemplate(String s){}
	public String getTemplate() {
		return "apply";
	}
	@Command
	@NotifyChange({"begin", "end"})
	public void updateRange(@BindingParam("begin") int begin, @BindingParam("end") int end) {
		this.begin = begin;
		this.end = end;
	}
	@Command
	@NotifyChange("step")
	public void updateStep(@BindingParam("step") int step) {
		if (step <= 0)
			throw new UiException("Step cannot be 0 or less than 0!");
		this.step = step;
	}
	@Command
	@NotifyChange("var")
	public void updateVar(@BindingParam("var") String var) {
		this.var = var;
	}
	@Command
	@NotifyChange("dataList")
	public void updateDataList(@BindingParam("data") String data) {
		if ("large".equals(data)) {
			list = large;
		} else if ("small".equals(data)) {
			list = small;
		} else {
			list = standard;
		}
	}
	private Converter concat = new ConcatConverter();
	public void setConcatConverter(Converter c) {}
	public Converter getConcatConverter() { return concat;}
	public static class ConcatConverter implements Converter {
	    public Object coerceToUi(Object val, Component comp, BindContext ctx) {
	        //user sets format in annotation of binding or args when calling binder.addPropertyBinding()  
	        final Number x = (Number) ctx.getConverterArg("x");
	        final Number y = (Number) ctx.getConverterArg("y");
	        final Number z = (Number) ctx.getConverterArg("z");
	        final String format = (String) ctx.getConverterArg("format");
	        return String.format(format, x, y, z);
	    }
	    public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	    	return null;
	    }
	}
	
}
