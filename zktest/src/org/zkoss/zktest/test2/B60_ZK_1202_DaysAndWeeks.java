package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class B60_ZK_1202_DaysAndWeeks {
  private List<B60_ZK_1202_DayWeekModel> dayWeekModels;
  
  public List<B60_ZK_1202_DayWeekModel> getDayWeekModels() {
    return dayWeekModels;
  }

  @Init
  public void init() throws Exception {
    List<String> weekTypes = new ArrayList<String>();
    weekTypes.add("Regular Week");
    weekTypes.add("Working Week");

    dayWeekModels = new ArrayList<B60_ZK_1202_DayWeekModel>();
    dayWeekModels.add(new B60_ZK_1202_DayWeekModel(weekTypes));
  }

}
