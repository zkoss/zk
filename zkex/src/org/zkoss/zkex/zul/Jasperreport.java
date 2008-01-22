/* Jasperreport.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 16 14:37:13     2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperRunManager;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zul.impl.Utils;

/**
 * The JasperReport component.
 * It is used to generate a Jasper report into an inline frame.
 * 
 * <p>Note: this component is serializable only if the data source
 * ({@link #getDatasource}) is serializable.
 *
 * @author gracelin
 * @since 3.0.2
 */
public class Jasperreport extends HtmlBasedComponent {
	private static final Log log = Log.lookup(Jasperreport.class);
	private static final long serialVersionUID = 20080117L;

	private String _src;
	private Map _parameters;
	private JRDataSource _datasource;
	private int _medver;

	public Jasperreport() {
		setHeight("100%");
		setWidth("100%");
	}

	public Jasperreport(String src) {
		setSrc(src);
	}

	/**
	 * Returns the source (jasper file).
	 * 
	 * @return src The compiled file (jasper file).
	 */
	public String getSrc() {
		return _src;
	}

	/**
	 * Sets the source (jasper file).
	 * <p>
	 * If src is changed, the whole component is invalidate.
	 * 
	 * @param src
	 *            The compiled file (jasper file). If null or empty, nothing is included.
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();
		}
	}

	/**
	 * Returns the attributes for generating the HTML tags.
	 */
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		if (_src == null)
			return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		StringTokenizer st = new StringTokenizer(_src, ".");
		HTMLs.appendAttribute(sb, "src", Utils.getDynamicMediaURI(this,
				_medver++, st.nextToken(), "pdf"));

		return sb.toString();
	}

	/**
	 * Returns the JasperReports Parameters.
	 */
	public Map getParameters() {
		return _parameters;
	}

	/**
	 * Sets the JasperReports Parameters.
	 * 
	 * @param parameters use to fill the report
	 */
	public void setParameters(Map parameters) {
		if (!Objects.equals(_parameters, parameters)) {
			_parameters = parameters;
			invalidate();
		}
	}

	/**
	 * Returns the JasperReports DataSource.
	 */
	public JRDataSource getDatasource() {
		return _datasource;
	}

	/**
	 * Sets the JasperReports DataSource.
	 * 
	 * @param dataSource use to fill the report
	 */
	public void setDatasource(JRDataSource dataSource) {
		if (!Objects.equals(_datasource, dataSource)) {
			_datasource = dataSource;
			invalidate();
		}
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl extends HtmlBasedComponent.ExtraCtrl
	implements DynamicMedia {
		// -- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return doReport();
		}
	}

	/**
	 * Use the Parameters & Data sourse to produce report.
	 * If parameters are null, we will use an empty Map. 
	 * If data source is null, use JREmptyDataSource.
	 * 
	 * @return A AMedia contains report's byte stream.
	 */
	private AMedia doReport() {

		InputStream is = null;
		
		try {
			// generate report pdf stream

			if (is == null) {// try to load by web context.
				is = Executions.getCurrent().getDesktop().getWebApp()
						.getResourceAsStream(_src);
				if (is == null) {// try to load by class loader
					is = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(_src);
					if (is == null) {// try to load by file
						File fl = new File(_src);
						if (!fl.exists())
							throw new RuntimeException("resource for " + _src + " not found.");

						is = new FileInputStream(fl);
					}
				}
			}

			if (_parameters == null)
				_parameters = new HashMap();
			if (_datasource == null)
				_datasource = new JREmptyDataSource();

			final byte[] buf = JasperRunManager.runReportToPdf(is, _parameters,
					_datasource);

			// prepare the AMedia
			final InputStream mediais = new ByteArrayInputStream(buf);
			return new AMedia("report.pdf", "pdf",
					"application/pdf", mediais);

			
		} catch (Exception ex) {
			throw new UiException(ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
}
