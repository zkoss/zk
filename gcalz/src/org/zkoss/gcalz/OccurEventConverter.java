/*
*Converter.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/13 ¤U¤È 12:20:02, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import org.zkforge.timeline.data.OccurEvent;

import com.google.gdata.data.calendar.CalendarEventEntry;

/**
 * @author Ian Tsai
 * @date 2007/7/13
 */
public interface OccurEventConverter
{
    /**
     * A method used to convert Google Calendar entry to Timeline OccurEvent.<br>
     * Maybe one day can drow out as a interface...
     * 
     * @param entry A Google Calendar Entry...
     * @return A OccurEvent represent this calendar entry...
     */
    OccurEvent convert(CalendarEventEntry entry);
    
}//end of interface
