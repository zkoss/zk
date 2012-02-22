package org.zkoss.zktest.bind.issue;

import java.util.Calendar;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class B00878WrongValueException2 {
	
	String bandboxVal;
	String comboboxVal;
	Date dateboxVal;
	Double decimalboxVal;
	Double doubleboxVal;
	Double doublespinnerVal;
	Integer intboxVal;
	Long longboxVal;
	Integer spinnerVal;
	String textboxVal;
	Date timeboxVal;
	public String getBandboxVal() {
		return bandboxVal;
	}
	public void setBandboxVal(String bandboxVal) {
		this.bandboxVal = bandboxVal;
	}
	public String getComboboxVal() {
		return comboboxVal;
	}
	public void setComboboxVal(String comboboxVal) {
		this.comboboxVal = comboboxVal;
	}
	public Date getDateboxVal() {
		return dateboxVal;
	}
	public void setDateboxVal(Date dateboxVal) {
		this.dateboxVal = dateboxVal;
	}
	public Double getDecimalboxVal() {
		return decimalboxVal;
	}
	public void setDecimalboxVal(Double decimalboxVal) {
		this.decimalboxVal = decimalboxVal;
	}
	public Double getDoubleboxVal() {
		return doubleboxVal;
	}
	public void setDoubleboxVal(Double doubleboxVal) {
		this.doubleboxVal = doubleboxVal;
	}
	public Double getDoublespinnerVal() {
		return doublespinnerVal;
	}
	public void setDoublespinnerVal(Double doublespinnerVal) {
		this.doublespinnerVal = doublespinnerVal;
	}
	public Integer getIntboxVal() {
		return intboxVal;
	}
	public void setIntboxVal(Integer intboxVal) {
		this.intboxVal = intboxVal;
	}
	public Long getLongboxVal() {
		return longboxVal;
	}
	public void setLongboxVal(Long longboxVal) {
		this.longboxVal = longboxVal;
	}
	public Integer getSpinnerVal() {
		return spinnerVal;
	}
	public void setSpinnerVal(Integer spinnerVal) {
		this.spinnerVal = spinnerVal;
	}
	public String getTextboxVal() {
		return textboxVal;
	}
	public void setTextboxVal(String textboxVal) {
		this.textboxVal = textboxVal;
	}
	public Date getTimeboxVal() {
		return timeboxVal;
	}
	public void setTimeboxVal(Date timeboxVal) {
		this.timeboxVal = timeboxVal;
	}
	
	public static class TimeConstraint implements Constraint{

		@Override
		public void validate(Component comp, Object value) throws WrongValueException {
			Date date = (Date)value;
			int h = -1;
			if(date!=null){
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				h = c.get(Calendar.HOUR_OF_DAY);
			}
			if(h < 12){
				throw new WrongValueException(comp,"Hours must large than 12, but is "+h);
			}
		}
		
	}
}
