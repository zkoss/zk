package org.zkoss.zktest.bind.viewmodel.validator;

import static java.lang.System.out;
import java.util.Date;
import java.util.Map;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;


public class Va10{

	private Integer age = 0;
	private Integer negativeOne = -1;
	private String keyword;
	private Integer maxLength =3;
	private Integer limit = 18;
	private boolean isAdult = false;
	private Date startDate;
	private Date endDate;
	
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
	
	public String getKeyword() {
		return keyword;
	}
	
	@NotifyChange
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public boolean isAdult() {
		return isAdult;
	}

	@NotifyChange
	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}

	public Date getStartDate() {
		return startDate;
	}

	@NotifyChange
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@NotifyChange
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
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

	public class DurationValidator implements Validator {

		public void validate(ValidationContext ctx) {
			String startProperty = (String)ctx.getBindContext().getValidatorArg("start");
			String endProperty = (String)ctx.getBindContext().getValidatorArg("end");
			Map<String, Property[]> properties = ctx.getProperties();
			Date startDate = (Date)properties.get(startProperty)[0].getValue();
			Date endDate = (Date)properties.get(endProperty)[0].getValue();
			if (startDate==null || endDate==null || endDate.before(startDate)){
				ctx.setInvalid();
			}
		}
	}	
	public Validator getDurationValidator(){
		return new DurationValidator();
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

	public class AgeLimitIndicator implements Converter {

		public Object coerceToUi(Object val, Component component, BindContext ctx) {

			Number limit = (Number)ctx.getConverterArg("limit");
			Integer age = (Integer)val;
			if (age >= limit.longValue()){
				return "Over age "+limit;
			}
			return "Under Age "+limit;
		}
		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			return null;
		}
	}
	
	public Converter getAgeLimitIndicator(){
		return new AgeLimitIndicator();
	}
	

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
	public void submit(){
		out.println("current age is "+age);
	}
	public void submit5(){
		submit();
	}
	public void checkAdult(){
		out.println("is Adult: "+isAdult);
	}	
	
	public void add(Map<String, Object> args){
		Long increment = (Long)args.get("increment");
		age += increment.intValue();
	}

	public void add10(){
		age += 10;
	}
	
	public void ok(){
		
	}
}
