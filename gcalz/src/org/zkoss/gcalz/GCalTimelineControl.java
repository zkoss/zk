/*
*GCalendarManager.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/11 ¤U¤È 1:09:37, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.zkforge.timeline.data.OccurEvent;
import org.zkoss.util.logging.Log;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * @author Ian Tsai
 * @date 2007/7/11
 */
public class GCalTimelineControl
{
    public static final Log log = Log.lookup(GCalTimelineControl.class);

    private String pastIconUrl;
    private String futureIconUrl;
    private String workingIconUrl;
    private String imageUrl;
    private String color;
    private Date currentDate = new Date();
    
    
    private ListModelList dataModel = new ListModelList(){
        public boolean add(Object oe)
        {
            if(oe!=null)return super.add(oe);
            return false;
        }
    };
    
    /**
     * Look up Google Calendar Entry Plan Text Content 
     * @param entry
     * @return the Plan Text Content of Google Calendar Entry.
     */
    private OccurEventConverter occurEventConverter = new OccurEventConverter()
    {
        public  OccurEvent convert(CalendarEventEntry entry)
        {
            OccurEvent oe = new OccurEvent();
            Date start = null;
            Date end = null;
            if(entry.getTimes().size()>0)
            {
                When obj = entry.getTimes().get(0);
                start = new Date(obj.getStartTime().getValue());
                if(obj.getEndTime()!=null)
                    end = new Date(obj.getEndTime().getValue());
            }else return null;
            oe.setStart(start);
            oe.setEnd(end);
            oe.setText(entry.getTitle().getPlainText());
            oe.setDescription(
                    GCalUtil.lookupEntryContent(entry));
            if(imageUrl!=null)oe.setImageUrl(imageUrl);
            if(color!=null)oe.setColor(color);
            if(futureIconUrl!=null&&pastIconUrl!=null)
                if(currentDate.after(start))oe.setIconUrl(pastIconUrl);
                else oe.setIconUrl(futureIconUrl);
            
            if(workingIconUrl!=null&&currentDate.after(start)&&
                    currentDate.before(end))
                oe.setIconUrl(workingIconUrl);
            return oe;
        }
    };//end of Anonymous Inner Calss

    


    /**
     * Load Google Calendar to userCalendarModel, any OccurEvent in userCalendarModel will cleared. 
     * @param user
     * @param password
     * @throws ServiceException 
     * @throws IOException 
     */
    public void loadGCalendar(String user, String password)
    {
        // through google service get calendars...
        try
        {
            dataModel.clear();
            for(CalendarEventEntry event : GCalUtil.getQuickMainCalendarEvents(user, password))
                dataModel.add(occurEventConverter.convert(event));
        } 
        catch (AuthenticationException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 
        catch (ServiceException e)
        {
            e.printStackTrace();
        }// think a method to handle Exception...
    }

    public void clearTimelineModel()
    {
        dataModel.clear();
    }

    /**
     * 
     * @return
     */
    public ListModel getTimelineModel()
    {
        return dataModel;
    }

    /**
     * 
     * @return
     */
    public String getFutureIconUrl()
    {
        return futureIconUrl;
    }
    /**
     * 
     * @param futureIconUrl
     */
    public void setFutureIconUrl(String futureIconUrl)
    {
        this.futureIconUrl=futureIconUrl;
    }
    /**
     * 
     * @return
     */
    public String getPastIconUrl()
    {
        return pastIconUrl;
    }
    /**
     * 
     * @param pastIconUrl
     */
    public void setPastIconUrl(String pastIconUrl)
    {
        this.pastIconUrl=pastIconUrl;
    }
    /**
     * 
     * @return
     */
    public String getImageUrl()
    {
        return imageUrl;
    }
    /**
     * 
     * @param imageUrl
     */
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl=imageUrl;
    }
    /**
     * 
     * @return
     */
    public String getColor()
    {
        return color;
    }
    /**
     * 
     * @param color
     */
    public void setColor(String color)
    {
        this.color=color;
    }


    public String getWorkingIconUrl()
    {
        return workingIconUrl;
    }


    public void setWorkingIconUrl(String workingIconUrl)
    {
        this.workingIconUrl = workingIconUrl;
    }
    
}//end of class
