/* Chart.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.impl.ChartEngine;
import org.zkoss.zul.event.ChartAreaListener;
import java.util.TimeZone;

/**
 * The generic chart component. Developers set proper chart type, data model,
 * and the threeD (3D) attribute to draw proper chart. The model and type must
 * match to each other; or the result is unpredictable. The 3D chart is not
 * supported on all chart type.
 * 
 * <table>
 * <tr>
 * <th>type</th>
 * <th>model</th>
 * <th>3D</th>
 * </tr>
 * <tr>
 * <td>area</td>
 * <td>{@link org.zkoss.zul.CategoryModel} or {@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>bar</td>
 * <td>{@link org.zkoss.zul.CategoryModel}</td>
 * <td>Yes</td>
 * </tr>
 * <tr>
 * <td>bubble</td>
 * <td>{@link org.zkoss.zul.XYZModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>candlestick</td>
 * <td>{@link org.zkoss.zul.HiLoModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>gantt</td>
 * <td>{@link org.zkoss.zul.GanttModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>hilow</td>
 * <td>{@link org.zkoss.zul.HiLoModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>histogram</td>
 * <td>{@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>line</td>
 * <td>{@link org.zkoss.zul.CategoryModel} or {@link org.zkoss.zul.XYModel}</td>
 * <td>Yes</td>
 * </tr>
 * <tr>
 * <td>pie</td>
 * <td>{@link org.zkoss.zul.PieModel}</td>
 * <td>Yes</td>
 * </tr>
 * <tr>
 * <td>polar</td>
 * <td>{@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>ring</td>
 * <td>{@link org.zkoss.zul.PieModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>scatter</td>
 * <td>{@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>stacked_bar</td>
 * <td>{@link org.zkoss.zul.CategoryModel}</td>
 * <td>Yes</td>
 * </tr>
 * <tr>
 * <td>stacked_area</td>
 * <td>{@link org.zkoss.zul.CategoryModel} or {@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>step</td>
 * <td>{@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>step_area</td>
 * <td>{@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>time_series</td>
 * <td>{@link org.zkoss.zul.XYModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>wafermap</td>
 * <td>{@link org.zkoss.zul.WaferMapModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>waterfall</td>
 * <td>{@link org.zkoss.zul.CategoryModel}</td>
 * <td>No</td>
 * </tr>
 * <tr>
 * <td>wind</td>
 * <td>{@link org.zkoss.zul.XYZModel}</td>
 * <td>No</td>
 * </tr>
 * </table>
 * 
 * @see ChartEngine
 * @see ChartModel
 * @author henrichen
 * @since 3.5.2
 */
public interface Chart extends Imagemap {
	/**
	 * Set the chart's type (Chart.PIE, Chart.BAR, Chart.LINE, etc.).
	 * 
	 * <p>
	 * Default: pie.
	 */
	public void setType(String type);

	/**
	 * Get the chart's type.
	 */
	public String getType();

	/**
	 * Set true to show three dimensional graph (If a type of chart got no 3d
	 * peer, this is ignored).
	 */
	public void setThreeD(boolean b);

	/**
	 * Whether a 3d chart.
	 */
	public boolean isThreeD();

	/**
	 * Set the chart's title.
	 * 
	 * @param title
	 *            the chart's title.
	 * 
	 */
	public void setTitle(String title);

	/**
	 * Get the chart's title.
	 */
	public String getTitle();

	/**
	 * Get the chart int width in pixel; to be used by the derived subclass.
	 */
	public int getIntWidth();

	/**
	 * Get the chart int width in pixel; to be used by the derived subclass.
	 */
	public int getIntHeight();

	/**
	 * Set the label in xAxis.
	 * 
	 * @param label
	 *            label in xAxis.
	 */
	public void setXAxis(String label);

	/**
	 * Get the label in xAxis.
	 */
	public String getXAxis();

	/**
	 * Set the label in yAxis.
	 * 
	 * @param label
	 *            label in yAxis.
	 */
	public void setYAxis(String label);

	/**
	 * Get the label in yAxis.
	 */
	public String getYAxis();

	/**
	 * whether show the chart's legend.
	 * 
	 * @param showLegend
	 *            true if want to show the legend (default to true).
	 */
	public void setShowLegend(boolean showLegend);

	/**
	 * Check whether show the legend of the chart.
	 */
	public boolean isShowLegend();

	/**
	 * whether show the chart's tooltip.
	 * 
	 * @param showTooltiptext
	 *            true if want to pop the tooltiptext (default to true).
	 */
	public void setShowTooltiptext(boolean showTooltiptext);

	/**
	 * Check whether show the tooltiptext.
	 */
	public boolean isShowTooltiptext();

	/**
	 * Set the pane alpha (transparency, 0 ~ 255).
	 * 
	 * @param alpha
	 *            the transparency of pane color (0 ~ 255, default to 255
	 *            opaque).
	 */
	public void setPaneAlpha(int alpha);

	/**
	 * Get the pane alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getPaneAlpha();

	/**
	 * Set the pane color of the chart.
	 * 
	 * @param color
	 *            in #RRGGBB format (hexdecimal).
	 */
	public void setPaneColor(String color);

	/**
	 * Get the pane color of the chart (in string as #RRGGBB). null means
	 * default.
	 */
	public String getPaneColor();

	/**
	 * Get the pane color in int array (0: red, 1: green, 2:blue). null means
	 * default.
	 */
	public int[] getPaneRGB();

	/**
	 * Set the foreground alpha (transparency, 0 ~ 255).
	 * 
	 * @param alpha
	 *            the transparency of foreground color (0 ~ 255, default to 255
	 *            opaque).
	 */
	public void setFgAlpha(int alpha);

	/**
	 * Get the foreground alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getFgAlpha();

	/**
	 * Set the background alpha (transparency, 0 ~ 255).
	 * 
	 * @param alpha
	 *            the transparency of background color (0 ~ 255, default to 255
	 *            opaque).
	 */
	public void setBgAlpha(int alpha);

	/**
	 * Get the background alpha (transparency, 0 ~ 255, opacue).
	 */
	public int getBgAlpha();

	/**
	 * Set the background color of the chart.
	 * 
	 * @param color
	 *            in #RRGGBB format (hexdecimal).
	 */
	public void setBgColor(String color);

	/**
	 * Get the background color of the chart (in string as #RRGGBB). null means
	 * default.
	 */
	public String getBgColor();

	/**
	 * Get the background color in int array (0: red, 1: green, 2:blue). null
	 * means default.
	 */
	public int[] getBgRGB();

	/**
	 * Set the chart orientation.
	 * 
	 * @param orient
	 *            vertical or horizontal (default to vertical)
	 */
	public void setOrient(String orient);

	/**
	 * Get the chart orientation (vertical or horizontal)
	 */
	public String getOrient();

	/**
	 * Returns the time zone that this Time Series Chart belongs to, or null if
	 * the default time zone is used.
	 * <p>
	 * The default time zone is determined by
	 * {@link org.zkoss.util.TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone();

	/**
	 * Sets the time zone that this Time Series Chart belongs to, or null if the
	 * default time zone is used.
	 * <p>
	 * The default time zone is determined by
	 * {@link org.zkoss.util.TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone);

	/**
	 * Returns the period used in Time Series Chart. The value can be
	 * "millisecond", "second", "minute", "hour", "day", "week", "month",
	 * "quarter", and "year". default is "millisecond" if not specified.
	 */
	public String getPeriod();

	/**
	 * Sets the period used in Time Series Chart. The value can be
	 * "millisecond", "second", "minute", "hour", "day", "week", "month",
	 * "quarter", and "year".
	 */
	public void setPeriod(String period);

	/**
	 * Returns the date format used by date related Chart.
	 * 
	 * @return the date format used by date related Chart..
	 */
	public String getDateFormat();

	/**
	 * Sets the date format used by date related Chart.
	 * 
	 * @param format
	 */
	public void setDateFormat(String format);

	/**
	 * Returns the chart model associated with this chart, or null if this chart
	 * is not associated with any chart data model.
	 */
	public ChartModel getModel();

	/**
	 * Sets the chart model associated with this chart. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 * 
	 * @param model
	 *            the chart model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setModel(ChartModel model);

	/**
	 * Sets the model by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setModel(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException;

	/**
	 * Returns the implemetation chart engine.
	 * 
	 * @exception UiException
	 *                if failed to load the engine.
	 */
	public ChartEngine getEngine() throws UiException;

	/**
	 * Sets the chart engine.
	 */
	public void setEngine(ChartEngine engine);

	/**
	 * Sets the chart engine by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setEngine(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException;

	/**
	 * Returns the renderer to render each area, or null if the default renderer
	 * is used.
	 */
	public ChartAreaListener getAreaListener();

	/**
	 * Sets the renderer which is used to render each area.
	 * 
	 * <p>
	 * Note: changing a render will not cause the chart to re-render. If you
	 * want it to re-render, you could call smartDraw.
	 * 
	 * @param listener
	 *            the area listener, or null to ignore it.
	 * @exception UiException
	 *                if failed to initialize.
	 */
	public void setAreaListener(ChartAreaListener listener);

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setAreaListener(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException;

}
