package org.zkoss.jsfdemo.test2;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MyApplierBean {
	private boolean doAfterComposeFlag;
	private boolean value;

	public boolean isDoAfterComposeFlag() {
		return doAfterComposeFlag;
	}

	public void setDoAfterComposeFlag(boolean doAfterCompose) {
		this.doAfterComposeFlag = doAfterCompose;
	}
	
	public String doSubmit(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("doAfterCompose:"+doAfterComposeFlag));
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
