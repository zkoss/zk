package org.zkoss.zktest.zbind.viewmodel.validator;

import static java.lang.System.out;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.NotifyChange;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.zk.ui.Component;


public class Va01{

	private Integer age = 0;
	private Integer negativeOne = -1;
	
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
	

	// ------ validator ------------

	public class NonNegativeValidator implements Validator {

		public void validate(ValidationContext ctx) {
			
			if (ctx.getProperty().getValue() instanceof Integer){
				Integer value = (Integer)ctx.getProperty().getValue();
				if (value < 0){
					ctx.setInvalid();
				}
			}else{
				ctx.setInvalid();
			}
		}

	}
	public Validator getNonNegative(){
		return new NonNegativeValidator();
	}
	
	//--------- converter ------------
	
	public class MaturityIndicator implements Converter {

		public Object coerceToUi(Object val, Component component, BindContext ctx) {

			Integer age = (Integer)val;
			if (age >= 18){
				return "Adult";
			}
			return "Under Age";
		}
		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			return null;
		}
	}	
	public Converter getMaturityIndicator(){
		return new MaturityIndicator();
	}

	// -----------command -----------------
	public void submit(){
		out.println("current age is "+age);
	}
}
