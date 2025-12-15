/* B103_ZK_6006_VM.java

        Purpose:

        Description:

        History:
                Thu Oct 30 17:44:57 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Messagebox;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;

@VariableResolver(DelegatingVariableResolver.class)
public class B103_ZK_6006_VM {
    private LocalTime timeValue = LocalTime.of(13, 40);
    private LocalDate dateValue = LocalDate.of(2025, 9, 12);
    private LocalDateTime dateTimeValue = LocalDateTime.of(2025, 9, 12, 13, 40);

    public LocalTime getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(LocalTime timeValue) {
        this.timeValue = timeValue;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    public LocalDateTime getDateTimeValue() {
        return dateTimeValue;
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }

    @Init
    public void init() {

    }

    @Command
    public void showValue() {
        Messagebox.show("Time : " + timeValue + ", Date : " + dateValue + ", DateTime : " + dateTimeValue);
    }
}
