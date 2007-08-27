/* ReservationBean.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/8/21 6:33:34 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * @author Dennis.Chen
 *
 */
public class ReservationBean {
	
	
	private String room;
	private String name;
	private String eid;
	private Date resvDate;
	private String resvTime;
	private String contactPhone;
	
	static HashMap reservations = new HashMap();
	static{
		reservations.put("Mannheim",new HashMap());
		reservations.put("Balears",new HashMap());
	}
	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	
	
	public ReservationBean(){
		
	}
	
	
	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getResvDate() {
		return resvDate;
	}

	public void setResvDate(Date resvDate) {
		this.resvDate = resvDate;
	}

	public String getResvTime() {
		return resvTime;
	}

	public void setResvTime(String resvTime) {
		this.resvTime = resvTime;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public void validateRoom(FacesContext context,
            UIComponent component,
            Object value) throws ValidatorException {
		if(!"Mannheim".equals(value)&&!"Balears".equals(value)){
			throw new ValidatorException(newMessage("Select conference room"));
		}
	}
	
	
	
	public String doReservation(){
		String dateStr = format.format(resvDate);
		HashMap roomMap = (HashMap)reservations.get(room);
		HashMap timeMap = (HashMap)roomMap.get(dateStr);
		if(timeMap==null){
			timeMap = new HashMap();
			timeMap.put(resvTime,cloneBean());
			roomMap.put(dateStr,timeMap);
		}else{
			if(timeMap.get(resvTime)==null){
				timeMap.put(resvTime,cloneBean());
			}else{
				FacesContext.getCurrentInstance().addMessage("form1:commitBtn",new FacesMessage("Time "+resvTime+" is already reserved by :"+((ReservationBean)timeMap.get(resvTime)).name));
				return null;
			}
		}
		System.out.println("reservations==>"+reservations);
		return "result";
	}
	
	public String doBack(){
		clear();
		return "back";
	}
	
	
	public FacesMessage newMessage(String message){
		return new FacesMessage(message);
	}
	
	
	public ReservationBean cloneBean(){
		ReservationBean newone = new ReservationBean();
		newone.room = room;
		newone.eid = eid;
		newone.name = name;
		newone.resvDate = resvDate;
		newone.resvTime = resvTime;
		newone.contactPhone = contactPhone;

		return newone;
	}
	
	public void clear(){
		room = null;
		eid = null;
		name = null;
		resvDate = null;
		resvTime = null;
		contactPhone = null;
	}
	
	String timeOptions[] = new String[]{"08","10","12","14","16","18"};
	
	public List getReservationList(String room,Date date){
		ArrayList list = new ArrayList();
		HashMap roomMap = (HashMap)reservations.get(room);
		String dateStr = format.format(date);
		HashMap timeMap = (HashMap)roomMap.get(dateStr);
		
		for(int i=0;i<timeOptions.length;i++){
			if(timeMap==null){
				list.add(new String[]{timeOptions[i],"Free"});
			}else{
				ReservationBean rb = (ReservationBean)timeMap.get(timeOptions[i]);
				if(rb==null){
					list.add(new String[]{timeOptions[i],"Free"});
				}else{
					list.add(new String[]{timeOptions[i],"reserved by "+rb.getName()});
				}
			}
		}
		
		return list;
	}
	
}
