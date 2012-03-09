package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;

public class F00921SystemConverterValidator {

	Binder binder;
	
	String value1 = "A";
	String value2 = "B";
	
	

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	@Init
	public void init(@ContextParam(ContextType.BINDER) Binder binder) {
		this.binder = binder;
	}

	public String getConverterName1() {
		return binder.getConverter("xConverter").getClass().getSimpleName();
	}

	public String getConverterName2() {
		return binder.getConverter("yConverter").getClass().getSimpleName();
	}

	public String getValidatorName1() {
		return binder.getValidator("xValidator").getClass().getSimpleName();
	}

	public String getValidatorName2() {
		return binder.getValidator("yValidator").getClass().getSimpleName();
	}

	public static class XConverter implements Converter {

		@Override
		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			// TODO Auto-generated method stub
			return val+"X";
		}

		@Override
		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class YConverter implements Converter {

		@Override
		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			// TODO Auto-generated method stub
			return val+"Y";
		}

		@Override
		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class XValidator implements Validator {

		@Override
		public void validate(ValidationContext ctx) {
			// TODO Auto-generated method stub
		}
	}

	public static class YValidator implements Validator {

		@Override
		public void validate(ValidationContext ctx) {
			// TODO Auto-generated method stub
		}
	}
}