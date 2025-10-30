/* ConverterVM.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.converter;

import java.util.Date;

import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Init;

/**
 * @author jameschu
 */
public class ConverterVM {
	private Date creationDate = new Date();
	private String message = "123";
	private double price = 100;
	private Date time = new Date();
	private Converter myConverter = new MyDateFormatConverter();

	@Init
	public void init() {
	}

	public Converter getMyConverter() {
		return myConverter;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
