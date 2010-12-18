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

import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * Test code for GenericForwardComposer.
 * Enter "xxx" in textbox, show "You entered: xxx" in label.
 * @author henrichen
 *
 */
public class MyForwardComposer extends GenericForwardComposer {
	 private Textbox mytextbox;
	 private Label mylabel;
	 
	 public void setMylabel(Label lb) {
		 mylabel = lb;
	 }
     public void onChange$mytextbox() {
         mylabel.setValue("You entered: "+mytextbox.getValue());
         mytextbox.focus();
     }
}
