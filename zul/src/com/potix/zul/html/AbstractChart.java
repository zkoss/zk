/* AbstractChart.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 16:57:48     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.EventListener;
import com.potix.zul.html.event.ChartDataEvent;
import com.potix.zul.html.event.ChartDataListener;
import com.potix.image.AImage;
import com.potix.lang.Classes;
import com.potix.lang.Objects;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Paint;


/**
 * An abstract base chart class.
 *
 * <p>This is the base chart class. All specific chart is derived from this class. 
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public abstract class AbstractChart extends Imagemap {
	//control variable
	private boolean _smartDrawChart; //whether post the smartDraw event already?
	private transient ChartDataListener _dataListener;
	private transient EventListener _smartDrawChartListener; //the smartDrawListner
	
	//chart related attributes
	private String _title; //chart title
	private boolean _showLegend = true; // wether show legend
	private boolean _showTooltiptext = true; //wether show tooltiptext
	private int _intWidth = 400; //default to 400
	private int _intHeight = 200; //default to 200
	private String _orient = "vertical"; //orient
	private AreaRenderer _renderer;
	
	//plot related attributes
	private int _fgAlpha = 255; //foreground alpha transparency (0 ~ 255, default to 255)
	private String _bgColor;
	private int[] _bgRGB; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	private int _bgAlpha = 255; //background alpha transparency (0 ~ 255, default to 255)
	
	//chart data model
	private ChartModel _model; //chart data model
	
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
	}
	
	/**
	 * Get the chart int width in pixel; to be used by the derived subclass.
	 */
	public int getIntHeight() {
		return _intHeight;
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
			initDataListener();
			if (_model != null) {
				_model.removeChartDataListener(_dataListener);
			}
			if (model != null) {
				model.addChartDataListener(_dataListener);
			}
			_model = model;
		}
		
		//Always redraw
		smartDrawChart();
	}

	private void initDataListener() {
		if (_dataListener == null) {
			_dataListener = new ChartDataListener() {
				public void onChange(ChartDataEvent event) {
					smartDrawChart();
				}
			};
		}
	}

	/** Returns the renderer to render each area, or null if the default
	 * renderer is used.
	 */
	public AreaRenderer getAreaRenderer() {
		return _renderer;
	}
	/** Sets the renderer which is used to render each area.
	 *
	 * <p>Note: changing a render will not cause the chart to re-render.
	 * If you want it to re-render, you could call smartDraw.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize.
	 */
	public void setAreaRenderer(AreaRenderer renderer) {
		if (_renderer != renderer) {
			_renderer = renderer;
		}
	}
	/** Sets the renderer by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setAreaRenderer(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null) {
			setAreaRenderer((AreaRenderer)Classes.newInstanceByThread(clsnm));
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
		if (_smartDrawChartListener == null) {
			_smartDrawChartListener = new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event event) {
					try {
						final AImage image = new AImage(getTitle(), drawChart());
						setContent(image);
					} catch(java.io.IOException ex) {
						throw UiException.Aide.wrap(ex);
					} finally {
						_smartDrawChart = false;
					}
				}
			};
			addEventListener("onSmartDrawChart", _smartDrawChartListener);
		}
		Events.postEvent("onSmartDrawChart", this, null);
	}
		
	/**
	 * To be derived by it subclass, which draw the chart and render into image
	 * format as an byte array.
	 */
	abstract protected byte[] drawChart();
		
	//-- utilities --//
	private void decode(String color, int[] rgb) {
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
	
	private int stringToInt(String str) {
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
}
