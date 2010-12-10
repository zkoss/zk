/* Tests.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Mar 16 11:55:16     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

/**
 * For testing the inter-operation between ZUML and Java codes.
 *
 * @author tomyeh
 */
public class Tests {
	/** Return a say-hi button that use anonymous inner class
	 * for testing Bug 1681058 (false alarm)
	 */
	public static Button newSayHiButton() {
		return new Button("Say Hi") {
			public void onClick(MouseEvent evt) throws InterruptedException {
				Messagebox.show("Hi there!");
					//it is not callable since anonymous class is not public
			}
		};
	}
}
