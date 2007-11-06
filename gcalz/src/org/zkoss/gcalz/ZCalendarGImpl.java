/*
*CalProxy.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/18 PM 3:00:20, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.util.ServiceException;


/**
 * An Google CalendarEventEntry's Container 
 * @author Ian Tsai
 */
public class ZCalendarGImpl implements ZCalendar
{
    private GCalendarQuery query;
    private CalendarEntry gcal;
    private Ref all;
    private Ref thread;
    private CalendarQueryModifer modifer;
    /**
     * 
     * @param _query a {@link GCalendarQuery}
     * @param _gcal a {@link CalendarEntry}
     */
    public ZCalendarGImpl(GCalendarQuery _query, CalendarEntry _gcal) 
    {
        gcal = _gcal;
        query = _query;
    }
    /**
     * lazyloading the target Events. 
     * @return get a list of Calendar Event.
     */
    public List getEvents()
    {
        if(all==null)all = loadScheduleEvents();
        return (List)all.get();
    }
    /**
     * 
     * @return the event's title.
     */
    public String getTitle()
    {
        return gcal.getTitle().getPlainText();
    }

    private void refresh()
    {
        if(thread.get()!=null&&((Thread)thread.get()).isAlive())return;
        all = loadScheduleEvents();
    }

    /*
     * lazy load CalendarEventEntry for each Google Calendar's calendarEventEntries
     */
    private Ref loadScheduleEvents()
    {
        if(modifer!=null)modifer.modify(query);
        // new lazyder...
        LazyLoader lazyLoader = new LazyLoader();
        thread = lazyLoader.getLoadingThreadRef();
        return lazyLoader.doLoad(
                new Ref(){
                    /**
                     * Real Works to do Remote Communication 
                     */
                    public Object get()
                    {
                        List list = new LinkedList();
                        try
                        {
                            CalendarEventImpl event = null;
                            CalendarEventEntry entry ;
                            for( Iterator it = query.invoke(gcal).iterator();it.hasNext();)
                            {
                            	entry = (CalendarEventEntry)it.next();
                                try
                                {
                                    event = new CalendarEventImpl(entry);
                                }
                                catch(Exception e){continue;}
                                list.add(event);
                            }
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (ServiceException e)
                        {
                            e.printStackTrace();
                        }
                        System.out.println(this.getClass()+".loadScheduleEvents(): List<ScheduleEvent> Size="+list.size());
                        
                        return list;
                    }
                }, 1000, 5);//end of class...
    }
    public CalendarQueryModifer getModifer()
    {
        return modifer;
    }
    public void setModifer(CalendarQueryModifer modifer)
    {
        this.modifer = modifer;
        refresh();
    }
}//end of class
