/* F02581.java

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 07, 2015  5:25:13 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.bind.issue;

import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.SmartNotifyChange;
import org.zkoss.zk.ui.Component;
/**
 * 
 * @author jumperchen
 */
public class F02581 {
	String url = "./B00993IncludeReload2.zul";
	public String getUrl(){
		return url;
	}
	
	@org.zkoss.bind.annotation.Command
	@SmartNotifyChange("url")
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