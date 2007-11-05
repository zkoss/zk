/**
 * 
 */
package org.zkoss.gcalz;

import java.util.Date;

/**
 * @author ian
 *
 */
public interface CalendarEvent 
{
	/**
	 * 
	 * @return
	 */
	String getContent();
	/**
	 * 
	 * @return
	 */
	String getTitle();
	/**
	 * 
	 * @return
	 */
	Date getEndDate() ;
	/**
	 * 
	 * @return
	 */
	Date getStartDate();
	/**
	 * 
	 * @return
	 */
	Object getNative();
	
}//end of interface...
