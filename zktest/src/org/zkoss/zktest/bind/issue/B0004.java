/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author dennis
 * 
 */
public class B0004{
	private int value1;
	private String value2;
	
	public int getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(int value1) {
		this.value1 = value1;
	}
	
	
	
	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	private String lastMessage1;
	private String lastMessage2;
	private String lastMessage3;
	
	public String getLastMessage1() {
		return lastMessage1;
	}

	public void setLastMessage1(String lastMessage1) {
		this.lastMessage1 = lastMessage1;
	}

	public String getLastMessage2() {
		return lastMessage2;
	}

	public void setLastMessage2(String lastMessage2) {
		this.lastMessage2 = lastMessage2;
	}

	
	public String getLastMessage3() {
		return lastMessage3;
	}

	public void setLastMessage3(String lastMessage3) {
		this.lastMessage3 = lastMessage3;
	}

	@Command @NotifyChange("lastMessage3")
	public void cmd1(){
		setLastMessage3("execute command 1");
		
	}
	
	public Validator getValidator1(){
		return new Validator(){
			public void validate(ValidationContext ctx) {
				if(!ctx.isValid()) return;
				Property p = ctx.getProperty();
				Object val = p.getValue();
				if(val!=null && Integer.parseInt(val.toString())>10){
					setLastMessage1(null);
				}else{
					ctx.setInvalid();
					setLastMessage1("value 1 have to large than 10");
				}
				ctx.getBindContext().getBinder().notifyChange(B0004.this, "lastMessage1");
			}			
		};
	}
	
	public Validator getValidator2(){
		return new Validator(){
			public void validate(ValidationContext ctx) {
				if(!ctx.isValid()) return;
				Property p = ctx.getProperty();
				Object val = p.getValue();
				try{
					if(val!=null && Integer.parseInt(val.toString())>20){
						setLastMessage2(null);
					}else{
						ctx.setInvalid();
						setLastMessage2("value 2 have to large than 20");
					}
				}catch(java.lang.NumberFormatException x){
					ctx.setInvalid();
					setLastMessage2("value 2 is not valid " +x.getMessage());
				}
				ctx.getBindContext().getBinder().notifyChange(B0004.this, "lastMessage2");
			}			
		};
	}
	
	public Validator getValidator3(){
		return new Validator(){
			public void validate(ValidationContext ctx) {
				if(!ctx.isValid()) return;
				Object val1 = ctx.getProperties().get("value1")[0].getValue();//ctx.getPropertyValue("value1");
				Object val2 = ctx.getProperties().get("value2")[0].getValue();//ctx.getPropertyValue("value2");//null;
				if(val2==null){ //val2 is deached
					ctx.setInvalid();
					setLastMessage2("value 2 is null");
				}else if(Integer.parseInt(val1.toString())>=Integer.parseInt(val2.toString())){
					ctx.setInvalid();
					setLastMessage2("value 2 have to large than value 1");
				}else{
					setLastMessage2(null);
				}
				ctx.getBindContext().getBinder().notifyChange(B0004.this, "lastMessage2");
			}			
		};
	}
}
