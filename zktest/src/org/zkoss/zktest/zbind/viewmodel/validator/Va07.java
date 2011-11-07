package org.zkoss.zktest.zbind.viewmodel.validator;

import org.zkoss.bind.NotifyChange;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;


public class Va07{

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
}
