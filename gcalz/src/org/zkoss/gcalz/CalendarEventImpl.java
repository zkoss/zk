/*
*ScheduleEvent.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/18 PM 3:36:42, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.util.Date;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;

/**
 * @author Ian Tsai
 * @date 2007/7/18
 */
public class CalendarEventImpl implements  CalendarEvent
{
    private CalendarEventEntry entry ;
    Date startDate;
    Date endDate;
    String title;
    String content ="";
    String id;

    /**
     * 
     * @param entry
     */
    public CalendarEventImpl(CalendarEventEntry entry )
    {
    	this.entry = entry;
        if(entry.getTimes().size()>0)
        {
            When obj = (When)entry.getTimes().get(0);
            startDate = new Date(obj.getStartTime().getValue());
            if(obj.getEndTime()!=null)
                endDate = new Date(obj.getEndTime().getValue());
        }else throw new IllegalArgumentException(
                "The CalendarEventEntry you entered has to time properties");
        title = entry.getTitle().getPlainText();
        content = GCalUtil.lookupEntryContent(entry);
        id = entry.getId();
    }
    
    public String getContent()
    {
        return content;
    }

    public String getTitle()
    {
        return title;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }
	public Object getNative() 
	{
		return entry;
	}




    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CalendarEventImpl other = (CalendarEventImpl) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }






}//end of class
