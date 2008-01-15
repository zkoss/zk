/* ReportEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan 14 14:37:13     2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.impl;

import org.zkoss.zul.Report;

/**
 * Report engine is an engine that do the real report producing.
 *
 * This interface defines the report engine for components like {@link org.zkoss.zul.Report}
 * use to get the value of each data and the size of the report data.
 * 
 * @author gracelin
 * 
 */
public interface ReportEngine {
	/**
	 * Produce the report and show into pdf format as an byte array.
	 * 
	 * @param report the data used in producing a report; depends on implementation.
	 */
	byte[] produceReport(Report report);
}
