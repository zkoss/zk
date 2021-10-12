/* B85_ZK_3698VM.java

	Purpose:
		
	Description:
		
	History:
		Tue May 22 12:24:00 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class B85_ZK_3698VM {
	private Date dateVal = new Date();
	private String strVal = "123";

	public Date getDateVal() {
		return dateVal;
	}

	public void setDateVal(Date dateVal) {
		this.dateVal = dateVal;
	}

	public String getStrVal() {
		return strVal;
	}

	public void setStrVal(String strVal) {
		this.strVal = strVal;
	}

	@Command
	@NotifyChange({"dateVal", "strVal"})
	public void testSetNull() {
		setStrVal(null);
		setDateVal(null);
	}

	@Command
	@NotifyChange({"dateVal", "strVal"})
	public void reset() {
		setStrVal("123");
		setDateVal(new Date());
	}
}
