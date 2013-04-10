package org.zkoss.zktest.bind.issue;

import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

public class B00993IncludeReload {
	String url = "/bind/issue/B00993IncludeReload2.zul";
	public String getUrl(){
		return url;
	}
	
	@org.zkoss.bind.annotation.Command
	@NotifyChange("url")
	public void reload(){
		
	}
	
	public Date getNow(){
		return new Date();
	}
	
	public Converter getConverter(){
		return new Converter() {
			
			
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				if(val instanceof String){
					return val +"?tms=" + System.currentTimeMillis();
				}
				return val;
			}
			
			
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}
		};
	}
}
