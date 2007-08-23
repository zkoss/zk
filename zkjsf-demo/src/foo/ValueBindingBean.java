/* ValidatorBean.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 2007/8/22 3:01:00 PM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package foo;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Dennis.Chen
 * 
 */
public class ValueBindingBean {

	String text;

	int number;

	Date date;

	String selection;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String doSubmit() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(
				"submitted text:" + text));
		context.addMessage(null, new FacesMessage(
				"submitted number:" + number));
		context.addMessage(null, new FacesMessage(
				"submitted date:" + date));
		context.addMessage(null, new FacesMessage(
				"submitted selection:" + selection));
		return null;
	}

}
