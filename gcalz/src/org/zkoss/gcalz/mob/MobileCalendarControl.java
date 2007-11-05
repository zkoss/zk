/*
*MobileCalendarControl.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/18 ¤U¤È 2:34:25, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz.mob;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.gcalz.*;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;

/**
 * @author Ian Tsai
 * @date 2007/7/18
 */
public class MobileCalendarControl 
{
    private List<ZCalendar> schList;
    Profiling pro = new Profiling();
    private Date current;
    private CalendarService calendarService;
    private ZCalendar currentSch;
    private CalendarQueryModifer modifer;
    private Calendar currentCalendar;
    
    private Map<String,ScheduleCollectionListener> listenerStorage;
    private ScheduleCollectionListener delegateFire;
    
    /**
     * 
     * @param date
     */
    public MobileCalendarControl(Date date)
    {
        current = date;
        currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(current.getTime());
        listenerStorage = new HashMap<String, ScheduleCollectionListener>();
        delegateFire = new ScheduleCollectionListener(){
            public void update(ZCalendar sch)
            {
                for(ScheduleCollectionListener fire:listenerStorage.values())
                    fire.update(sch);
            }
        };
        modifer = getDefultModifer();
    }
    /**
     * initial CalendarService, look up Meta User Calendars.
     * @param username
     * @param password
     */
    public boolean init(String username, String password)
    {
        pro.start("init");
        boolean ans = false;
        calendarService=null;
        try
        {
            if(ans = (calendarService = GCalUtil.createGCalService(username, password))!=null)
            {
                schList = new ArrayList<ZCalendar>(8);
                pro.start("lookupMetaUserCalendars");
                for(CalendarEntry gcal : GCalUtil.getMetaUserCalendars(calendarService))
                {
                    pro.start("new Schedule");
                    ZCalendarGImpl sch = null;
                    if(gcal!=null)schList.add(sch = new ZCalendarGImpl(
                            new GCalendarQuery(calendarService), gcal));
                    if(sch!=null)sch.setModifer(modifer);
                    pro.start("lookupMetaUserCalendars");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        pro.end();
        return ans; 
    }
    /**
     * 
     * @param sch
     */
    public void changeCurrentSchedule(ZCalendar sch)
    {
        this.currentSch = sch;
        this.delegateFire.update(sch);
    }

    /**
     * 
     */
    private void changeCurrentScheduleMode()
    {
        if(modifer==null)choseCalModeToday();
        currentSch.setModifer(modifer);
//        currentSch.refresh();
        this.delegateFire.update(currentSch);
    }
    
    /**
     * 
     * @return
     */
    private CalendarQueryModifer getDefultModifer()
    {
        return new CalendarQueryModifer(){
            public String toString(){return "today's ";}
            public void modify(GCalendarQuery query)
            {
                query.clear();
                int year = currentCalendar.get(Calendar.YEAR);
                int month = currentCalendar.get(Calendar.MONTH);
                int date = currentCalendar.get(Calendar.DAY_OF_MONTH);
                Calendar start = Calendar.getInstance();
                start.set(year, month, date,0,0,0);
//                System.out.println("Today Modifer's Start:"+
//                        new SimpleDateFormat("yy/MM/dd HH:mm").format(
//                                new Date(start.getTimeInMillis())));
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.DAY_OF_YEAR,1);
                query.setStartMin(new Date(start.getTimeInMillis()))
                    .setStartMax(new Date(end.getTimeInMillis()))
                    .setOrderby(GCalendarQuery.OrderBy.starttime);
            }};
    }
    
    public void choseCalModeToday()
    {
        modifer = getDefultModifer();
        changeCurrentScheduleMode();
    }

    public void choseCalModeNextDays(final int days)
    {
        modifer = new CalendarQueryModifer(){
            public String toString()
            {
                if(days==1)return "Next 24hr's";
                else return "Next "+days+" Days";
            }
            public void modify(GCalendarQuery query)
            {
                query.clear();
                Calendar end = (Calendar) currentCalendar.clone();
                end.add(Calendar.DAY_OF_YEAR,days);
                query.setStartMin(new Date(currentCalendar.getTimeInMillis()))
                .setStartMax(new Date(end.getTimeInMillis()))
                .setOrderby(GCalendarQuery.OrderBy.starttime);
            }
        };
        changeCurrentScheduleMode();
    }

    public void choseCalModeThisWeek()
    {
        modifer = new CalendarQueryModifer(){
            public String toString(){return "This Week's";}
            public void modify(GCalendarQuery query)
            {
                query.clear();
                int year = currentCalendar.get(Calendar.YEAR);
                int month = currentCalendar.get(Calendar.MONTH);
                int date = currentCalendar.get(Calendar.DAY_OF_MONTH);
                int dweek = currentCalendar.get(Calendar.DAY_OF_WEEK);
                Calendar start = Calendar.getInstance();
                start.set(year, month, date-dweek+1,0,0);
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.DAY_OF_YEAR,7);
                query.setStartMin(new Date(start.getTimeInMillis()))
                    .setStartMax(new Date(end.getTimeInMillis()))
                    .setOrderby(GCalendarQuery.OrderBy.starttime);
            }
        };
        changeCurrentScheduleMode();
    }
    
    public void choseCalModeManual(final Date start, final Date end)
    {
        modifer = new CalendarQueryModifer(){
            public String toString(){return "Manual";}
            public void modify(GCalendarQuery query)
            {
                query.clear();
                query.setStartMin(start)
                    .setStartMax(end)
                    .setOrderby(GCalendarQuery.OrderBy.starttime);
            }
        };
        changeCurrentScheduleMode();
    }
    public void choseCalModeAll()
    {
        modifer = new CalendarQueryModifer(){
            public String toString(){return "All";}
            public void modify(GCalendarQuery query)
            {
                query.clear();
                query.setOrderby(GCalendarQuery.OrderBy.starttime);
            }
        };
        changeCurrentScheduleMode();
    }
    
    public void addListener(String key, ScheduleCollectionListener value)
    {
        this.listenerStorage.put(key, value);
    }
    
    public void removeListener(String key)
    {
        this.listenerStorage.remove(key);
    }
    /**
     * 
     * @return
     */
    public List<ZCalendar> getSchedules()
    {
        return schList;
    }

    public Date getCurrent()
    {
        return current;
    }
    
    public String getTitle()
    {
        SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return "Entered on: "+form.format(current);
    }
    

    public void setCurrent(Date current)
    {
        this.current = current;
    }
    
    public ZCalendar getCurrentSchedule()
    {
        return currentSch;
    }
    
    
}//end of class
