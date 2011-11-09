/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.zbind.basic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import org.zkoss.bind.BindComposer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.DependsOn;
import org.zkoss.bind.NotifyChange;
import org.zkoss.zk.ui.Component;

/**
 * @author Dennis Chen
 * 
 */
public class ConverterComposer extends BindComposer {
	private Date bday1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	public ConverterComposer() {
		try {
			bday1 = sdf.parse("1975/02/13");
		} catch (ParseException e) {
			throw new RuntimeException();
		}
		addConverter("myconverter1", new Converter() {
			
//			@DependsOn(""), has issue of depends-on scope.
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				Date d = (Date)val;
				if(d==null){
					return null;
				}
				return sdf.format(d);
			}
			
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				String v = (String)val;
				if(v==null){
					return null;
				}
				try {
					return sdf.parse(v);
				} catch (ParseException e) {
					return null;
				}
			}
		});
	}

	public Date getBday1() {
		return bday1;
	}

	@NotifyChange
	public void setBday1(Date bday1) {
		this.bday1 = bday1;
	}

	//value DependsOn will tie to the last name(string before last dot + this attr) of the evaluation, 
	@DependsOn("bday1")
	public int getAge1() {
		Calendar c = null;
		if(bday1!=null){
			c = Calendar.getInstance();
			c.setTime(bday1);
		}else{
			return 0;
		}
		return Calendar.getInstance().get(Calendar.YEAR) - c.get(Calendar.YEAR);
	}
	
	
	@NotifyChange(".")
	public void saveForm(){
		//notify vm is changed
	}
}
