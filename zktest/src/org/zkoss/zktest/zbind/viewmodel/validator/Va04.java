package org.zkoss.zktest.zbind.viewmodel.validator;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.NotifyChange;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.zk.ui.Component;


public class Va04{

	private Integer age = 0;
	private Integer positiveOne = 1;

	
	public Integer getPositiveOne() {
		return positiveOne;
	}

	public void setPositiveOne(Integer positiveOne) {
		this.positiveOne = positiveOne;
	}

	public Integer getAge() {
		return age;
	}

	@NotifyChange
	public void setAge(Integer age) {
		this.age = age;
	}
	

	// ------ validator ------------
	
	public class UpperBoundValidator implements Validator {

		public void validate(ValidationContext ctx) {
			Number upperBound = (Number)ctx.getBindContext().getValidatorArg("max");
			if (ctx.getProperty().getValue() instanceof Number){
				Number value = (Number)ctx.getProperty().getValue();
				if (value.longValue() > upperBound.longValue()){
					ctx.setInvalid();
				}
			}else{
				ctx.setInvalid();
			}
		}
	}	
	public Validator getUpperBoundValidator(){
		return new UpperBoundValidator();
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
	
	public void add(Map<String, Object> args){
		Long increment = (Long)args.get("increment");
		age += increment.intValue();
	}

}
