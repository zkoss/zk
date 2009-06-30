/* DialRange.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 25, 2009 10:12:58 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.zul;

import java.io.Serializable;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * Color range to be marked in {@link DialModelScale}.
 * @author henrichen
 *
 */
public class DialModelRange implements Serializable {
	private DialModelScale _scale;
	private double _lower;
	private double _upper;
	private String _color;
	private int[] _RGB = new int[] {0x00,0x00,0xFF}; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	private double _innerRadius;
	private double _outerRadius;
	
	/*package*/ DialModelRange(DialModelScale scale) {
		_scale = scale;
	}
	
	/**
	 * Setup the DailModel range.
	 * @param lower the lower bound of the range in the scale.
	 * @param upper the upper bound of the range in the scale.
	 * @param color the color in #RRGGBB format (hexdecimal); default to blue.
	 * @param innerRadius the inner radius percentage(0 ~ 1) of the range; default to 0.7.
	 * @param outerRadius the outer radius percentage(0 ~ 1) of the range; default to 0.8;
	 */
	public void setRange(double lower, double upper, String color, double innerRadius, double outerRadius) {
		_lower = lower;
		_upper = upper;
		setRangeColor(color);
		_innerRadius = innerRadius;
		_outerRadius = outerRadius;
	}
	
	/**
	 * Set the range color of the dial range.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setRangeColor(String color) {
		if (Objects.equals(color, _color)) {
			return;
		}
		_color = color;
		if (_color == null) {
			_RGB = null;
		} else {
			_RGB = new int[3];
			Chart.decode(_color, _RGB);
		}
		_scale.fireEvent(ChartDataEvent.CHANGED);
	}
	
	/**
	 * Get the background color of the dial frame (in string as #RRGGBB).
	 * null means default.
	 */
	public String getRangeColor() {
		return _color;
	}
	
	/**
	 * Get the background color of the dial frame in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getRangeRGB() {
		return _RGB;
	}
	
	/**
	 * Sets the inner radius percentage(0 ~ 1) of the dial range; default to 0.7.
	 * @param the inner radius percentage(0 ~ 1) of the dial range; default to 0.7.
	 */
	public void setInnerRadius(double radius) {
		_innerRadius = radius;
	}
	
	/**
	 * Return the inner radius percentage(0 ~ 1) of the dial range; default to 0.7.
	 * @return the inner radius percentage(0 ~ 1) of the dial range; default to 0.7.
	 */
	public double getInnerRadius() {
		return _innerRadius;
	}
	
	/**
	 * Sets the outer radius percentage(0 ~ 1) of the dial range; default to 0.8.
	 * @param the outer radius percentage(0 ~ 1) of the dial range; default to 0.8.
	 */
	public void setOuterRadius(double radius) {
		_outerRadius = radius;
	}
	
	/**
	 * Return the outer radius percentage(0 ~ 1) of the dial range; default to 0.8.
	 * @return the outer radius percentage(0 ~ 1) of the dial range; default to 0.8.
	 */
	public double getOuterRadius() {
		return _outerRadius;
	}

	/**
	 * Returns the lower bound in scale of this colored range.
	 * @return the lower bound in scale of this colored range.
	 */
	public double getLowerBound() {
		return _lower;
	}
	
	/**
	 * Sets the lower bound in scale of this colored range.
	 * @param lower the specified lower bound in the scale.
	 */
	public void setLowerBound(double lower) {
		if (_lower != lower) {
			_lower = lower;
			_scale.fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Returns the upper bound in scale of this colored range.
	 * @return the upper bound in scale of this colored range.
	 */
	public double getUpperBound() {
		return _upper;
	}
	
	/**
	 * Sets the upper bound in scale of this colored range.
	 * 
	 * @param upper the upper bound
	 */
	public void setUpperBound(double upper) {
		if (_upper != upper) {
			_upper = upper;
			_scale.fireEvent(ChartDataEvent.CHANGED);
		}
	}
}
