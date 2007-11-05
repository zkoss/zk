/*
*EventListBox.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/19 ¤U¤È 2:17:28, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz.mob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.gcalz.CalendarEvent;
import org.zkoss.gcalz.ZCalendar;
import org.zkoss.gcalz.Profiling;
import org.zkoss.gcalz.ScheduleCollectionListener;
import org.zkoss.gcalz.ZCalendarGImpl;
import org.zkoss.mil.Listbox;
import org.zkoss.mil.Listitem;
import org.zkoss.zk.ui.event.SelectEvent;

/**
 * @author Ian Tsai
 * @date 2007/7/19
 */
public class EventListBox extends Listbox
{
    private static final long serialVersionUID = -6356546910630921030L;
    private ZCalendar schedule;
    private String pasticon ;
    private String futureicon ;
    private String workingicon ;
    private String crossdaypasticon ;
    private String crossdayfutureicon ;
    private String crossdayworkingicon ;
    Profiling pro = new Profiling();
    private List<Listitem> itemList = new LinkedList<Listitem>();
    MobileCalendarControl control;
    
    /**
     * 
     * @param _control
     */
    public void initControl(MobileCalendarControl _control)
    {
        control = _control;
        control.addListener(this.toString(), 
        		new ScheduleCollectionListener(){
            public void update(ZCalendar sch)
            {
                loadSchedule(sch);
            }
        });
        this.loadSchedule(control.getCurrentSchedule());
    }
    
    /**
     * 
     * @param schedule
     */
    public void loadSchedule(ZCalendar _schedule)
    {
        pro.start("loadSchedule");
        try
        {
            removeAllListitems();
            schedule = _schedule;
            if(schedule==null)return;
            Listitem item;
            for(CalendarEvent event:schedule.getEvents())
            {
                itemList.add(item = createNewItem(event));
                item.setParent(this);
            }
            int size = schedule.getEvents().size();
            if(size>0)this.setSelectedItem(itemList.get(0));
            if(getSelectedItem()!=null)
                setLabel(getCurrentEventInfo((CalendarEvent) getSelectedItem().getValue()));
            else setLabel("Events(" +size+"):"+(size>0?"":"No Data"));
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        pro.end();
    }

    private SimpleDateFormat dateFormatter;
    private String dateFormat = "HH:mm";
    /**
     * 
     * @param sEvent
     */
    public void onSelect(SelectEvent sEvent)
    {
        if(getSelectedItem()!=null)
        {
        	CalendarEvent event = (CalendarEvent) getSelectedItem().getValue();
            setLabel("current: "+getCurrentEventInfo(event)+"");
            System.out.println("EventListBox: change label="+getLabel());
        }
    }
    /**
     * 
     * @param event
     * @return
     */
    private String getCurrentEventInfo(CalendarEvent event)
    {
        SimpleDateFormat df = null;
        String ans = "";
        if(isCrossDay(event))
        {
            df = new SimpleDateFormat("MM/dd");
            ans = df.format(event.getStartDate())+"~"+
            df.format(event.getEndDate());
        }
        else
        {
            df = new SimpleDateFormat("MM/dd HH:mm");
            long min = (event.getEndDate().getTime()-event.getStartDate().getTime())/(60000);
            System.out.println("formatDate:"+min);
            long hr = min/60;
            min = min - hr*60;

            
            ans ="current: " + df.format(event.getStartDate())+" "+
                (hr>0?hr+"hr ":"")+
                (min>0?min+"min":"");
        }

        return ans;
    }
    
    /**
     * 
     *
     */
    @SuppressWarnings("unchecked")
    public void removeAllListitems()
    {
        if(this.getChildren().size()>0)
        {
            for(Listitem item:itemList)this.removeChild(item);
        }
        itemList.clear();
    }
    /**
     * 
     * @param event
     * @return
     */
    private Listitem createNewItem(CalendarEvent event)
    {
        if(control==null)throw new NullPointerException(" you must init control first!!!");
        Listitem item = new Listitem();
        item.setLabel(event.getTitle());
        item.setValue(event);
        switch(justify(event, control.getCurrent()))
        {
            case WORKING: 
                item.setImage(workingicon);
                break;
            case PAST: 
                item.setImage(pasticon);
                break;
            case FUTURE: 
                item.setImage(futureicon);
                break;
        }
        if(isCrossDay(event))
        {
            switch(justify(event, control.getCurrent()))
            {
                case WORKING: 
                    item.setImage(crossdayworkingicon);
                    break;
                case PAST: 
                    item.setImage(crossdaypasticon);
                    break;
                case FUTURE: 
                    item.setImage(crossdayfutureicon);
                    break;
            }
        }
        return item;
    }
    public static final int FUTURE = 1;
    public static final int PAST = -1;
    public static final int WORKING = 0;
    public int justify(CalendarEvent event, Date current)
    {
        if(event.getEndDate().before(current))return PAST;//past
        else if(event.getStartDate().after(current))return FUTURE;//future
        else return WORKING;//working
    }
    
    private SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
    private static final long onday = 1000L*60*60*24;
    
    private boolean isCrossDay(CalendarEvent event)
    {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis( event.getStartDate().getTime());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(event.getEndDate().getTime());
        int i= start.get(Calendar.DAY_OF_YEAR);
        int j= end.get(Calendar.DAY_OF_YEAR);
        long l= end.getTimeInMillis()-start.getTimeInMillis();
        if(dateFormatter==null)dateFormatter = new SimpleDateFormat(dateFormat);
//        System.out.println("TITLE: "+event.getTitle());
//        System.out.println("Duration: "+format.format(event.getStartDate())+" : "+format.format(event.getEndDate()));
//        System.out.println("Timeinmillis: "+start.getTimeInMillis()+" : "+end.getTimeInMillis());
//        System.out.println("minus  : "+(end.getTimeInMillis()-start.getTimeInMillis()));
//        System.out.println("oneDay : "+(1000L*60*60*24));
//        System.out.println("DAY_OF_YEAR: "+i+" : "+j);
//        System.out.println("Date Time"+dateFormatter.format( event.getStartDate())
//                +" : "+dateFormatter.format( event.getEndDate()));
        return i!=j&&l>onday;
    }
    


    public String getFutureicon()
    {
        return futureicon;
    }
    public void setFutureicon(String futureicon)
    {
        this.futureicon = futureicon;
    }
    public String getPasticon()
    {
        return pasticon;
    }
    public void setPasticon(String pasticon)
    {
        this.pasticon = pasticon;
    }
    public String getWorkingicon()
    {
        return workingicon;
    }
    public void setWorkingicon(String workingicon)
    {
        this.workingicon = workingicon;
    }
    public String getDateFormat()
    {
        return dateFormat;
    }
    public void setDateFormat(String dateFormat)
    {
        this.dateFormat = dateFormat;
    }
    public String getCrossdayfutureicon()
    {
        return crossdayfutureicon;
    }
    public void setCrossdayfutureicon(String crossdayfutureicon)
    {
        this.crossdayfutureicon = crossdayfutureicon;
    }
    public String getCrossdaypasticon()
    {
        return crossdaypasticon;
    }
    public void setCrossdaypasticon(String crossdaypasticon)
    {
        this.crossdaypasticon = crossdaypasticon;
    }
    public String getCrossdayworkingicon()
    {
        return crossdayworkingicon;
    }
    public void setCrossdayworkingicon(String crossdayworkingicon)
    {
        this.crossdayworkingicon = crossdayworkingicon;
    }
    public MobileCalendarControl getControl()
    {
        return control;
    }
    public void setControl(MobileCalendarControl control)
    {
        this.control = control;
    }


}//end of class
