package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B00757OnChange {
	String value11;
	String value12;
	String value21;
	String value22;
	String value31;
	String value32;
	String value41;
	String value42;
	
	public String getValue11() {
		return value11;
	}

	public void setValue11(String value11) {
		this.value11 = value11;
	}

	public String getValue12() {
		return value12;
	}

	public void setValue12(String value12) {
		this.value12 = value12;
	}

	public String getValue21() {
		return value21;
	}

	public void setValue21(String value21) {
		this.value21 = value21;
	}

	public String getValue22() {
		return value22;
	}

	public void setValue22(String value22) {
		this.value22 = value22;
	}
	
	

	public String getValue31() {
		return value31;
	}

	public void setValue31(String value31) {
		this.value31 = value31;
	}

	public String getValue32() {
		return value32;
	}

	public void setValue32(String value32) {
		this.value32 = value32;
	}

	public String getValue41() {
		return value41;
	}

	public void setValue41(String value41) {
		this.value41 = value41;
	}

	public String getValue42() {
		return value42;
	}

	public void setValue42(String value42) {
		this.value42 = value42;
	}

	@Command @NotifyChange("value12")
	public void cmd1(){
		this.value12 = value11+"-X";
	}
	
	@Command @NotifyChange("value22")
	public void cmd2(){
		this.value22 = value21+"-Y";
	}
	
	@Command @NotifyChange({"value32","value42"})
	public void cmd3(){
		this.value32 = value31+"-I";
		this.value42 = value41+"-J";
	}
	
	public Validator getValidator1(){
		return new Validator() {
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"B".equals(val)){
					ctx.setInvalid();
				}
			}
		};
	}
	
	public Validator getValidator2(){
		return new Validator() {
			public void validate(ValidationContext ctx) {
				String val = (String)ctx.getProperty().getValue();
				if(!"C".equals(val)){
					ctx.setInvalid();
				}
			}
		};
	}
}
