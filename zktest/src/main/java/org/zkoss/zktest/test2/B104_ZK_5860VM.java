/* B104_ZK_5860VM.java

	Purpose:

	Description:

	History:
		Tue Feb 10 11:57:52 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class B104_ZK_5860VM {
	private Date selectedDate;
	private String selectedDateConstraint;

	@Init
	public void init() {
		Calendar calendar = Calendar.getInstance();
		selectedDate = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		selectedDateConstraint = "between " + format(selectedDate) + " and " + format(calendar.getTime());
	}

	@Command
	@NotifyChange({"selectedDateConstraint", "selectedDate"})
	public void changeDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 20);
		selectedDate = calendar.getTime();
		calendar.add(Calendar.DATE, 4);
		selectedDateConstraint = "between " + format(selectedDate) + " and " + format(calendar.getTime());
	}

	private static String format(Date dt) {
		return new SimpleDateFormat("yyyyMMdd").format(dt);
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public String getSelectedDateConstraint() {
		return selectedDateConstraint;
	}
}
