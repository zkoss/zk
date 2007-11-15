/* MyWest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 8, 2007 10:09:44 AM 2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jspdemo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkex.zul.West;

/**
 * @author ian
 * used to test ...
 */
public class MyWest extends West 
{

	public boolean insertBefore(Component child, Component insertBefore) 
	{
		System.out.println(MyWest.class+"::insertBefore() insertComponent="+child);
		return super.insertBefore(child, insertBefore);
	}
	
	
	
	
	
	
}//end of class...
