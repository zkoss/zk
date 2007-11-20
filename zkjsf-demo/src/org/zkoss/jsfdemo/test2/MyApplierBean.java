package org.zkoss.jsfdemo.test2;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MyApplierBean {
	private boolean doAfterCompose;
	private boolean value;

	public boolean isDoAfterCompose() {
		return doAfterCompose;
	}

	public void setDoAfterCompose(boolean doAfterCompose) {
		this.doAfterCompose = doAfterCompose;
	}
	
	public String doSubmit(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("doAfterCompose:"+doAfterCompose));
		context.addMessage(null, new FacesMessage("value:"+value));
		return null;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
}
