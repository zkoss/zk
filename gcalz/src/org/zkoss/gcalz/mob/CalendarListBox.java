/*
*CalendarListBox.java
*
*	Purpose:
*		
*	Description:
*		
*	History:
*	  2007/7/19 PM 9:51:19, Created by Ian Tsai
* 
*
* Copyright (C) Potix Corporation.  2006~2007 All Rights Reserved.
*/
package org.zkoss.gcalz.mob;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.gcalz.ZCalendar;
import org.zkoss.mil.Listbox;
import org.zkoss.mil.Listitem;
import org.zkoss.mil.event.CommandEvent;
import org.zkoss.zk.ui.event.SelectEvent;

/**
 * @author Ian Tsai
 */
public class CalendarListBox extends Listbox
{
    private static final long serialVersionUID = -311840486586481230L;

    private List listItemList = new ArrayList(10);
    private String dateFormat = "yy/MM/dd HH:mm";
    private SimpleDateFormat formatter;
    MobileCalendarControl control;

    /**
     * initialize this component. 
     * @param ctrl a {@link MobileCalendarControl}
     */
    public void initControl(final MobileCalendarControl ctrl)
    {
        if(ctrl==null)return;
        control = ctrl;
        if(formatter==null)formatter = new SimpleDateFormat(dateFormat);
        for(Iterator it = ctrl.getSchedules().iterator();it.hasNext();)
        	createNewItem((ZCalendar)it.next()).setParent(this);
        	
        if(ctrl.getSchedules().size()>0)
        {
            this.setSelectedIndex(ctrl.getSchedules().size()-1);
            ZCalendar sch = (ZCalendar) getSelectedItem().getValue();
            ctrl.changeCurrentSchedule(sch);
            this.setLabel(sch.getModifer()+":");
        }
    }
    /**
     * 
     * @param event while receiving a command from mobile.
     */
    public void onCommand(CommandEvent event)
    {
    	ZCalendar sch = (ZCalendar) getSelectedItem().getValue();
        this.setLabel(sch.getModifer()+":");
        getSelectedItem().setLabel(sch.getTitle()+"("+sch.getEvents().size()+")");
    }
    /*
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
     * @param sEvent while selected this listbox's item. 
     */
    public void onSelect(SelectEvent sEvent)
    {
        Listitem item = getSelectedItem();
        ZCalendar sch = (ZCalendar)item.getValue();
        control.changeCurrentSchedule(sch);
        item.setLabel(sch.getTitle()+"("+sch.getEvents().size()+")");
        this.setLabel(sch.getModifer()+":");
    }
    
    public void setSelectedItem(Listitem item)
    {
        super.setSelectedItem(item);
    }
    
    public boolean isMultiple()
    {
        return false;
    }
    
    public void setSelectedIndex(int jsel)
    {
        super.setSelectedIndex(jsel);
    }
    /**
     * 
     * @return the date format
     */
    public String getDateFormat()
    {
        return dateFormat;
    }
    /**
     * 
     * @param dateFormate the date format
     */
    public void setDateFormat(String dateFormate)
    {
        this.dateFormat = dateFormate;
    }

}// end of class
