/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henri

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.basic;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * currently, no way to display error message if I decouple the vm with bind-composer.
 * @author Dennis Chen
 * 
 */
public class ViewModelInit implements Initiator {
	

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		page.setAttribute("myvm1", new SubViewModel("AA"));
		page.setAttribute("myvm2", new SubViewModel("BB"));
		page.setAttribute("myvm3", new SubViewModel("CC"));
	}
	
	static public class SubViewModel {
		String name;
		String value1;
		String value2;
//		String message;

		public SubViewModel(){
			this("XX");
		}
		
		public SubViewModel(String name) {
			this.name = name;
			this.value1 = "V1";
			this.value2 = "V2";
		}

		public String getName() {
			return name;
		}



//		public String getMessage() {
//			return message;
//		}
//
//		private void setMessage(String message) {
//			this.message = message;
//			getBinder().notifyChange(this, "message");
//		}

//		public Validator getValidator1() {
//			return new Validator() {
//				public void validate(ValidationContext ctx) {
//					if(!name.equals(ctx.getProperty().getValue())){
//						setMessage("value must equals to '"+name+"' but is "+ctx.getProperty().getValue());
//						ctx.setInvalid();
//					}else{
//						setMessage(null);
//					}
//				}
//			};
//		}
		
		
		
		public String getValue1() {
			return value1;
		}

		@NotifyChange
		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public String getValue2() {
			return value2;
		}
		@NotifyChange
		public void setValue2(String value2) {
			this.value2 = value2;
		}

		public Converter getConverter1(){
			return new Converter(){

				public Object coerceToBean(Object val, Component component,
						BindContext ctx) {
					return val;
				}

				public Object coerceToUi(Object val, Component component,
						BindContext ctx) {
					return val+"-"+name;
				}
			};
		}
		
		public Converter getConverter(String name){
			if("converter1".equals(name)){
				return getConverter1();
			}
			throw new RuntimeException("no such converter");
		}
		
		@Command @NotifyChange("value2")
		public void cmd1(){
			value2 = "do command1 "+name;
		}
	}


}
