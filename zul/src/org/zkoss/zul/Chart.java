/* Chart.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 16:57:48     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.impl.ChartEngine;
import org.zkoss.zul.event.ChartDataEvent;
import org.zkoss.zul.event.ChartDataListener;
import org.zkoss.zul.event.ChartAreaListener;
import org.zkoss.image.AImage;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;

import java.awt.Font;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * The generic chart component. Developers set proper chart type, data model,
 * and the threeD (3D) attribute to draw proper chart. The model and type must
 * match to each other; or the result is unpredictable. The 3D chart is not supported
 * on all chart type.
 *
 * <table>
 *   <tr><th>type</th><th>model</th><th>3D</th></tr>
 *   <tr><td>area</td><td>{@link CategoryModel} or {@link XYModel}</td><td>No</td></tr>
 *   <tr><td>bar</td><td>{@link CategoryModel}</td><td>Yes</td></tr>
 *   <tr><td>bubble</td><td>{@link XYZModel}</td><td>No</td></tr>
 *   <tr><td>candlestick</td><td>{@link HiLoModel}</td><td>No</td></tr>
 *   <tr><td>dial</td><td>@{link DialModel}</td><td>No</td></tr>
 *   <tr><td>gantt</td><td>{@link GanttModel}</td><td>No</td></tr>
 *   <tr><td>highlow</td><td>{@link HiLoModel}</td><td>No</td></tr>
 *   <tr><td>histogram</td><td>{@link XYModel}</td><td>No</td></tr>
 *   <tr><td>line</td><td>{@link CategoryModel} or {@link XYModel}</td><td>Yes</td></tr>
 *   <tr><td>pie</td><td>{@link PieModel}</td><td>Yes</td></tr>
 *   <tr><td>polar</td><td>{@link XYModel}</td><td>No</td></tr>
 *   <tr><td>ring</td><td>{@link PieModel}</td><td>No</td></tr>
 *   <tr><td>scatter</td><td>{@link XYModel}</td><td>No</td></tr>
 *   <tr><td>stacked_bar</td><td>{@link CategoryModel}</td><td>Yes</td></tr>
 *   <tr><td>stacked_area</td><td>{@link CategoryModel} or {@link XYModel}</td><td>No</td></tr>
 *   <tr><td>step</td><td>{@link XYModel}</td><td>No</td></tr>
 *   <tr><td>step_area</td><td>{@link XYModel}</td><td>No</td></tr>
 *   <tr><td>time_series</td><td>{@link XYModel}</td><td>No</td></tr>
 *   <tr><td>wafermap</td><td>{@link WaferMapModel}</td><td>No</td></tr>
 *   <tr><td>waterfall</td><td>{@link CategoryModel}</td><td>No</td></tr>
 *   <tr><td>wind</td><td>{@link XYZModel}</td><td>No</td></tr>
 * </table>
 *
 * @see ChartEngine
 * @see ChartModel
 * @author henrichen
 */
public class Chart extends Imagemap implements org.zkoss.zul.api.Chart {
	private static final long serialVersionUID = 20091008183601L;
	//chart type
	public static final String PIE = "pie";
	public static final String RING = "ring";
	public static final String BAR = "bar";
	public static final String LINE = "line";
	public static final String AREA = "area";
	public static final String STACKED_BAR = "stacked_bar";
	public static final String STACKED_AREA = "stacked_area";
	public static final String WATERFALL = "waterfall";
	public static final String POLAR = "polar";
	public static final String SCATTER = "scatter";
	public static final String TIME_SERIES = "time_series";
	public static final String STEP = "step";
	public static final String STEP_AREA = "step_area";
	public static final String HISTOGRAM = "histogram";
	public static final String CANDLESTICK = "candlestick";
	public static final String HIGHLOW = "highlow";
	public static final String BUBBLE = "bubble"; //@since 3.5.0
	public static final String WAFERMAP = "wafermap"; //@since 3.5.0
	public static final String GANTT = "gantt"; //@since 3.5.0
	public static final String WIND = "wind"; //@since 3.5.0
	public static final String DIAL = "dial"; //@since 3.6.3
	
	private static final Map DEFAULT_MODEL = new HashMap();
	static {
		DEFAULT_MODEL.put(PIE, "org.zkoss.zul.SimplePieModel");
		DEFAULT_MODEL.put(RING, "org.zkoss.zul.SimplePieModel");
		DEFAULT_MODEL.put(BAR, "org.zkoss.zul.SimpleCategoryModel");
		DEFAULT_MODEL.put(LINE, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(AREA, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(STACKED_BAR, "org.zkoss.zul.SimpleCategoryModel");
		DEFAULT_MODEL.put(STACKED_AREA, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(WATERFALL, "org.zkoss.zul.SimpleCategoryModel");
		DEFAULT_MODEL.put(POLAR, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(SCATTER, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(TIME_SERIES, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(STEP, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(STEP_AREA, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(HISTOGRAM, "org.zkoss.zul.SimpleXYModel");
		DEFAULT_MODEL.put(CANDLESTICK, "org.zkoss.zul.SimpleHiLoModel");
		DEFAULT_MODEL.put(HIGHLOW, "org.zkoss.zul.SimpleHiLoModel");
		DEFAULT_MODEL.put(BUBBLE, "org.zkoss.zul.SimpleXYZModel"); //@since 3.5.0
		DEFAULT_MODEL.put(WAFERMAP, "org.zkoss.zul.WaferMapModel"); //@since 3.5.0
		DEFAULT_MODEL.put(GANTT, "org.zkoss.zul.GanttModel"); //@since 3.5.0
		DEFAULT_MODEL.put(WIND, "org.zkoss.zul.SimpleXYZModel"); //@since 3.5.0
		DEFAULT_MODEL.put(DIAL, "org.zkoss.zul.DialModel"); //@since 3.6.3 
	}
	
	//Time Series Chart Period
	public static final String YEAR = "year";
	public static final String QUARTER = "quarter";
	public static final String MONTH = "month";
	public static final String WEEK = "week";
	public static final String DAY = "day";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	public static final String SECOND = "second";
	public static final String MILLISECOND = "millisecond";
	
	//control variable
	private boolean _smartDrawChart; //whether post the smartDraw event already?
	private EventListener _smartDrawChartListener; //the smartDrawListner
	private ChartDataListener _dataListener;

	private String _type = PIE; //chart type (pie, ring, bar, line, xy, etc)
	private boolean _threeD; //whether a 3D chart
	
	//chart related attributes
	private String _title; //chart title
	private int _intWidth = 400; //default to 400
	private int _intHeight = 200; //default to 200
	private String _xAxis;
	private String _yAxis;
	private boolean _showLegend = true; // wether show legend
	private boolean _showTooltiptext = true; //wether show tooltiptext
	private String _orient = "vertical"; //orient
	private ChartAreaListener _areaListener; //callback function when chart area changed
    private String _paneColor; // pane's color
    private int[] _paneRGB = new int[] {0xEE,0xEE,0xEE}; //pane red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
    private int _paneAlpha = 255; //pane alpha transparency (0 ~ 255, default to 255)
	
	//plot related attributes
	private int _fgAlpha = 255; //foreground alpha transparency (0 ~ 255, default to 255)
	private String _bgColor;
	private int[] _bgRGB = new int[] {0xFF,0xFF,0xFF}; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	private int _bgAlpha = 255; //background alpha transparency (0 ~ 255, default to 255)
	
	//Time Series Chart related attributes
	private TimeZone _tzone;
	private String _period;
	private String _dateFormat;
	
	//chart data model
	private ChartModel _model; //chart data model
	
	//chart engine
	private ChartEngine _engine; //chart engine. model and engine is related
	
	//chart Font
	private Font _titleFont; //chart's title font
	private Font _legendFont; //chart's lengend font
	private Font _xAxisTickFont; //chart's x axis tick number font
	private Font _xAxisFont; //chart's x axis font
	private Font _yAxisTickFont; //chart's y axis tick number font
	private Font _yAxisFont; //chart's y axis font
	
	public Chart() {
		init();
		setWidth("500px");
		setHeight("250px");
	}
	
	private ChartModel createDefaultModel() {
		if (WAFERMAP.equals(getType())) {
			return new WaferMapModel(100,100);
		}
		final String klass = (String) DEFAULT_MODEL.get(getType());
		if (klass != null) {
			try {
				return (ChartModel) Classes.newInstanceByThread(klass);
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
		} else {
			throw new UiException("unknown chart type: "+getType());
		}
	}
	
	private void init() {
		if (_smartDrawChartListener == null) {
			_smartDrawChartListener = new SmartDrawListener();
			addEventListener("onSmartDrawChart", _smartDrawChartListener);
		}
	}
	
	private class SmartDrawListener implements EventListener, Serializable {
		private static final long serialVersionUID = 20091008183610L;
		public void onEvent(Event event) throws Exception {
			doSmartDraw();
		}
	}
	
	private void doSmartDraw() {
		if (Strings.isBlank(getType()))
			throw new UiException("chart must specify type (pie, bar, line, ...)");

		if (_model == null) {
			_model = createDefaultModel();
		}

		if (Strings.isBlank(getWidth()))
			throw new UiException("chart must specify width");
			
		if (Strings.isBlank(getHeight()))
			throw new UiException("chart must specify height");
			
		try {
			final AImage image = new AImage("chart"+new Date().getTime(), getEngine().drawChart(Chart.this));
			setContent(image);
		} catch(java.io.IOException ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			_smartDrawChart = false;
		}
	}
	
	/**
	 * Set the chart's type (Chart.PIE, Chart.BAR, Chart.LINE, etc.).
	 *
	 * <p>Default: pie.
	 *
	 */
	public void setType(String type) {
		if (Objects.equals(_type, type)) {
			return;
		}
		_type = type;
		smartDrawChart();
	}
	
	/**
	 * Get the chart's type.
	 */
	public String getType() {
		return _type;
	}

	/**
	 * Set true to show three dimensional graph (If a type of chart got no 3d peer, this is ignored).
	 */
	public void setThreeD(boolean b) {
		if (_threeD == b) {
			return;
		}
		_threeD = b;
		smartDrawChart();
	}
	
	/**
	 * Whether a 3d chart.
	 */
	public boolean isThreeD() {
		return _threeD;
	}

	/**
	 * Set the chart's title.
	 * @param title the chart's title.
	 *
	 */
	public void setTitle(String title) {
		if (Objects.equals(_title, title)) {
			return;
		}
		_title = title;
		smartDrawChart();
	}
	
	/**
	 * Get the chart's title.
	 */
	public String getTitle() {
		return _title;
	}
	
	/**
	 * Override super class to prepare the int width.
	 */
	public void setWidth(String w) {
		if (Objects.equals(w, getWidth())) {
			return;
		}
		_intWidth = stringToInt(w);
		super.setWidth(w);
		smartDrawChart();
	}
	
	/**
	 * Get the chart int width in pixel; to be used by the derived subclass.
	 */
	public int getIntWidth() {
		return _intWidth;
	}
	
	/**
	 * Override super class to prepare the int height.
	 */
	public void setHeight(String h) {
		if (Objects.equals(h, getHeight())) {
			return;
		}
		_intHeight = stringToInt(h);
		super.setHeight(h);
		smartDrawChart();
	}
	
	/**
	 * Get the chart int width in pixel; to be used by the derived subclass.
	 */
	public int getIntHeight() {
		return _intHeight;
	}
	
	/**
	 * Set the label in xAxis.
	 * @param label label in xAxis.
	 */
	public void setXAxis(String label) {
		if (Objects.equals(_xAxis, label)) {
			return;
		}
		_xAxis = label;
		smartDrawChart();
	}
	
	/**
	 * Get the label in xAxis.
	 */
	public String getXAxis() {
		return _xAxis;
	}
	
	/**
	 * Set the label in yAxis.
	 * @param label label in yAxis.
	 */
	public void setYAxis(String label) {
		if (Objects.equals(_yAxis, label)) {
			return;
		}
		_yAxis = label;
		smartDrawChart();
	}
	
	/**
	 * Get the label in yAxis.
	 */
	public String getYAxis() {
		return _yAxis;
	}

	/**
	 * whether show the chart's legend.
	 * @param showLegend true if want to show the legend (default to true).
	 */
	public void setShowLegend(boolean showLegend) {
		if (_showLegend == showLegend) {
			return;
		}
		_showLegend = showLegend;
		smartDrawChart();
	}
	
	/**
	 * Check whether show the legend of the chart.
	 */
	public boolean isShowLegend() {
		return _showLegend;
	}
		
	/**
	 * whether show the chart's tooltip.
	 * @param showTooltiptext true if want to pop the tooltiptext (default to true).
	 */
	public void setShowTooltiptext(boolean showTooltiptext) {
		if (_showTooltiptext == showTooltiptext) {
			return;
		}
		_showTooltiptext = showTooltiptext;
		smartDrawChart();
	}
	
	/**
	 * Check whether show the tooltiptext.
	 */
	public boolean isShowTooltiptext() {
		return _showTooltiptext;
	}
	
	/**
	 * Set the pane alpha (transparency, 0 ~ 255).
	 * @param alpha the transparency of pane color (0 ~ 255, default to 255 opaque).
	 */
	public void setPaneAlpha(int alpha) {
		if (alpha == _paneAlpha) {
			return;
		}
		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_paneAlpha = alpha;
		smartDrawChart();
	}
	
	/**
	 * Get the pane alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getPaneAlpha() {
		return _paneAlpha;
	}

	/**
	 * Set the pane color of the chart.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setPaneColor(String color) {
		if (Objects.equals(color, _paneColor)) {
			return;
		}
		_paneColor = color;
		if (_paneColor == null) {
			_paneRGB = null;
		} else {
			_paneRGB = new int[3];
			decode(_paneColor, _paneRGB);
		}
		smartDrawChart();
	}
	
	/**
	 * Get the pane color of the chart (in string as #RRGGBB).
	 * null means default.
	 */
	public String getPaneColor() {
		return _paneColor;
	}
	
	/**
	 * Get the pane color in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getPaneRGB() {
		return _paneRGB;
	}

	/**
	 * Set the foreground alpha (transparency, 0 ~ 255).
	 * @param alpha the transparency of foreground color (0 ~ 255, default to 255 opaque).
	 */
	public void setFgAlpha(int alpha) {
		if (alpha == _fgAlpha) {
			return;
		}
		
		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_fgAlpha = alpha;
		smartDrawChart();
	}

	/**
	 * Get the foreground alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getFgAlpha() {
		return _fgAlpha;
	}

	/**
	 * Set the background alpha (transparency, 0 ~ 255).
	 * @param alpha the transparency of background color (0 ~ 255, default to 255 opaque).
	 */
	public void setBgAlpha(int alpha) {
		if (alpha == _bgAlpha) {
			return;
		}
		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_bgAlpha = alpha;
		smartDrawChart();
	}
	
	/**
	 * Get the background alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getBgAlpha() {
		return _bgAlpha;
	}

	/**
	 * Set the background color of the chart.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setBgColor(String color) {
		if (Objects.equals(color, _bgColor)) {
			return;
		}
		_bgColor = color;
		if (_bgColor == null) {
			_bgRGB = null;
		} else {
			_bgRGB = new int[3];
			decode(_bgColor, _bgRGB);
		}
		smartDrawChart();
	}
	
	/**
	 * Get the background color of the chart (in string as #RRGGBB).
	 * null means default.
	 */
	public String getBgColor() {
		return _bgColor;
	}
	
	/**
	 * Get the background color in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getBgRGB() {
		return _bgRGB;
	}
	
	/**
	 * Set the chart orientation.
	 * @param orient vertical or horizontal (default to vertical)
	 */
	public void setOrient(String orient) {
		if (Objects.equals(orient, _orient)) {
			return;
		}
		_orient = orient;
		smartDrawChart();
	}
	
	/**
	 * Get the chart orientation (vertical or horizontal)
	 */
	public String getOrient() {
		return _orient;
	}
	
	/** Returns the time zone that this Time Series Chart belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link org.zkoss.util.TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone() {
		return _tzone;
	}
	/** Sets the time zone that this Time Series Chart belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link org.zkoss.util.TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone) {
		if (Objects.equals(tzone, _tzone)) {
			return;
		}
		_tzone = tzone;
		smartDrawChart();
	}

	/** Returns the period used in Time Series Chart. The value can be
	 * "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", and "year".
	 * default is "millisecond" if not specified.
	 */
	public String getPeriod() {
		return _period;
	}
	
	/** Sets the period used in Time Series Chart. The value can be
	 * "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", and "year".
	 */
	public void setPeriod(String period) {
		if (Objects.equals(period, _period)) {
			return;
		}
		_period = period;
		smartDrawChart();
	}
	
	/**
	 * Returns the date format used by date related Chart.
	 * @return the date format used by date related Chart..
	 */
	public String getDateFormat() {
		return _dateFormat;
	}
	
	/**
	 * Sets the date format used by date related Chart.
	 * @param format
	 */
	public void setDateFormat(String format) {
		if (Objects.equals(format, _dateFormat)) {
			return;
		}
		_dateFormat = format;
		smartDrawChart();
	}
	
	/** Returns the chart model associated with this chart, or null
	 * if this chart is not associated with any chart data model.
	 */
	public ChartModel getModel() {
		return _model;
	}

	/** Sets the chart model associated with this chart.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * @param model the chart model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 */
	public void setModel(ChartModel model) {
		if (_model != model) {
			if (_model != null) {
				_model.removeChartDataListener(_dataListener);
			}
			_model = model;
			initDataListener();
		}
		
		//Always redraw
		smartDrawChart();
	}

	/**
	 * Returns  the title font of this chart. If you saw squares rather than correct
	 * words in title, check whether the default title font supports your
	 * characters (e.g. Chinese). You probably have to set this font accordingly.
	 * @return the title font
	 */
	public Font getTitleFont() {
		return _titleFont;
	}

	/**
	 * Sets the title font of this chart. If you saw squares rather than correct
	 * words in title, check whether the default title font supports your
	 * characters (e.g. Chinese). You probably have to set this font accordingly.
	 * @param font the title font of this chart 
	 */
	public void setTitleFont(Font font) {
		if (Objects.equals(font, _titleFont)) {
			return;
		}
		_titleFont = font;
		smartDrawChart();
	}

	/**
	 * Returns the legend font of this chart. If you saw squares rather than correct
	 * words in legend, check whether the default legend font supports your
	 * characters (e.g. Chinese). You probably have to set this font accordingly.
	 * @return the title font
	 */
	public Font getLegendFont() {
		return _legendFont;
	}

	/**
	 * Sets the legend font of this chart. If you saw squares rather than correct
	 * words in legend, check whether the default legend font supports your
	 * characters (e.g. Chinese). You probably have to set this font accordingly.
	 * @param font the legend font of this chart 
	 */
	public void setLegendFont(Font font) {
		if (Objects.equals(_legendFont, font)) {
			return;
		}
		_legendFont = font;
		smartDrawChart();
	}

	/**
	 * Returns the tick number font of x axis of this chart. If you saw squares 
	 * rather than correct words in x axis tick, check whether the default x axis 
	 * tick font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @return the tick number font of x axis of this chart 
	 */
	public Font getXAxisTickFont() {
		return _xAxisTickFont;
	}

	/**
	 * Sets the tick number font of x axis of this chart. If you saw squares 
	 * rather than correct words in x axis tick, check whether the default x axis 
	 * tick font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @param axisTickFont the tick number font of x axis of this chart 
	 */
	public void setXAxisTickFont(Font axisTickFont) {
		if (Objects.equals(_xAxisTickFont, axisTickFont)) {
			return;
		}
		_xAxisTickFont = axisTickFont;
		smartDrawChart();
	}

	/**
	 * Returns the label font of x axis of this chart. If you saw squares 
	 * rather than correct words in x axis label, check whether the default x axis 
	 * label font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @return the label font of x axis of this chart 
	 */
	public Font getXAxisFont() {
		return _xAxisFont;
	}

	/**
	 * Sets the label font of x axis of this chart. If you saw squares 
	 * rather than correct words in x axis label, check whether the default x axis 
	 * label font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @param axisFont the label font of x axis of this chart 
	 */
	public void setXAxisFont(Font axisFont) {
		if (Objects.equals(_xAxisFont, axisFont)) {
			return;
		}
		_xAxisFont = axisFont;
		smartDrawChart();
	}

	/**
	 * Returns the tick number font of y axis of this chart. If you saw squares 
	 * rather than correct words in y axis tick, check whether the default y axis 
	 * tick font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @return the tick number font of y axis of this chart 
	 */
	public Font getYAxisTickFont() {
		return _yAxisTickFont;
	}

	/**
	 * Sets the tick number font of y axis of this chart. If you saw squares 
	 * rather than correct words in y axis tick, check whether the default y axis 
	 * tick font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @param axisTickFont the tick number font of y axis of this chart 
	 */
	public void setYAxisTickFont(Font axisTickFont) {
		if (Objects.equals(_yAxisTickFont, axisTickFont)) {
			return;
		}
		_yAxisTickFont = axisTickFont;
		smartDrawChart();
	}

	/**
	 * Returns the label font of y axis of this chart. If you saw squares 
	 * rather than correct words in y axis label, check whether the default y axis 
	 * label font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @return the label font of y axis of this chart 
	 */
	public Font getYAxisFont() {
		return _yAxisFont;
	}

	/**
	 * Sets the label font of y axis of this chart. If you saw squares 
	 * rather than correct words in y axis label, check whether the default y axis 
	 * label font supports your characters (e.g. Chinese). You probably 
	 * have to set this font accordingly.
	 * @param axisFont the tick number font of y axis of this chart 
	 */
	public void setYAxisFont(Font axisFont) {
		if (Objects.equals(_yAxisFont, axisFont)) {
			return;
		}
		_yAxisFont = axisFont;
		smartDrawChart();
	}
	
	/** Sets the model by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setModel(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null) {
			setModel((ChartModel)Classes.newInstanceByThread(clsnm));
		}
	}

	/** Returns the implemetation chart engine.
	 * @exception UiException if failed to load the engine.
	 */
	public ChartEngine getEngine() throws UiException {
		if (_engine == null)
			_engine = newChartEngine();
		return _engine;
	}
	/** Instantiates the default chart engine.
	 * It is called, if {@link #setEngine} is not called with non-null
	 * engine.
	 *
	 * <p>By default, it looks up the component attribute called
	 * chart-engine. If found, the value is assumed to be the class
	 * or the class name of the default engine (it must implement
	 * {@link ChartEngine}).
	 * If not found, {@link UiException} is thrown.
	 *
	 * <p>Derived class might override this method to provide your
	 * own default class.
	 *
	 * @exception UiException if failed to instantiate the engine
	 * @since 3.0.0
	 */
	protected ChartEngine newChartEngine() throws UiException {
		Object v = getAttribute("chart-engine");
		if (v == null)
			v = "org.zkoss.zkex.zul.impl.JFreeChartEngine";

		try {
			final Class cls;
			if (v instanceof String) {
				cls = Classes.forNameByThread((String)v);
			} else if (v instanceof Class) {
				cls = (Class)v;
			} else {
				throw new UiException(v != null ? "Unknown chart-engine, "+v:
					"The chart-engine attribute is not defined");
			}
	
			v = cls.newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
		if (!(v instanceof ChartEngine))
			throw new UiException(ChartEngine.class + " must be implemented by "+v);
		return (ChartEngine)v;
	}
	
	/** Sets the chart engine.
	 */
	public void setEngine(ChartEngine engine) {
		if (_engine != engine) {
			_engine = engine;
		}
		
		//Always redraw
		smartDrawChart();
	}

	/** Sets the chart engine by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setEngine(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null) {
			setEngine((ChartEngine)Classes.newInstanceByThread(clsnm));
		}
	}
				
	private void initDataListener() {
		if (_dataListener == null) {
			_dataListener = new MyChartDataListener();
			_model.addChartDataListener(_dataListener);
		}
	}
	
	private class MyChartDataListener implements ChartDataListener, Serializable {
		private static final long serialVersionUID = 20091008183622L;

		public void onChange(ChartDataEvent event) {
			smartDrawChart();
		}
	}

	/** Returns the renderer to render each area, or null if the default
	 * renderer is used.
	 */
	public ChartAreaListener getAreaListener() {
		return _areaListener;
	}
	/** Sets the renderer which is used to render each area.
	 *
	 * <p>Note: changing a render will not cause the chart to re-render.
	 * If you want it to re-render, you could call smartDraw.
	 *
	 * @param listener the area listener, or null to ignore it.
	 * @exception UiException if failed to initialize.
	 */
	public void setAreaListener(ChartAreaListener listener) {
		if (_areaListener != listener) {
			_areaListener = listener;
		}
	}
	/** Sets the renderer by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setAreaListener(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null) {
			setAreaListener((ChartAreaListener)Classes.newInstanceByThread(clsnm));
		}
	}

	/**
	 * mark a draw flag to inform that this Chart needs update.
	 */
	protected void smartDrawChart() {
		if (_smartDrawChart) { //already mark smart draw
			return;
		}
		_smartDrawChart = true;
		Events.postEvent("onSmartDrawChart", this, null);
	}
		
	//-- utilities --//
	/*package*/ static void decode(String color, int[] rgb) {
		if (color == null) {
			return;
		}
		if (color.length() != 7 || !color.startsWith("#")) {
			throw new UiException("Incorrect color format (#RRGGBB) : "+color);
		}
		rgb[0] = Integer.parseInt(color.substring(1, 3), 16);
		rgb[1] = Integer.parseInt(color.substring(3, 5), 16);
		rgb[2] = Integer.parseInt(color.substring(5, 7), 16);
	}
	
	/*package*/ static int stringToInt(String str) {
		int j = str.lastIndexOf("px");
		if (j > 0) {
			final String num = str.substring(0, j);
			return Integer.parseInt(num);
		}
		
		j = str.lastIndexOf("pt");
		if (j > 0) {
			final String num = str.substring(0, j);
			return (int) (Integer.parseInt(num) * 1.3333);
		}

		j = str.lastIndexOf("em");
		if (j > 0) {
			final String num = str.substring(0, j);
			return (int) (Integer.parseInt(num) * 13.3333);
		}
		return Integer.parseInt(str);
	}

	public boolean addEventListener(String evtnm, EventListener listener) {
		final boolean ret = super.addEventListener(evtnm, listener);
		if (Events.ON_CLICK.equals(evtnm) && ret)
			smartDrawChart(); //since Area has to generate
		return ret;
	}

	//Cloneable//
	public Object clone() {
		final Chart clone = (Chart)super.clone();

		// Due to the not unique ID of the area component creating in JFreeChartEngine, we have to clear
		// all its children first.
		clone.getChildren().clear();
		clone._smartDrawChartListener = null;
		clone._smartDrawChart = false;
		clone.init();
		clone.doSmartDraw();
		if (clone._model != null) {
			clone._dataListener = null;
			clone.initDataListener();
		}
		
		return clone;
	}
}
