/* DialModel.java

	Purpose:
		
	Description:
		
	History:
		Jun 24, 2009 3:21:41 PM, Created by henrichen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.zul;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A data model to be used with dial chart.
 * @author henrichen
 *
 */
public class DialModel extends AbstractChartModel {
	private static final long serialVersionUID = 20091008183229L;

	private List<DialModelScale> _series = new ArrayList<DialModelScale>(4);
	
	//DialFrame background
	private String _bgColor = "#FFFFFF";
	private int[] _bgRGB = new int[] {0xFF,0xFF,0xFF}; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	private int _bgAlpha = 255; //background alpha transparency (0 ~ 255, default to 255)
	private String _bgColor1 = "#FFFFFF";
	private int[] _bgRGB1 = new int[] {0xFF,0xFF,0xFF}; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255) for gradient background starting color
	private String _bgColor2 = "#AAAADC";
	private int[] _bgRGB2 = new int[] {0xAA,0xAA,0xDC}; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255) for gradient background ending color
	
	//DialFrame forgournd
	private String _fgColor;
	private int[] _fgRGB = new int[] {0x80,0x80,0x80}; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	
	//DialBackground gradient direction
	private String _gdirection = "vertical";
	
	//DialCap radius
	private double _capRadius = 0.07;
	
	/**
	 * Returns the number of {@link DialModelScale}s.
	 */
	public int size() {
		return _series.size();
	}
	
	/**
	 * new an instance of scale in this DialModel.
	 * @return an instance of scale in this DialModel.
	 */
	public DialModelScale newScale() {
		final DialModelScale entry = new DialModelScale(this);
		_series.add(entry);
		fireEvent(ChartDataEvent.ADDED, null, null);
		return entry;
	}
	
	/**
	 * new an instance of scale in this DialModel.
	 * @param lowerBound lower bound of this scale.
	 * @param upperBound upper bound of this scale.
	 * @param startAngle starting angle in degree associated to the sclae's lower bound(0 degree point to east, counter-clockwise is positive). 
	 * @param extent angles in degree extended from the starting angle (counter clockwise is positive).
	 * @param majorTickInterval the interval between major tick (in lower bound and upper bound).
	 * @param minorTickCount the number of minor ticks between major tick.
	 */
	public DialModelScale newScale(double lowerBound, double upperBound, 
		double startAngle, double extent, double majorTickInterval, int minorTickCount) {
		final DialModelScale entry = new DialModelScale(this);
		_series.add(entry);
		entry.setScale(lowerBound, upperBound, startAngle, extent, majorTickInterval, minorTickCount);
		fireEvent(ChartDataEvent.ADDED, null, null);
		return entry;
	}
	
	/**
	 * Return the index of the specified model entry.
	 * @param entry the DialModelScale
	 * @return the index of the specified model entry.
	 */
	public int indexOf(DialModelScale entry) {
		return _series.indexOf(entry);
	}
	
	/**
	 * Returns the {@link DialModelScale} of the specified index.
	 * @param index the index of the entry.
	 * @return the DialModelScale.
	 */
	public DialModelScale getScale(int index) {
		return _series.get(index);
	}
	
	/**
	 * Remove the specified DialModelScale from this DialModel.
	 * @param scale
	 */
	public void removeScale(DialModelScale scale) {
		_series.remove(scale);
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}
	
	/**
	 * Get value of the scale per the specified index.
	 * @param index the scale index.
	 */
	public double getValue(int index) {
		return getScale(index).getValue();
	}

	/**
	 * add or update the value of a specified scale index.
	 * @param index the index of the Scale
	 * @param value the value
	 */
	public void setValue(int index,  double value) {
		getScale(index).setValue(value);
	}

	/**
	 * clear the model.
	 */
	public void clear() {
		if (!_series.isEmpty()) {
			_series.clear();
			fireEvent(ChartDataEvent.REMOVED, null, null);
		}
	}

	/**
	 * Set the background alpha of the dial frame (transparency, 0 ~ 255).
	 * @param alpha the transparency of background color (0 ~ 255, default to 255 opaque).
	 */
	public void setFrameBgAlpha(int alpha) {
		if (alpha == _bgAlpha) {
			return;
		}
		if (alpha > 255 || alpha < 0) {
			alpha = 255;
		}
		_bgAlpha = alpha;
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	/**
	 * Get the background alpha of the dial frame (transparency, 0 ~ 255, opacue).
	 */
	public int getFrameBgAlpha() {
		return _bgAlpha;
	}

	/**
	 * Set the background color of the dial frame.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setFrameBgColor(String color) {
		if (Objects.equals(color, _bgColor)) {
			return;
		}
		_bgColor = color;
		if (_bgColor == null) {
			_bgRGB = null;
		} else {
			_bgRGB = new int[3];
			Chart.decode(_bgColor, _bgRGB);
		}
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	/**
	 * Get the background color of the dial frame (in string as #RRGGBB).
	 * null means default.
	 */
	public String getFrameBgColor() {
		return _bgColor;
	}
	
	/**
	 * Get the background color of the dial frame in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getFrameBgRGB() {
		return _bgRGB;
	}
	
	/**
	 * Set the foreground color of the dial frame.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setFrameFgColor(String color) {
		if (Objects.equals(color, _fgColor)) {
			return;
		}
		_fgColor = color;
		if (_fgColor == null) {
			_fgRGB = null;
		} else {
			_fgRGB = new int[3];
			Chart.decode(_fgColor, _fgRGB);
		}
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	/**
	 * Get the foreground color of the dial frame (in string as #RRGGBB).
	 * null means default.
	 */
	public String getFrameFgColor() {
		return _fgColor;
	}
	
	/**
	 * Get the background color of the dial frame in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getFrameFgRGB() {
		return _fgRGB;
	}
	
	/**
	 * Set the 1st background color of the dial frame (for gradient starting color).
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setFrameBgColor1(String color) {
		if (Objects.equals(color, _bgColor1)) {
			return;
		}
		_bgColor1 = color;
		if (_bgColor1 == null) {
			_bgRGB1 = null;
		} else {
			_bgRGB1 = new int[3];
			Chart.decode(_bgColor1, _bgRGB1);
		}
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	/**
	 * Get the 1st background color of the dial frame (in string as #RRGGBB) for gradient starting color.
	 * null means use default.
	 */
	public String getFrameBgColor1() {
		return _bgColor1;
	}
	
	/**
	 * Get the 1st background color of the dial frame in int array (0: red, 1: green, 2:blue) for gradient starting color.
	 * null means use default.
	 */
	public int[] getFrameBgRGB1() {
		return _bgRGB1;
	}

	/**
	 * Set the 2nd background color of the dial frame (for gradient ending color).
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setFrameBgColor2(String color) {
		if (Objects.equals(color, _bgColor2)) {
			return;
		}
		_bgColor2 = color;
		if (_bgColor2 == null) {
			_bgRGB2 = null;
		} else {
			_bgRGB2 = new int[3];
			Chart.decode(_bgColor2, _bgRGB2);
		}
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	/**
	 * Get the 2nd background color of the dial frame (in string as #RRGGBB) for gradient ending color.
	 * null means default.
	 */
	public String getFrameBgColor2() {
		return _bgColor2;
	}
	
	/**
	 * Get the 2nd background color of the dial frame in int array (0: red, 1: green, 2:blue) for gradient ending color.
	 * null means default.
	 */
	public int[] getFrameBgRGB2() {
		return _bgRGB2;
	}

	/**
	 * Set the Frame background gradient color direction (from bgColor to bgColor2); 
	 * center_horizontal, center_vertical, horizontal, vertical. 
	 */
	public void setGradientDirection(String direction) {
		_gdirection = direction;
	}
	
	/**
	 * Returns the Frame background gradient color direction (from bgColor to bgColor2); 
	 * center_horizontal, center_vertical, horizontal, vertical. 
	 */
	public String getGradientDirection() {
		return _gdirection;
	}
	
	/**
	 * Sets the radius percentage(0 ~ 1) of the meter's cap; default to 0.07.
	 * @param radius the radius percentage(0 ~ 1) of the meter's cap.
	 */
	public void setCapRadius(double radius) {
		_capRadius = radius;
	}
	
	/**
	 * Return the radius percentage(0 ~ 1) of the meter's cap; default to 0.07.
	 * @return the radius percentage(0 ~ 1) of the meter's cap.
	 */
	public double getCapRadius() {
		return _capRadius;
	}
}
