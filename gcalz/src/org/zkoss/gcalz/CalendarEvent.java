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
	 * 
	 * @return The text content of the event.
	 */
	String getContent();
	/**
	 * 
	 * @return The title of the event.
	 */
	String getTitle();
	/**
	 * 
	 * @return end date of this event.
	 */
	Date getEndDate() ;
	/**
	 * 
	 * @return start date of this event.
	 */
	Date getStartDate();
	/**
	 * 
	 * @return native Calendar Object.
	 */
	Object getNative();
	
}//end of interface...
