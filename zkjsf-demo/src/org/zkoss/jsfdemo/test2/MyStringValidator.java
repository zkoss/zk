package org.zkoss.jsfdemo.test2;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class MyStringValidator implements Validator {

	private static final int LENGTH = 10;
	
	public void validate(FacesContext context, UIComponent comp, Object obj)
			throws ValidatorException {
		if(context ==null || comp == null || obj ==null){
			throw new NullPointerException();
		}else{
			//String value = ((UIOutput)comp).getValue().toString();
			String value = obj.toString();
			if(value.length()<LENGTH){
				context.addMessage(null, new FacesMessage("The length of string has to be at less:"+LENGTH));
			}
		}

	}

}
