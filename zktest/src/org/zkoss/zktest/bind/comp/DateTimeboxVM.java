/* DateTimeboxVM.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 11:26:51 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.comp;

import java.util.Date;

/**
 * @author rudyhuang
 */
public class DateTimeboxVM {
	private Date date = new Date();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
