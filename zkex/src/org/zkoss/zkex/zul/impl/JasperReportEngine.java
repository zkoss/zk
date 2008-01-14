/* JasperReportEngine.java

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
package org.zkoss.zkex.zul.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.impl.ReportEngine;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 * A report engine implemented with JasperReports.
 * 
 * @author gracelin
 *
 */
public class JasperReportEngine implements ReportEngine {
	
	//-- ReportEngine --//
	public byte[] produceReport(String src) {
		InputStream is = null;
		// generate report pdf stream
		is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(src);

		Map params = new HashMap();
		// params.put("ReportTitle", "The First Jasper Report Ever");
		// params.put("MaxOrderID", new Integer(10500));

		byte[] buf = null;
		try {
			buf = JasperRunManager.runReportToPdf(is, params,
					new JREmptyDataSource());

		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf;
	}
}
