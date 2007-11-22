/* Window.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 19, 2007 7:29:44 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import org.zkoss.zul.Messagebox;

/**
 * @author Dennis.Chen
 *
 */
public class ForwardWindow extends org.zkoss.zul.Window {

	
	public void onOK(){
		try {
			Messagebox.show("on OK");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onCancel(){
		try {
			Messagebox.show("on Cancel");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
