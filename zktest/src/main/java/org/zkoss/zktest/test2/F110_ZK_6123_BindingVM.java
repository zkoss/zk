/* F110_ZK_6123_BindingVM.java

        Purpose:

        Description:

        History:
                Wed Aug 19 10:12:04 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.zkoss.bind.annotation.NotifyChange;

/**
 * ViewModel for the six begin/end Daterangebox properties in the java.time
 * types. Every field starts at a known non-null date so a broken save (the
 * value stays at the initial date) is distinguishable from a broken load (the
 * label renders empty). The {@code *Text} getters render only the ISO local
 * date so the assertions stay independent of the component's time zone.
 */
public class F110_ZK_6123_BindingVM {
	private LocalDate beginLocalDate = LocalDate.of(2020, 1, 5);
	private LocalDate endLocalDate = LocalDate.of(2020, 1, 20);
	private LocalDateTime beginLocalDateTime = LocalDateTime.of(2020, 2, 5, 12, 0);
	private LocalDateTime endLocalDateTime = LocalDateTime.of(2020, 2, 20, 12, 0);
	private ZonedDateTime beginZonedDateTime = ZonedDateTime.of(2020, 3, 5, 12, 0, 0, 0, ZoneId.systemDefault());
	private ZonedDateTime endZonedDateTime = ZonedDateTime.of(2020, 3, 20, 12, 0, 0, 0, ZoneId.systemDefault());

	public LocalDate getBeginLocalDate() {
		return beginLocalDate;
	}

	@NotifyChange({ "beginLocalDate", "beginLocalDateText" })
	public void setBeginLocalDate(LocalDate beginLocalDate) {
		this.beginLocalDate = beginLocalDate;
	}

	public LocalDate getEndLocalDate() {
		return endLocalDate;
	}

	@NotifyChange({ "endLocalDate", "endLocalDateText" })
	public void setEndLocalDate(LocalDate endLocalDate) {
		this.endLocalDate = endLocalDate;
	}

	public LocalDateTime getBeginLocalDateTime() {
		return beginLocalDateTime;
	}

	@NotifyChange({ "beginLocalDateTime", "beginLocalDateTimeText" })
	public void setBeginLocalDateTime(LocalDateTime beginLocalDateTime) {
		this.beginLocalDateTime = beginLocalDateTime;
	}

	public LocalDateTime getEndLocalDateTime() {
		return endLocalDateTime;
	}

	@NotifyChange({ "endLocalDateTime", "endLocalDateTimeText" })
	public void setEndLocalDateTime(LocalDateTime endLocalDateTime) {
		this.endLocalDateTime = endLocalDateTime;
	}

	public ZonedDateTime getBeginZonedDateTime() {
		return beginZonedDateTime;
	}

	@NotifyChange({ "beginZonedDateTime", "beginZonedDateTimeText" })
	public void setBeginZonedDateTime(ZonedDateTime beginZonedDateTime) {
		this.beginZonedDateTime = beginZonedDateTime;
	}

	public ZonedDateTime getEndZonedDateTime() {
		return endZonedDateTime;
	}

	@NotifyChange({ "endZonedDateTime", "endZonedDateTimeText" })
	public void setEndZonedDateTime(ZonedDateTime endZonedDateTime) {
		this.endZonedDateTime = endZonedDateTime;
	}

	public String getBeginLocalDateText() {
		return format(beginLocalDate);
	}

	public String getEndLocalDateText() {
		return format(endLocalDate);
	}

	public String getBeginLocalDateTimeText() {
		return beginLocalDateTime == null ? "" : format(beginLocalDateTime.toLocalDate());
	}

	public String getEndLocalDateTimeText() {
		return endLocalDateTime == null ? "" : format(endLocalDateTime.toLocalDate());
	}

	public String getBeginZonedDateTimeText() {
		return beginZonedDateTime == null ? "" : format(beginZonedDateTime.toLocalDate());
	}

	public String getEndZonedDateTimeText() {
		return endZonedDateTime == null ? "" : format(endZonedDateTime.toLocalDate());
	}

	private static String format(LocalDate d) {
		return d == null ? "" : d.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
}
