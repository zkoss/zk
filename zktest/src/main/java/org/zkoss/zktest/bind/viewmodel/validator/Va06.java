package org.zkoss.zktest.bind.viewmodel.validator;

import static java.lang.System.out;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;


public class Va06{

	private Integer age = 0;
	private Integer negativeOne = -1;
	private boolean isAdult = false;
	
	public Integer getAge() {
		return age;
	}

	@NotifyChange
	public void setAge(Integer age) {
		this.age = age;
	}
	
	public Integer getNegativeOne() {
		return negativeOne;
	}
	
	public void setNegativeOne(Integer negativeOne) {
		this.negativeOne = negativeOne;
	}
	
	
	public boolean isAdult() {
		return isAdult;
	}

	@NotifyChange
	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}

	

	// ------ validator ------------
	public class BooleanValidator implements Validator {

		public void validate(ValidationContext ctx) {
			
			if (!(ctx.getProperty().getValue() instanceof Boolean)){
					ctx.setInvalid();
			}
		}

	}	
	public Validator getBooleanValidator(){
		return new BooleanValidator();
	}	
	//--------- converter ------------
	
	public class AdultConverter implements Converter{

		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			return val;
		}

		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			
			Integer age = (Integer)val;
			if (age >= 18){
				return new Boolean(true);
			}
			return new Boolean(false);
		}

	}
	
	public Converter getAdultConverter(){
		return new AdultConverter();
	}
	
	// -----------command -----------------
	@Command 
	public void checkAdult(){
		out.println("is Adult: "+isAdult);
	}	
	
}
