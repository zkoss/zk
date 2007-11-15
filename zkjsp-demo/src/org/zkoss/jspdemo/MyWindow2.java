package org.zkoss.jspdemo;

import org.zkoss.zul.Window;

public class MyWindow2 extends Window{
	public void onOK() {
        System.out.println("========= OK ========");
    }
    public void onCancel(){
       System.out.println("========== Cancel ======");
    }
}
