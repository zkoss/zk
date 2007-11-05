/*
*CalendarListBox.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/19 ¤W¤È 9:51:19, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz.mob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.gcalz.Profiling;
import org.zkoss.gcalz.ZCalendar;
import org.zkoss.mil.Listbox;
import org.zkoss.mil.Listitem;
import org.zkoss.mil.event.CommandEvent;
import org.zkoss.zk.ui.event.SelectEvent;

/**
 * @author Ian Tsai
 * @date 2007/7/19
 */
public class CalendarListBox extends Listbox
{
    /**
     * 
     */
    private static final long serialVersionUID = -311840486586481230L;

    Profiling pro = new Profiling();
    private List<Listitem> listItemList = new ArrayList<Listitem>(10);
    private String dateFormat = "yy/MM/dd HH:mm";
    private SimpleDateFormat formatter;
    MobileCalendarControl control;

    /**
     * 
     * @param cals
     */
    public void initControl(final MobileCalendarControl con)
    {
        if(con==null)return;
        control = con;
        pro.start("setCalendars");
        if(formatter==null)formatter = new SimpleDateFormat(dateFormat);
        
        for(ZCalendar cal:con.getSchedules()) 
        	createNewItem(cal).setParent(this);
        if(con.getSchedules().size()>0)
        {
            this.setSelectedIndex(con.getSchedules().size()-1);
            ZCalendar sch = (ZCalendar) getSelectedItem().getValue();
            con.changeCurrentSchedule(sch);
            this.setLabel(sch.getModifer()+":");
        }
        pro.end();
    }
    
    public void onCommand(CommandEvent event)
    {
    	ZCalendar sch = (ZCalendar) getSelectedItem().getValue();
        this.setLabel(sch.getModifer()+":");
        getSelectedItem().setLabel(sch.getTitle()+"("+sch.getEvents().size()+")");
    }
    /**
     * 
     * @param sch
     * @return
     */
    private Listitem createNewItem(ZCalendar sch)
    {
        Listitem item = null;
        item = new Listitem();
        item.setLabel(sch.getTitle());
        item.setValue(sch);
        listItemList.add(item);
        return item; 
    }
    /**
     * 
     * @param sEvent
     */
    public void onSelect(SelectEvent sEvent)
    {
        pro.start("onSelect");
        Listitem item = getSelectedItem();
        ZCalendar sch = (ZCalendar)item.getValue();
        control.changeCurrentSchedule(sch);
        item.setLabel(sch.getTitle()+"("+sch.getEvents().size()+")");
        this.setLabel(sch.getModifer()+":");
        pro.end();
    }
    
    @Override
    public void setSelectedItem(Listitem item)
    {
        super.setSelectedItem(item);
    }
    @Override
    public boolean isMultiple()
    {
        return false;
    }
    
    @Override
    public void setSelectedIndex(int jsel)
    {
        super.setSelectedIndex(jsel);
    }

    public String getDateFormat()
    {
        return dateFormat;
    }

    public void setDateFormat(String dateFormate)
    {
        this.dateFormat = dateFormate;
    }

}// end of class
