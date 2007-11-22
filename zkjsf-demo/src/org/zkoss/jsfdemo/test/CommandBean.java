/* CommandBean.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2007 3:16:24 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * @author Dennis.Chen
 *
 */
public class CommandBean {

	
	public void doSubmit1(){
		addMessage("1.doSubmit-1:"+new Date());
	}
	
	public void onSubmit1(ActionEvent event){
		addMessage("2.onSubmit-1:"+event+new Date());
	}
	public void doSubmit2(){
		addMessage("3.doSubmit-2:"+new Date());
	}
	
	public void onSubmit2(ActionEvent event){
		addMessage("4.onSubmit-2:"+event+new Date());
	}
	public void doSubmit3(){
		addMessage("5.doSubmit-3:"+new Date());
	}
	
	public void onSubmit3(ActionEvent event){
		addMessage("6.onSubmit-3:"+event+new Date());
	}
	public void doSubmit4(){
		addMessage("7.doSubmit-4:"+new Date());
	}
	
	public void onSubmit4(ActionEvent event){
		addMessage("8.onSubmit-4:"+event+new Date());
	}
	
	private void addMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(message));
	}
}
