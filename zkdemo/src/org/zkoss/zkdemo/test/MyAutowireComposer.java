/* MyAutowireComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 11, 2008 3:14:51 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * @author henrichen
 *
 */
public class MyAutowireComposer extends GenericAutowireComposer {
	 private Textbox mytextbox;
	 private Label mylabel;
	 private Window win1;
	 private Window main;
	 private int count;
	 
     public void onOK() {
         main.setTitle("main:"+ ++count + ", pageid:"+page.getId());
         win1.setTitle("win1:"+ count);
         mylabel.setValue("label:"+ count+", selfid:"+self.getId());
         mytextbox.setValue("Enter Pressed"+ count);
         mytextbox.focus();
     }
}
