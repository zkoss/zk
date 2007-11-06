/*
*CalendarQueryModifer.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/23 AM 11:41:35, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import org.zkoss.zk.ui.Desktop;

/**
 * @author Ian Tsai
 * this class should be refactored one day.
 */
public interface CalendarQueryModifer
{
    /**
     * this class should be refactored one day.
     * @param query the {@link GCalendarQuery} need to be modified.
     */
    void modify(GCalendarQuery query);
    

}//end of interface...
