/*
*GDataFactory.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/11 ¤U¤È 12:56:28, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.zkoss.util.logging.Log;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * @author Ian Tsai
 * @date 2007/7/11
 */
public class GCalUtil
{
    
    private static final String METAFEED_URL = "http://www.google.com/calendar/feeds/default";
    public static final Log log = Log.lookup(GCalUtil.class);
    
    /**
     * GET meta user calendars use defult URL "http://www.google.com/calendar/feeds/default".
     * @param service
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public static List<CalendarEntry> getMetaUserCalendars(CalendarService service) 
    throws IOException, ServiceException
    {
        return getEntries(METAFEED_URL, service, CalendarFeed.class);
    }
    /**
     * Use username + password to connect to Google Calendar Service and getback  user's calendars.
     * @param username the email address of your Google account
     * @param Password
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public static List<CalendarEntry> getMetaUserCalendars(String username, String password) 
    throws IOException, ServiceException
    {
        return getMetaUserCalendars(createGCalService(username,password));
    }
    
    
    /**
     * A fast method to GET event list from user's main calendar. <br>
     * Query URL is like this: "http://www.google.com/calendar/feeds/" + username + "/private/full"
     * @param username the email address of your Google account
     * @param password
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    public static List<CalendarEventEntry> getQuickMainCalendarEvents(String username, String password) 
    throws IOException, ServiceException
    {
        String que = "http://www.google.com/calendar/feeds/" + username + "/private/full";
        return getAllEvents(que, createGCalService(username,password)) ;
    }
    /**
     * Use a calendar uri to query it's detail events.
     * @param targetCalUri 
     * @param service
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    public static List<CalendarEventEntry> getAllEvents(String targetCalUri, CalendarService service) 
    throws IOException, ServiceException
    {
        return getEntries(targetCalUri, service, CalendarEventFeed.class);
    }
    /**
     * Generic type query in Google Base Data API
     * @param feedUri
     * @param service
     * @param clazz
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public static List
    getEntries(String feedUri, CalendarService service, Class clazz)
    throws IOException, ServiceException
    {
        URL feedUrl = new URL(feedUri);
        BaseFeed  resultFeed = service.getFeed(feedUrl, clazz);
        return resultFeed.getEntries();
    }
    
    
    /**
     * Use username & password to create an Authened Service to connect to Google server. 
     * @param username the email address of your Google account
     * @param password
     * @return
     * @throws AuthenticationException 
     */
    public static CalendarService createGCalService(String username,String password) 
    throws AuthenticationException
    {
        CalendarService googleServ = new CalendarService("ZK-ZGDATA-0.1");
        googleServ.setUserCredentials(username, password);
        return googleServ;
    }

    /**
     * By profiling CalendarEntry Object
     * @param links
     * @return URL to get back this calendar's evnet details.
     */
    public static String findCalEventsLinkHref(CalendarEntry cal)
    {
        for(Link link:cal.getLinks())
            if(link.getRel().equals("alternate"))
                return link.getHref();
        return null;
    }

    /**
     * 
     * @param entry
     * @return
     */
    public static String lookupEntryContent(CalendarEventEntry entry)
    {
        String ans = "";
        if((entry!=null) && entry.getContent() instanceof TextContent)
        {
            TextContent c = (TextContent) entry.getContent();
            if(c.getContent() instanceof PlainTextConstruct)
                ans = ((PlainTextConstruct)c.getContent()).getPlainText();
            else log.warning(" entry.getContent().getContent() is not a " +
                    "PlainTextConstruct!!! its class:"+c.getContent().getClass());
        }
        else log.warning(" entry is not a PlainTextConstruct!!!");
        return ans;
    }
    
    
}// end of class
