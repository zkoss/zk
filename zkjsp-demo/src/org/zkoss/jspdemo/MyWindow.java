/* MyWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 26, 2007 10:25:44 AM 2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jspdemo;

import org.zkoss.zul.Window;

/**
 * A test Window used in zkjsp-demo
 * @author ian
 */
public class MyWindow extends Window {
	
	/**
	 * 
	 *test if onCreat event will be triggerd.
	 */
	public void onCreate()
	{
		System.out.println("MyWindow OnCreate Event happend.");
	}
	
}
