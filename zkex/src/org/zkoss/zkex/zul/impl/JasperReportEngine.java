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

import org.zkoss.lang.Objects;
import org.zkoss.zul.Report;
import org.zkoss.zul.impl.ReportEngine;

import net.sf.jasperreports.engine.JRDataSource;
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
	private JRDataSource _dataSource;

	public JasperReportEngine() {
		super();
	}

	public JasperReportEngine(JRDataSource source) {
		_dataSource = source;
	}

	//-- ReportEngine --//
	public byte[] produceReport(Report report) {
		InputStream is = null;
		// generate report pdf stream
		is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(report.getSrc());

		byte[] buf = null;
		try {
			buf = JasperRunManager.runReportToPdf(is,
					report.getKeyValue() == null ? new HashMap() : report
							.getKeyValue(),
					getDataSource() == null ? new JREmptyDataSource()
							: getDataSource());

		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf;
	}

	public JRDataSource getDataSource() {
		return _dataSource;
	}

	public void setDataSource(JRDataSource dataSource) {
		if (!Objects.equals(_dataSource, dataSource)) {
			_dataSource = dataSource;
		}
	}

}
