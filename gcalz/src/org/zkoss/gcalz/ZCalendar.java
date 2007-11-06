/**
 * 
 */
package org.zkoss.gcalz;

import java.util.List;

/**
 * @author ian
 *
 *
 */
public interface ZCalendar 
{
	/**
	 * 
	 * @return
	 */
	List getEvents();
	/**
	 * 
	 * @return
	 */
	String getTitle();
	/**
	 * 
	 * @return
	 */
	CalendarQueryModifer getModifer();
	/**
	 * 
	 * @param modifer
	 */
	void setModifer(CalendarQueryModifer modifer);
}//end of interface...
