package org.zkoss.zktest.test2;

import java.io.Serializable;
import java.util.Date;

public class F80_ZK_3185SubBean implements Serializable {

	private String name;

	private Date date;

	public F80_ZK_3185SubBean() {

	}

	public F80_ZK_3185SubBean(String pName, Date pDate) {
		name = pName;
		date = pDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
