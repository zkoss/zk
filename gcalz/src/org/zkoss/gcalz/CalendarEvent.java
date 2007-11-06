/**
 * 
 */
package org.zkoss.gcalz;

import java.util.Date;

/**
 * @author ian
 * to represent an event booked on a calendar.
 */
public interface CalendarEvent 
{
	/**
	 * The text content of the event.
	 * @return
	 */
	String getContent();
	/**
	 * The title of the event.
	 * @return
	 */
	String getTitle();
	/**
	 * end date of this event.
	 * @return
	 */
	Date getEndDate() ;
	/**
	 * start date of this event.
	 * @return
	 */
	Date getStartDate();
	/**
	 * get native Calendar Object.
	 * @return
	 */
	Object getNative();
	
}//end of interface...
