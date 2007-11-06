/*
*GCalQuery.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/19 AM 6:15:10, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.util.ServiceException;

/**
 * @author Ian Tsai
 * @date 2007/7/19
 */
public class GCalendarQuery
{
    //true OR false
    public static final String FUTURE_EVENTS = "futureevents=";
    //lastmodified, starttime
    public static final String ORDER_BY = "orderby=";

    // true OR false
    public static final String SINGLE_EVENTS = "singleevents=";
    
    //RFC 3339 example: 2005-08-09T10:57:00-08:00
    public static final String RECURRENCE_EXPANSION_START = "recurrence-expansion-start=";
    public static final String RECURRENCE_EXPANSION_END = "recurrence-expansion-end=";
    
    //ascending, ascend, a OR descending, descend, d
    public static final String SORT_ORDER = "sortorder=";
    
    //RFC 3339 example: 2005-08-09T10:57:00-08:00
    public static final String START_MAX = "start-max=";
    public static final String START_MIN = "start-min=";

    
    /**
     * 
     * @param o
     * @return
     */
    public static String caseOrderBy(OrderBy o)
    {
        if(o==null)return OrderBy.starttime.toString();
        return o.toString();
    }
    
    /**
     * 
     * @param o
     * @return
     */
    public static String caseSortOrder(SortOrder o)
    {
        if(o==null)return SortOrder.descending.toString();
        return o.toString();
    }
    
    public static final SimpleDateFormat RFC3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    

    

    private Boolean futureEvents;
    private Boolean singleEvents;
    private OrderBy orderby;
    private SortOrder sortOrder;
    private Date recurrenceExpansionStart;
    private Date recurrenceExpansionEnd;
    private Date startMax;
    private Date startMin;
    private CalendarService gCalServ;
    /**
     * 
     * @param _gCalServ
     */
    public GCalendarQuery(CalendarService _gCalServ)
    {
        gCalServ = _gCalServ;
    }
    /**
     * 
     *clear all state of this guery.
     */
    public void clear()
    {
        futureEvents = new Boolean(false);
        singleEvents = null;
        orderby =null;
        sortOrder=null;
        recurrenceExpansionStart=null;
        recurrenceExpansionEnd=null;
        startMax=null;
        startMin=null;
    }
    
    
    /**
     * invoke this guery, get back the event list
     * @param cal
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    public List invoke(CalendarEntry calendar) 
    throws IOException, ServiceException
    {
        StringBuffer feedUri = new StringBuffer(GCalUtil.findCalEventsLinkHref(calendar));
        Ref flag = new Ref(){
        	int i;
        	public Object get()
        	{
        		String ans = i++ < 1 ? "?":"&"; 
        		return ans;
        	}
        };
        if(futureEvents!=null)
        	feedUri.append(flag.get()).append(FUTURE_EVENTS).append(futureEvents);
        if(singleEvents!=null)
        	feedUri.append(flag.get()).append(SINGLE_EVENTS).append(singleEvents);
        
        if(startMin!=null)
        	feedUri.append(flag.get()).append(START_MIN).append(RFC3339.format(startMin));
        if(startMax!=null)
        	feedUri.append(flag.get()).append(START_MAX).append(RFC3339.format(startMax));
        
        if(recurrenceExpansionStart!=null)feedUri.append(flag.get()).append( 
                RECURRENCE_EXPANSION_START).append(RFC3339.format(recurrenceExpansionStart));
        if(recurrenceExpansionEnd!=null)feedUri.append(flag.get()).append(
                RECURRENCE_EXPANSION_END).append(RFC3339.format(recurrenceExpansionEnd));
        if(orderby!=null)
        	feedUri.append(flag.get()).append(ORDER_BY).append(caseOrderBy(orderby));
        if(sortOrder!=null)
        	feedUri.append(flag.get()).append(SORT_ORDER).append(caseSortOrder(sortOrder));
        
        String classname = GCalendarQuery.class.getName();
        classname = classname.substring(classname.lastIndexOf("."));
        System.out.println(classname+".invokeEventQuery(): feedUri->"+feedUri);
        
        return GCalUtil.getEntries(feedUri.toString(), gCalServ, CalendarEventFeed.class);
    }
    /**
     * get the calendar events orderby attribute. 
     * @return
     */
    public OrderBy getOrderby()
    {
        return orderby;
    }
    /**
     * set the calendar events orderby attribute.
     * @param orderby
     * @return
     */
    public GCalendarQuery setOrderby(OrderBy orderby)
    {
        this.orderby = orderby;
        return this;
    }
    /**
     * get Recurrence Expansion End
     * @return
     */
    public Date getRecurrenceExpansionEnd()
    {
        return recurrenceExpansionEnd;
    }
    /**
     * set Recurrence Expansion End
     * @param recurrenceExpansionEnd
     * @return
     */
    public GCalendarQuery setRecurrenceExpansionEnd(Date recurrenceExpansionEnd)
    {
        this.recurrenceExpansionEnd = recurrenceExpansionEnd;
        return this;
    }
    /**
     * get Recurrence Expansion Start
     * @return
     */
    public Date getRecurrenceExpansionStart()
    {
        return recurrenceExpansionStart;
    }
    /**
     * set Recurrence Expansion Start
     * @param recurrenceExpansionStart
     * @return
     */
    public GCalendarQuery setRecurrenceExpansionStart(Date recurrenceExpansionStart)
    {
        this.recurrenceExpansionStart = recurrenceExpansionStart;
        return this;
    }
    /**
     * the condition to get back the event that is not occurrence in the mean time.
     * @return
     */
    public boolean isSingleEvents()
    {
        return singleEvents.booleanValue();
    }
    /**
     * set the condition to get back the event that is not occurrence in the mean time.
     * @param singleEvents
     * @return
     */
    public GCalendarQuery setSingleEvents(boolean singleEvents)
    {
        this.singleEvents = new Boolean(singleEvents);
        return this;
    }
    /**
     * get the condition to sort the searching result.
     * @return
     */
    public SortOrder getSortOrder()
    {
        return sortOrder;
    }
    /**
     * 
     * @param sortOrder
     * @return
     */
    public GCalendarQuery setSortOrder(SortOrder sortOrder)
    {
        this.sortOrder = sortOrder;
        return this;
    }
    /**
     * 
     * @return
     */
    public Date getStartMax()
    {
        return startMax;
    }
    /**
     * 
     * @param startMax
     * @return
     */
    public GCalendarQuery setStartMax(Date startMax)
    {
        this.startMax = startMax;
        return this;
    }
    /**
     * 
     * @return
     */
    public Date getStartMin()
    {
        return startMin;
    }
    /**
     * 
     * @param startMin
     * @return
     */
    public GCalendarQuery setStartMin(Date startMin)
    {
        this.startMin = startMin;
        return this;
    }
    
    
}//end of class
