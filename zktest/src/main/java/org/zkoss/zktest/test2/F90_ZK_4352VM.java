/* F90_ZK_4352VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 19 12:35:43 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class F90_ZK_4352VM {
	private LocalDateTime localDateTime;
	private LocalDate localDate;
	private LocalTime localTime;

	public F90_ZK_4352VM() {
		localDateTime = LocalDateTime.of(2000, 1, 1, 14, 23, 55);
		localDate = LocalDate.of(2000, 1, 1);
		localTime = LocalTime.of(14, 23, 55);
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
	public void showLocalDateTime() {
		Clients.log(localDateTime.toString());
	}

	@Command
	public void showLocalDate() {
		Clients.log(localDate.toString());
	}

	@Command
	public void showLocalTime() {
		Clients.log(localTime.toString());
	}
}
