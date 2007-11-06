/*
*SortOrder.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/11 PM 2:57:00, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

/**
 * @author ian
 *
 */
public class SortOrder 
{
	private static final String asc = "a";
	private static final String desc = "b";
    public static final SortOrder ascending = new SortOrder(asc);
    public static final SortOrder descending = new SortOrder(desc);
    
	private String key;

	private SortOrder(String lkey) 
	{
		key = lkey;
	}
	
	public String toString()
	{
		return key;
	}


}
