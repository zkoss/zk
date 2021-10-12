package org.zkoss.zktest.bind.issue;

import java.util.Date;

import org.zkoss.bind.annotation.NotifyChange;

public class B00682 {

	String stringValue;
	Date dateValue;
	Double decimalValue = -1D;
	Double doubleValue = -2D;
	Integer intValue = -3;
	Long longValue = -4L;
	public String getStringValue() {
		return stringValue;
	}
	
	@NotifyChange
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Date getDateValue() {
		return dateValue;
	}
	@NotifyChange
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public Double getDecimalValue() {
		return decimalValue;
	}
	@NotifyChange
	public void setDecimalValue(Double decimalValue) {
		this.decimalValue = decimalValue;
	}
	public Double getDoubleValue() {
		return doubleValue;
	}
	@NotifyChange
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public Integer getIntValue() {
		return intValue;
	}
	@NotifyChange
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	public Long getLongValue() {
		return longValue;
	}
	@NotifyChange
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}
	
	
	
}
