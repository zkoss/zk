package org.zkoss.zktest.bind.viewmodel.validator;

import java.util.Map;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;


public class Va03{

	private Integer age = 0;
	
	public Integer getAge() {
		return age;
	}

	@NotifyChange
	public void setAge(Integer age) {
		this.age = age;
	}



	// ------ validator ------------
	public class LowerBoundValidator implements Validator {

		public void validate(ValidationContext ctx) {
			Number lowerBound = (Number)ctx.getBindContext().getValidatorArg("min");
			if (ctx.getProperty().getValue() instanceof Number){
				Number value = (Number)ctx.getProperty().getValue();
				if (value.longValue() < lowerBound.longValue()){
					ctx.setInvalid();
				}
			}else{
				ctx.setInvalid();
			}
		}
	}	
	public Validator getLowerBoundValidator(){
		return new LowerBoundValidator();
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
	
	public void minus(Map<String, Object> args){
		Long decrement = (Long)args.get("decrement");
		age -= decrement.intValue();
	}

}
