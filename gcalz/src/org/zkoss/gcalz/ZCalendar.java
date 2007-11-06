/*
*ZCalendar.java
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

import java.util.List;

/**
 * @author ian
 *
 *
 */
public interface ZCalendar 
{
	/**
	 * 
	 * @return get a list of {@link CalendarEvent}
	 */
	List getEvents();
	/**
	 * 
	 * @return this calendar's title.
	 */
	String getTitle();
	/**
	 * 
	 * @return a {@link CalendarQueryModifer}
	 */
	CalendarQueryModifer getModifer();
	/**
	 * 
	 * @param modifer set {@link CalendarQueryModifer}
	 */
	void setModifer(CalendarQueryModifer modifer);
}//end of interface...
