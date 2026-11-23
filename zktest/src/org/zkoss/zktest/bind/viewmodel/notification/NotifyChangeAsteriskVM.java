/* NotifyChangeSelfVM.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 12:26:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.notification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class NotifyChangeAsteriskVM {
	private final TimeRange range = new TimeRange();

	public TimeRange getRange() {
		return range;
	}

	public static class TimeRange {
		private LocalDate start = LocalDate.of(2021, 1, 1);
		private LocalDate end = LocalDate.of(2021, 3, 31);

		public LocalDate getStart() {
			return start;
		}

		@NotifyChange("*") // Update all properties, but without self
		public void setStart(LocalDate start) {
			this.start = start;
		}

		public LocalDate getEnd() {
			return end;
		}

		@NotifyChange("*")
		public void setEnd(LocalDate end) {
			this.end = end;
		}

		@Override
		public String toString() {
			return String.valueOf(ChronoUnit.DAYS.between(start, end));
		}
	}
}
