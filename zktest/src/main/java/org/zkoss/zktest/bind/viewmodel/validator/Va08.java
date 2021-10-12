package org.zkoss.zktest.bind.viewmodel.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.NotifyChange;


public class Va08{

	private String keyword;
	private Integer maxLength =3;
	
	public String getKeyword() {
		return keyword;
	}
	
	@NotifyChange
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	

	public Integer getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	

	// ------ validator ------------
	
	public class MaxLengthValidator implements Validator {

		public void validate(ValidationContext ctx) {
			Number maxLength = (Number)ctx.getBindContext().getValidatorArg("length");
//			String maxLength = (String)ctx.getBindContext().getValidatorArg("length");
			if (ctx.getProperty().getValue() instanceof String){
				String value = (String)ctx.getProperty().getValue();
				if (value.length() > maxLength.longValue()){
					ctx.setInvalid();
				}
			}else{
				ctx.setInvalid();
			}
		}
	}	
	public Validator getMaxLengthValidator(){
		return new MaxLengthValidator();
	}

}
