/* F104_ZK_4305_BindingVM.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 15:14:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;

import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DateRange;

public class F104_ZK_4305_BindingVM {
	private DateRange range;
	private Date from;
	private Date to;

	public DateRange getRange() {
		return range;
	}

	@NotifyChange("range")
	public void setRange(DateRange range) {
		this.range = range;
	}

	public Date getFrom() {
		return from;
	}

	@NotifyChange("from")
	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	@NotifyChange("to")
	public void setTo(Date to) {
		this.to = to;
	}
}
