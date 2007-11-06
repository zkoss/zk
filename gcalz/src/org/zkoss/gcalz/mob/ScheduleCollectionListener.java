/*
*ScheduleListener.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/23 PM 12:18:07, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz.mob;

import org.zkoss.gcalz.ZCalendar;

/**
 * @author Ian Tsai
 */
public interface ScheduleCollectionListener
{
    /**
     * 
     * @param sch
     */
    void update(ZCalendar sch);
        
}// end of interface
