package org.zkoss.zktest.bind.viewmodel.collection;

import static java.lang.System.out;
import java.util.List;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;



public class C1{
	private boolean isSingle = true;
	private List<String> selectedList;
	private String selectedOne = ListPool.getListNameList().get(0);
	

	public boolean isSingle() {
		return isSingle;
	}
	
	@NotifyChange
	public void setSingle(boolean isSingle) {
		this.isSingle = isSingle;
		out.println(isSingle);
	}
	
	public String getSelectedOne() {
		return selectedOne;
	}

	@NotifyChange
	public void setSelectedOne(String selectedOne) {
		this.selectedOne = selectedOne;
	}

	@DependsOn("selectedOne")
	public List<String> getSelectedList() {
		if (selectedOne.equals("Fruit")){
			selectedList = getFruitList();
		}else{
			selectedList = getCarMarkList();
		}
		return selectedList;
	}


	public List<String> getCarMarkList(){
		return ListPool.getCarMarkList();
	}
	
	public List<String> getFruitList(){
		return ListPool.getFruitList();
	}
	
	public List<String> getListNameList(){
		return ListPool.getListNameList();
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
	
	//--------- converter ------------
	/*
	 * reverse the boolean value
	 */
	public class NotConverter implements Converter{

		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			return !(Boolean)val;
		}

		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			
			return !(Boolean)val;
		}

	}	
	public Converter getNotConverter(){
		return new NotConverter();
	}

	
	// -----------command -----------------
	
}
