/* F90_ZK_4347VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 08 10:35:49 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class F90_ZK_4347VM {
	private static final Instant FAKE_INSTANT = Instant.parse("2020-01-01T00:00:00.00Z");
	private static final Clock FAKE_CLOCK = Clock.fixed(FAKE_INSTANT, ZoneId.of("Europe/Berlin")); // UTC+1

	private Date date = Date.from(FAKE_INSTANT);
	private ZonedDateTime zonedDateTime = ZonedDateTime.now(FAKE_CLOCK);
	private LocalDateTime localDateTime = LocalDateTime.now(FAKE_CLOCK);
	private LocalDate localDate = LocalDate.now(FAKE_CLOCK);
	private LocalTime localTime = LocalTime.now(FAKE_CLOCK);

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ZonedDateTime getZonedDateTime() {
		return zonedDateTime;
	}

	public void setZonedDateTime(ZonedDateTime zonedDateTime) {
		this.zonedDateTime = zonedDateTime;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	public LocalTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

	@Command
	public void showNow() {
		Clients.alert(
			new StringBuilder()
				.append(date).append("\n")
				.append(zonedDateTime).append("\n")
				.append(localDateTime).append("\n")
				.append(localDate).append("\n")
				.append(localTime).append("\n")
				.toString()
		);
	}
}
