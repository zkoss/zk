package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B65_ZK_2506_VM {

  private String name;

  @Command
  @NotifyChange("name")
  public void checkState() {
     name = "Hello there!";
  }

  public String getName() {
     return name;
  }

}