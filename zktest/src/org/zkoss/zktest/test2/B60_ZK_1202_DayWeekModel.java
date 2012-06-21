package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.NotifyChange;

public class B60_ZK_1202_DayWeekModel {
  private static final List<String> WEEKDAYS = new ArrayList<String>();
  private static final List<String> WORKDAYS = new ArrayList<String>();
  
  static {
    WEEKDAYS.add("Sunday");
    WEEKDAYS.add("Monday");
    WEEKDAYS.add("Tuesday");
    WEEKDAYS.add("Wednesday");
    WEEKDAYS.add("Thursday");
    WEEKDAYS.add("Friday");
    WEEKDAYS.add("Saturday");

    WORKDAYS.add("Monday");
    WORKDAYS.add("Tuesday");
    WORKDAYS.add("Wednesday");
    WORKDAYS.add("Thursday");
    WORKDAYS.add("Friday");
  }
 
  private List<String> weekTypes;
  private List<String> days;
 
  private String selectedWeekType;
  private String selectedDay;

  public B60_ZK_1202_DayWeekModel(List<String> weekTypes) {
    this.weekTypes = weekTypes;
    this.days      = new ArrayList<String>();
  }

  public List<String> getWeekTypes() {
    return this.weekTypes;
  }

  public List<String> getDays() {
    return this.days;
  }

  @NotifyChange({ "days", "selectedDay" })
  public void setSelectedWeekType(String selectedWeekType) {
    this.selectedWeekType = selectedWeekType;
    if (selectedWeekType.equals("Regular Week")) {
      this.days = WEEKDAYS;
      setSelectedDay(this.days.get(0));
    } else {
      this.days = WORKDAYS;
      setSelectedDay(this.days.get(0));
    }
  }

  public String getSelectedWeekType() {
    return selectedWeekType;
  }

  public void setSelectedDay(String selectedDay) {
    this.selectedDay = selectedDay;
  }

  public String getSelectedDay() {
    return selectedDay;
  }
}
