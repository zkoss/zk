/* Jasperreport.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul.api;

import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import org.zkoss.util.Locales;

/**
 * The JasperReport component. It is used to generate a Jasper report into an
 * inline frame.
 * 
 * <p>
 * Note: this component is serializable only if the data source (
 * {@link #getDatasource}) is serializable.
 * 
 * @author gracelin
 * @since 3.0.2
 */
public interface Jasperreport extends org.zkoss.zul.api.Iframe {

	/**
	 * Returns the JasperReports Parameters.
	 * <p>
	 * Default: null.
	 */
	public Map getParameters();

	/**
	 * Sets the JasperReports Parameters.
	 * 
	 * @param parameters
	 *            use to fill the report
	 */
	public void setParameters(Map parameters);

	/**
	 * Returns the JasperReports DataSource.
	 * <p>
	 * Default: null.
	 */
	public JRDataSource getDatasource();

	/**
	 * Sets the JasperReports DataSource.
	 * 
	 * @param dataSource
	 *            use to fill the report
	 */
	public void setDatasource(JRDataSource dataSource);

	/**
	 * Returns the output file type.
	 * <p>
	 * Default: pdf.
	 * 
	 * @since 3.0.3
	 */
	public String getType();

	/**
	 * Sets the output file type. Allowed type: pdf, xml, html, rtf, xls, jxl,
	 * csv and odt.
	 * <dl>
	 * <dt>pdf</dt>
	 * <dd>The PDF format.</dd>
	 * <dt>rtf</dt>
	 * <dd>The RTF format (Rich Text Format).</dd>
	 * <dt>xls</dt>
	 * <dd>The Microsoft Excel format generated with <a
	 * href="http://poi.apache.org">Apache POI</a>.</dd>
	 * <dt>jxl</dt>
	 * <dd>The Microsoft Excel format generated with <a
	 * href="http://jexcelapi.sourceforge.net/">JExcelApi</a>.</dd>
	 * <dt>csv</dt>
	 * <dd>The CSV format (Comma-Separated Values)</dd>
	 * <dt>odt</dt>
	 * <dd>The OpenOffice Writer format</dd>
	 * </dl>
	 * 
	 * @param type
	 *            type of output file. If type is null, assign it a default
	 *            value "pdf".
	 * @since 3.0.3
	 */
	public void setType(String type);

	/**
	 * Returns the output file locale.
	 * 
	 * <p>
	 * Default: null (means the ZK default, {@link Locales#getCurrent})).
	 * </p>
	 * 
	 * <table border="1">
	 * <tr>
	 * <td>{@link #getLocale}</td>
	 * <td>{@link #getParameters} with a value<br/>
	 * associated with JRParameter.REPORT_LOCALE</td>
	 * <td>What is used</td>
	 * </tr>
	 * <tr>
	 * <td>X</td>
	 * <td>ignored</td>
	 * <td>X</td>
	 * </tr>
	 * <tr>
	 * <td>null</td>
	 * <td>Y</td>
	 * <td>Y</td>
	 * </tr>
	 * <tr>
	 * <td>null</td>
	 * <td>null</td>
	 * <td>{@link Locales#getCurrent}</td>
	 * </tr>
	 * </table>
	 * 
	 * @since 3.0.4
	 */
	public Locale getLocale();

	/**
	 * Sets the output file locale.
	 * 
	 * @param locale
	 *            the locale. If null, the ZK default is used (
	 *            {@link Locales#getCurrent}).
	 * @see #getLocale
	 * @since 3.0.4
	 */
	public void setLocale(Locale locale);

}
