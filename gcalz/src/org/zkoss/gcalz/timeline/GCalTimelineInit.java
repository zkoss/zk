/*
*CalFeedToTimelineInit.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/11 PM 6:20:35, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz.timeline;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.zkoss.gcalz.GCalUtil;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.xml.XmlOutputFns;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;


/**
 * @author Ian Tsai
 * @date 2007/7/11
 */
public class GCalTimelineInit implements Initiator
{

    public void doAfterCompose(Page page) throws Exception
    {
    }

    public boolean doCatch(Throwable ex)
    {
    	return false;
    }

    public void doFinally()
    {

    }
    /**
     * get username & password from request to guery Google service.<br>
     * if they are all exist, publish it as a MIT timeline Service.<br> 
     * 
     * @param user Google service's user, get it from request parameters by "user".
     * @param password Google user's password,  get it from request parameters by "pwd".
     * 
     */
    public void doInit(Page page, Object[] args) throws Exception
    {
        //get user & password...
        HttpServletRequest req = (HttpServletRequest) ServletFns.getCurrentRequest();
        
        String user = req.getParameter("user");
        String password = req.getParameter("pwd");
        if(user!=null && password!=null)publish(user, password);
        else XmlOutputFns.doForward("<?xml version=\"1.0\" encoding=\"UTF-8\"?><data></data>");
    }
    /**
     * 
     * @throws IOException
     */
    private  void publish(String user, String password) throws IOException
    {
        List entries = null;
        try
        {
            entries = GCalUtil.getQuickMainCalendarEvents(user, password);
        } catch (ServiceException e)
        {
            e.printStackTrace();
        }
        String content = convert(entries);
        XmlOutputFns.doForward(content);
    }
    
    /**
     * 
     * @param entries
     * @return
     */
    private  String convert(List entries)
    {
        SimpleDateFormat format  = new SimpleDateFormat("MM dd yyyy HH:mm:ss"); 
        StringBuffer sbf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sbf.append("<data>");
        CalendarEventEntry entry;
        for( Iterator it = entries.iterator();it.hasNext();)
        {
        	entry = (CalendarEventEntry) it.next();
            sbf.append("<event ");
            if(entry.getTimes().size()>0)
            {
                When obj = (When)entry.getTimes().get(0);
                DateTime sdt = obj.getStartTime();
                DateTime edt = obj.getEndTime();
                sbf.append(" start=\""+format.format(new Date(sdt.getValue()))+" GMT\" ");
                if(obj.getEndTime()!=null)
                    sbf.append(" end=\""+format.format(new Date(edt.getValue()))+" GMT\" ");
            }
            sbf.append("title=\""+entry.getTitle().getPlainText()+"\" >");
            String content = GCalUtil.lookupEntryContent(entry);
            if(content!=null)sbf.append(content.replace("<","&lt;"));
            sbf.append("</event>");        	
        }
        sbf.append("</data>");
        return sbf.toString();
    }
}//end of class
