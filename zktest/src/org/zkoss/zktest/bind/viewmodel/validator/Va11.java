package org.zkoss.zktest.bind.viewmodel.validator;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.zk.ui.Component;


public class Va11{

	private Integer age = 0;
	private Integer negativeOne = -1;
	private boolean isLessThanThirteen = true;
	private boolean isLessThanEighteen = true;
	private boolean isOverEighteen = false;
	
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	public Integer getNegativeOne() {
		return negativeOne;
	}
	
	public void setNegativeOne(Integer negativeOne) {
		this.negativeOne = negativeOne;
	}
	
	@DependsOn("age")
	public boolean isLessThanThirteen() {
		isLessThanThirteen = age < 13;
		return isLessThanThirteen;
	}

	public void setLessThanThirteen(boolean isLessThanThirteen) {
		this.isLessThanThirteen = isLessThanThirteen;
	}

	@DependsOn("age")
	public boolean isLessThanEighteen() {
		isLessThanEighteen = age < 18;
		return isLessThanEighteen;
	}

	public void setLessThanEighteen(boolean isLessThanEighteen) {
		this.isLessThanEighteen = isLessThanEighteen;
	}

	@DependsOn("age")
	public boolean isOverEighteen() {
		isOverEighteen = age >= 18;
		return isOverEighteen;
	}

	public void setOverEighteen(boolean isOverEighteen) {
		this.isOverEighteen = isOverEighteen;
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
	public void classify(){
//		if (age < 13){
//			isLessThanThirteen = true;
//			isLessThanEighteen = true;
//			isOverEighteen = false;
//		}else if (age < 18){
//			isLessThanThirteen = false;
//			isLessThanEighteen = true;
//			isOverEighteen = false;
//		}else{
//			isLessThanThirteen = false;
//			isLessThanEighteen = false;
//			isOverEighteen = true;
//		}
	}
}
