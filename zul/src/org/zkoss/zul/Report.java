/* Report.java

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
package org.zkoss.zul;

import java.io.*;
import java.util.*;

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zul.impl.ReportEngine;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;

/**
 * The generic report component.
 * 
 * @see ReportEngine
 * @author gracelin
 *
 */
public class Report extends XulElement {
	private static final long serialVersionUID = 1L;
	private String _src;
	private int _medver;

	// chart engine
	private ReportEngine _engine;

	public Report() {
	}

	public Report(String src) {
		setSrc(src);
	}

	/**
	 * Returns the src.
	 * 
	 * @return src
	 */
	public String getSrc() {
		return _src;
	}

	/**Sets the src.
	 * <p>If src is changed, the whole component is invalidate.
	 * 
	 * @param src the source URL. If null or empty, nothing is included.
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
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		if (_src != null) {
			StringTokenizer st = new StringTokenizer(_src, ".");
			HTMLs.appendAttribute(sb, "src", Utils.getDynamicMediaURI(this,
					_medver, st.nextToken(), "pdf"));
		}
		return sb.toString();
	}

	/**
	 * Returns the implemetation report engine.
	 * 
	 * @exception UiException
	 *                if failed to load the engine.
	 */
	public ReportEngine getEngine() throws UiException {
		if (_engine == null)
			_engine = newReportEngine();
		return _engine;
	}

	/**
	 * Instantiates the default report engine. It is called, if
	 * {@link #setEngine} is not called with non-null engine.
	 * 
	 * <p>
	 * By default, it looks up the component attribute called report-engine. If
	 * found, the value is assumed to be the class or the class name of the
	 * default engine (it must implement {@link ChartEngine}). If not found,
	 * {@link UiException} is thrown.
	 * 
	 * <p>
	 * Derived class might override this method to provide your own default
	 * class.
	 * 
	 * @exception UiException
	 *                if failed to instantiate the engine
	 * @since 3.0.0
	 */
	protected ReportEngine newReportEngine() throws UiException {
		Object v = getAttribute("report-engine");
		if (v == null)
			v = "org.zkoss.zkex.zul.impl.JasperReportEngine";

		try {
			final Class cls;
			if (v instanceof String) {
				cls = Classes.forNameByThread((String) v);
			} else if (v instanceof Class) {
				cls = (Class) v;
			} else {
				throw new UiException(v != null ? "Unknown report-engine, " + v
						: "The report-engine attribute is not defined");
			}

			v = cls.newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
		if (!(v instanceof ReportEngine))
			throw new UiException(ReportEngine.class
					+ " must be implemented by " + v);
		return (ReportEngine) v;
	}

	/**
	 * Sets the chart engine.
	 */
	public void setEngine(ReportEngine engine) {
		if (_engine != engine) {
			_engine = engine;
		}

		// Always redraw
		invalidate();
	}

	/**
	 * Sets the report engine by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setEngine(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null) {
			setEngine((ReportEngine) Classes.newInstanceByThread(clsnm));
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
	protected class ExtraCtrl implements DynamicMedia {
		// -- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return doReport();
		}
	}

	private AMedia doReport() {
		ReportEngine re = getEngine();

		// prepare the AMedia
		final InputStream mediais = new ByteArrayInputStream(re
				.produceReport(_src));
		final AMedia amedia = new AMedia("FirstReport.pdf", "pdf",
				"application/pdf", mediais);

		return amedia;
	}
}
