package org.zkoss.zktest.bind.issue;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class B01960BindArray {

  private String[] testArray = {"This", "is", "a", "Test"};
  
  public String[] getTestArray() {
  	return testArray;
  }
}