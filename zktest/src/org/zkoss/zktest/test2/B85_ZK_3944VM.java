/* B85_ZK_3944VM.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 27 11:39:08 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.sql.Timestamp;
import java.util.Date;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B85_ZK_3944VM {
	private static long time = 1000000000;
	private Date myDate = new Timestamp(time);
	
	@Command
	@NotifyChange("myDate")
	public void update() {
		myDate = new Date(time);
	}
	
	public Date getMyDate() {
		return myDate;
	}
}
