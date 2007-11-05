/*
*CalProxy.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/18 ¤U¤È 3:00:20, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.util.ServiceException;


/**
 * An Google CalendarEventEntry's Container 
 * @author Ian Tsai
 * @date 2007/7/18
 */
public class ZCalendarGImpl implements ZCalendar
{
    private GCalendarQuery query;
    private CalendarEntry gcal;
    private Ref<List<CalendarEvent>> all;
    private Ref<Thread> thread;
    private CalendarQueryModifer modifer;
    /**
     * 
     * @param _manager
     * @throws ServiceException 
     * @throws IOException 
     */
    public ZCalendarGImpl(GCalendarQuery _query, CalendarEntry _gcal) 
    {
        gcal = _gcal;
        query = _query;
    }
    /**
     * lazyloading technic 
     * @return
     */
    public List<CalendarEvent> getEvents()
    {
        if(all==null)all = loadScheduleEvents();
        return all.get();
    }
    /**
     * 
     * @return the schedule title.
     */
    public String getTitle()
    {
        return gcal.getTitle().getPlainText();
    }
    /**
     * 
     */
    private void refresh()
    {
        if(thread.get()!=null&&thread.get().isAlive())return;
        all = loadScheduleEvents();
    }

    
    /**
     * lazy load CalendarEventEntry for each Google Calendar's calendarEventEntries
     * @return
     */
    private Ref<List<CalendarEvent>> loadScheduleEvents()
    {
        if(modifer!=null)modifer.modify(query);
        // new lazyder...
        LazyLoader<List<CalendarEvent>> lazyLoader = new LazyLoader<List<CalendarEvent>>();
        thread = lazyLoader.getLoadingThreadRef();
        return lazyLoader.doLoad(
                new Ref<List<CalendarEvent>>(){
                    /**
                     * Real Works to do Remote Communication 
                     */
                    public List<CalendarEvent> get()
                    {
                        List<CalendarEvent> list = new LinkedList<CalendarEvent>();
                        try
                        {
                            CalendarEventImpl event = null;
                            for(CalendarEventEntry entry : 
                                query.invokeEventQuery(gcal))
                            {
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
//    public GCalendarQuery getQuery()
//    {
//        return query;
//    }
//    public void setQuery(GCalendarQuery query)
//    {
//        this.query = query;
//    }

}//end of class
