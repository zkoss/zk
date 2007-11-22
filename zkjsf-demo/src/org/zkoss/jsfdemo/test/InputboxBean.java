/* ConverterTestBean.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 14, 2007 10:30:22 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.zkoss.jsfdemo.reservation.ReservationBean;

/**
 * @author Dennis.Chen
 *
 */
public class InputboxBean {
	int intValue;
	float floatValue;
	String textValue;
	boolean booleanValue;
	double doubleValue;
	BigDecimal decimalValue;
	String comboboxValue;
	String bandboxValue;
	
	
	Date dateValue = new Date();
	Date timeValue = new Date();
	Date calendarValue = new Date();
	int sliderValue = 5;
	String radioGroupValue = null;
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	
	
	public String getBandboxValue() {
		return bandboxValue;
	}
	public void setBandboxValue(String bandboxValue) {
		this.bandboxValue = bandboxValue;
	}
	public double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public boolean isBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	
	
	
	public String getRadioGroupValue() {
		return radioGroupValue;
	}
	public void setRadioGroupValue(String radioGroupValue) {
		this.radioGroupValue = radioGroupValue;
	}
	public Date getCalendarValue() {
		return calendarValue;
	}
	public void setCalendarValue(Date calendarValue) {
		this.calendarValue = calendarValue;
	}
	public int getSliderValue() {
		return sliderValue;
	}
	public void setSliderValue(int sliderValue) {
		this.sliderValue = sliderValue;
	}
	public Date getTimeValue() {
		return timeValue;
	}
	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}
	public BigDecimal getDecimalValue() {
		return decimalValue;
	}
	public void setDecimalValue(BigDecimal decimalValue) {
		this.decimalValue = decimalValue;
	}
	
	
	
	public String getComboboxValue() {
		return comboboxValue;
	}
	public void setComboboxValue(String comboboxValue) {
		this.comboboxValue = comboboxValue;
	}
	public String doSubmit(){
		
		StringBuffer sb = new StringBuffer();
		addMessage("textValue="+textValue);
		addMessage("intValue="+intValue);
		//addMessage("tfloatValue="+floatValue);
		addMessage("doubleValue="+doubleValue);
		addMessage("decimalValue="+decimalValue);
		addMessage("comboboxValue="+comboboxValue);
		addMessage("checkboxValue="+booleanValue);
		addMessage("radioGroupValue="+radioGroupValue);
		addMessage("bandboxValue="+bandboxValue);
		addMessage("sliderValue="+sliderValue);
		addMessage("timeValue="+timeValue==null?null:new SimpleDateFormat("yyyy/MM/dd HH:mm").format(timeValue));
		addMessage("dateValue="+dateValue==null?null:new SimpleDateFormat("yyyy/MM/dd HH:mm").format(dateValue));
		addMessage("calendarValue="+calendarValue==null?null:new SimpleDateFormat("yyyy/MM/dd HH:mm").format(calendarValue));
		
		return null;
	}
	
	private void addMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(message));
	}
	
	
	
	
	
}
