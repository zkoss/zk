/* B104_ZK_5860VM.java

    Purpose:
        
    Description:
        
    History:
        Thu Jan 29 13:05:35 CST 2026, Created by josephlo

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
	private Object selectedDateConstraint;
	
	@Init
	public void init(){
	    Calendar calendar=Calendar.getInstance();
	    selectedDate=calendar.getTime();
	    calendar.add(Calendar.DATE,2);

	    selectedDateConstraint="between " + convertDateToString(selectedDate, "yyyyMMdd") + " and " + convertDateToString(calendar.getTime(), "yyyyMMdd");
	}

	@Command
	@NotifyChange({"selectedDateConstraint","selectedDate"})
	public void changeDate(){
	    Calendar calendar=Calendar.getInstance();
	    calendar.add(Calendar.MINUTE,20);

	    selectedDate=calendar.getTime();
	    calendar.add(Calendar.DATE,4);

	    selectedDateConstraint="between " + convertDateToString(selectedDate, "yyyyMMdd") + " and " + convertDateToString(calendar.getTime(), "yyyyMMdd");
	}
	
	private String convertDateToString(Date dt, String format) {
		return new SimpleDateFormat(format).format(dt);
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}

	public Object getSelectedDateConstraint() {
		return selectedDateConstraint;
	}

	public void setSelectedDateConstraint(Object selectedDateConstraint) {
		this.selectedDateConstraint = selectedDateConstraint;
	}
}
