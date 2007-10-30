package org.zkoss.demo;

import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Window;

public class MyWindow extends Window {
	Person person;
	
	public void dump(){
		
		System.out.println(person);
	}
}
