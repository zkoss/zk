/* F85_ZK_1148ReloadVM.java

        Purpose:
                
        Description:
                
        History:
                Mon Jul 02 16:15:56 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

public class F85_ZK_1148ReloadVM {
	String url = "./F85-ZK-1148-2.zul";
	public String getUrl(){
		return url;
	}
	
	@NotifyChange ("url")
	@Command
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
