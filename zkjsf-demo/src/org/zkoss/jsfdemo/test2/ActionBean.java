package org.zkoss.jsfdemo.test2;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

public class ActionBean {
	
	private String value;
	
	
	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}


	public void onAction(ActionEvent event){
			
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("onAction:Process value :"+value));
	}
	
	public void validateString(FacesContext context,
            UIComponent component,
            Object value) throws ValidatorException {
			throw new ValidatorException(new FacesMessage("Warming !"));
	}
	
	public String doSubmit(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Process value:"+value));
		return null;
	}
}
