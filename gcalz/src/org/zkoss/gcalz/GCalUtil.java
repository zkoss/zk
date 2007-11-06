/*
*GDataFactory.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/11 PM 12:56:28, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
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
 */
public class GCalUtil
{
    
    private static final String METAFEED_URL = "http://www.google.com/calendar/feeds/default";
    public static final Log log = Log.lookup(GCalUtil.class);
    
    /**
     * GET meta user calendars use defult URL "http://www.google.com/calendar/feeds/default".
     * @param service a Google Calendar service object.
     * @return a list of meta Calendar entries
     * @throws IOException throw if HTTP connection error
     * @throws ServiceException if Service object has error.
     */
    public static List getMetaUserCalendars(CalendarService service) 
    throws IOException, ServiceException
    {
        return getEntries(METAFEED_URL, service, CalendarFeed.class);
    }
    /**
     * Use username + password to connect to Google Calendar Service and getback  user's calendars.
     * @param username the email address of your Google account
     * @param password the password of your Google account
     * @return a list of meta Calendar entries
     * @throws IOException throw if HTTP connection error
     * @throws ServiceException if Service object has error.
     */
    public static List getMetaUserCalendars(String username, String password) 
    throws IOException, ServiceException
    {
        return getMetaUserCalendars(createGCalService(username,password));
    }
    
    
    /**
     * A fast method to GET event list from user's main calendar. <br>
     * Query URL is like this: "http://www.google.com/calendar/feeds/" + username + "/private/full"
     * @param username the email address of your Google account
     * @param password the password of your Google account
     * @return a list of Calendar entry events.
     * @throws IOException throw if HTTP connection error
     * @throws ServiceException throw if Service object has error.
     */
    public static List getQuickMainCalendarEvents(String username, String password) 
    throws IOException, ServiceException
    {
        String que = "http://www.google.com/calendar/feeds/" + username + "/private/full";
        return getAllEvents(que, createGCalService(username,password)) ;
    }
    /**
     * Use a calendar uri to query it's detail events.
     * @param targetCalUri the target calendar url.
     * @param service Google {@link CalendarService} Object.
     * @return a list of {@link CalendarEventEntry}
     * @throws IOException throws if HTTP connection error
     * @throws ServiceException throws if Service object has error.
     */
    public static List getAllEvents(String targetCalUri, CalendarService service) 
    throws IOException, ServiceException
    {
        return getEntries(targetCalUri, service, CalendarEventFeed.class);
    }
    /**
     * Generic type query in Google Base Data API
     * @param feedUri the target calendar url.
     * @param service  Google {@link CalendarService} Object.
     * @param clazz Google Data Object's class.
     * @return a list of entries represent Google data set.
     * @throws IOException  throws if HTTP connection error
     * @throws ServiceException throws if Service object has error.
     */
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
     * @param username the email address of your Google account.
     * @param password the password of your Google account.
     * @return a {@link CalendarService} Object instance.
     * @throws AuthenticationException  if authentication is failed.
     */
    public static CalendarService createGCalService(String username, String password) 
    throws AuthenticationException
    {
        CalendarService googleServ = new CalendarService("ZK-ZGDATA-0.1");
        googleServ.setUserCredentials(username, password);
        return googleServ;
    }

    /**
     * By profiling CalendarEntry Object
     * @param cal A Google {@link CalendarEntry} reprensent a Clendar entry in your Google calendar.
     * @return A URL to this calendar's evnet RSS Service. nuul if "alternate" can't be finded.
     */
    public static String findCalEventsLinkHref(CalendarEntry cal)
    {
    	Link link;
        for(Iterator it = cal.getLinks().iterator();it.hasNext();)
        {
        	link = (Link) it.next();
        	if(link.getRel().equals("alternate"))
                return link.getHref();
        }
        return null;
    }

    /**
     * look up google CalendarEventEntry object's event content.
     * @param entry a {@link CalendarEventEntry} Object.
     * @return if content is null return "".
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
