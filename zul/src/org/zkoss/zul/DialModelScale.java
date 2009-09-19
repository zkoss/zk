/* DialModelScale.java

	Purpose:
		
	Description:
		
	History:
		Jun 24, 2009 5:21:08 PM, Created by henrichen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.zul;

import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A set of information of a scale in a Dial chart. You cannot new a DialModelScale
 * directly; instead, use {@link DialModel#newScale()} to start setting the scale.
 * @author henrichen
 *
 */
public class DialModelScale implements Serializable {
	private DialModel _model;
	
	//data
	private double _value;
	
	//text annotation
	private String _text;
	private Font _textFont; //14, 10
	private double _textRadius = 0.7; //0.7, 0.5
	
	//value indicator
	private Font _valueFont; //10, 10
	private double _valueRadius = 0.6; //0.6, 0.6
	private double _valueAngle = -90; //-103.0, -77.0 
	
	//tick
	private Font _tickFont; //14, 10
	private double _tickRadius = 0.88; //0.88, 0.50
	private String _tickColor; //FrameFgColor, 0xFF0000
	private int[] _tickRGB; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	private double _tickLabelOffset = 0.20; //offset between tick and tick label
	
	//scale
	double _lowerBound;
	double _upperBound;
	double _startAngle;
	double _extent;
	double _majorTickInterval;
	int _minorTickCount;
	
	//ranges
	private List _ranges =  new ArrayList(4);
	
	//needle
	private String _needleType = "pointer"; //"pointer", "pin"
	private String _needleColor; //FrameFgColor, 0xFF0000
	private int[] _needleRGB; //background red, green, blue (0 ~ 255, 0 ~ 255, 0 ~ 255)
	private double _needleRadius = 0.9; //0.9, 0.55

	/*package*/ DialModelScale(DialModel model) {
		_model = model;
	}

	public int getIndex() {
		return _model.indexOf(this);
	}
	
	/** Get the value */
	public double getValue() {
		return _value;
	}
	
	public void setValue(double val) {
		if (_value != val) {
			_value = val;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Returns the text annotation of this scale.
	 */
	public String getText() {
		return _text;
	}
	
	/**
	 * Sets the text annotation of this scale; e.g. "Temperature" for a temperature dial meter.
	 * @param text text annotation(subtitle) of this scale.
	 */
	public void setText(String text) {
		if (Objects.equals(text, _text)) {
			_text = text;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	/**
	 * Returns the text annotation font.
	 * @return the text annotation font.
	 */
	public Font getTextFont() {
		return _textFont;
	}
	
	/**
	 * Sets the text annotation font.
	 * @param font the text annotation font.
	 */
	public void setTextFont(Font font) {
		if (Objects.equals(font, _textFont)) {
			_textFont = font;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Return the radius percentage(0 ~ 1) to place the text annotation.
	 * @return the radius percentage(0 ~ 1) to place the text annotation.
	 */
	public double getTextRadius() {
		return _textRadius;
	}
	
	/**
	 * Sets the radius percentage(0 ~ 1) to place the text annotation.
	 * @param radius radius percentage(0 ~ 1) to place the text annotation.
	 */
	public void setTextRadius(double radius) {
		if (_textRadius != radius) {
			_textRadius = radius;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Returns the value font.
	 * @return the value font.
	 */
	public Font getValueFont() {
		return _valueFont;
	}
	
	/**
	 * Sets the value font.
	 * @param font the value font.
	 */
	public void setValueFont(Font font) {
		if (Objects.equals(font, _valueFont)) {
			_valueFont = font;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Return the radius percentage(0 ~ 1) to place the value.
	 * @return the radius percentage(0 ~ 1) to place the value.
	 */
	public double getValueRadius() {
		return _valueRadius;
	}
	
	/**
	 * Sets the radius percentage(0 ~ 1) to place the value.
	 * @param radius radius percentage(0 ~ 1) to place the value.
	 */
	public void setValueRadius(double radius) {
		if (_valueRadius != radius) {
			_valueRadius = radius;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}	

	/**
	 * Return the angle to place the value (counter clockwise is positive).
	 * @return the angle to place the value (counter clockwise is positive).
	 */
	public double getValueAngle() {
		return _valueAngle;
	}
	
	/**
	 * Sets the angle in degree to place the value (counter clockwise is positive).
	 * @param angle angle in degree to place the value (counter clockwise is positive).
	 */
	public void setValueAngle(double angle) {
		if (_valueAngle != angle) {
			_valueAngle = angle;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Sets the scale information of this DialModelScale.
	 * @param lowerBound lower bound of this scale.
	 * @param upperBound upper bound of this scale.
	 * @param startAngle starting angle in degree associated to the sclae's lower bound(0 degree point to east, counter-clockwise is positive). 
	 * @param extent angles in degree extended from the starting angle (counter clockwise is positive).
	 * @param majorTickInterval the interval between major tick (in lower bound and upper bound).
	 * @param minorTickCount the number of minor ticks between major tick.
	 */
	public void setScale(double lowerBound, double upperBound, 
		double startAngle, double extent, double majorTickInterval, int minorTickCount) {
		if (lowerBound != _lowerBound
			|| upperBound != _upperBound
			|| startAngle != _startAngle
			|| extent != _extent
			|| majorTickInterval != _majorTickInterval
			|| minorTickCount != _minorTickCount) {
			_lowerBound = lowerBound;
			_upperBound = upperBound;
			_startAngle = startAngle;
			_extent = extent;
			_majorTickInterval = majorTickInterval;
			_minorTickCount = minorTickCount;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Returns the scale's lower bound.
	 * @return the scale's lower bound.
	 */
	public double getScaleLowerBound() {
		return _lowerBound;
	}
	
	/**
	 * Returns the scale's upper bound.
	 * @return the scale's upper bound.
	 */
	public double getScaleUpperBound() {
		return _upperBound;
	}
	/**
	 * Returns starting angle in degree associated to the sclae's lower bound
	 * (0 degree point to east, counter-clockwise is positive).
	 * @return starting angle in degree associated to the sclae's lower bound
	 * (0 degree point to east, counter-clockwise is positive).
	 */
	public double getScaleStartAngle() {
		return _startAngle;
	}
	/**
	 * Returns angles in degree extended from the starting angle (counter clockwise is positive).
	 * @return angles in degree extended from the starting angle (counter clockwise is positive).
	 */
	public double getScaleExtent() {
		return _extent;
	}
	
	/**
	 * Returns the interval between major tick (in lower bound and upper bound).
	 * @return the interval between major tick (in lower bound and upper bound).
	 */
	public double getMajorTickInterval() {
		return _majorTickInterval;
	}
	
	/**
	 * returns the number of minor ticks between major tick.
	 * @return the number of minor ticks between major tick.
	 */
	public int getMinorTickCount() {
		return _minorTickCount;
	}

	/**
	 * Returns the tick label font.
	 * @return the tick label font.
	 */
	public Font getTickFont() {
		return _tickFont;
	}
	
	/**
	 * Sets the tick label font.
	 * @param font the tick label font.
	 */
	public void setTickFont(Font font) {
		if (Objects.equals(font, _tickFont)) {
			_tickFont = font;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Return the radius percentage(0 ~ 1) to place the tick label.
	 * @return the radius percentage(0 ~ 1) to place the tick label.
	 */
	public double getTickRadius() {
		return _tickRadius;
	}
	
	/**
	 * Sets the radius percentage(0 ~ 1) to place the tick label.
	 * @param radius radius percentage(0 ~ 1) to place the tick label.
	 */
	public void setTickRadius(double radius) {
		if (_tickRadius != radius) {
			_tickRadius = radius;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Returns the radius offset in percentage(0 ~ 1) between the tick and tick label.
	 * @return the radius offset in percentage(0 ~ 1) between the tick and tick label.
	 */
	public double getTickLabelOffset() {
		return _tickLabelOffset;
	}

	/**
	 * Sets the radius offset in percentage(0 ~ 1) between the tick and tick label.
	 * @param tickLabelOffset the radius offset in percentage(0 ~ 1) between the tick and tick label.
	 */
	public void setTickLabelOffset(double tickLabelOffset) {
		if (_tickLabelOffset != tickLabelOffset) {
			_tickLabelOffset = tickLabelOffset;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Set the tick color.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setTickColor(String color) {
		if (Objects.equals(color, _tickColor)) {
			return;
		}
		_tickColor = color;
		if (_tickColor == null) {
			_tickRGB = null;
		} else {
			_tickRGB = new int[3];
			Chart.decode(_tickColor, _tickRGB);
		}
		fireEvent(ChartDataEvent.CHANGED);
	}

	/**
	 * Get the tick color of this scale(in string as #RRGGBB).
	 * null means default.
	 */
	public String getTickColor() {
		return _tickColor;
	}
	
	/**
	 * Get the tick color of this scale in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getTickRGB() {
		return _tickRGB;
	}
	
	/**
	 * Setup the DailModel range.
	 * @param lower the lower bound in the scale.
	 * @param upper the upper bound in the scale.
	 * @param color the color in #RRGGBB format (hexdecimal); default to blue.
	 * @param innerRadius the inner radius percentage(0 ~ 1) of the range; default to 0.7.
	 * @param outerRadius the outer radius percentage(0 ~ 1) of the range; default to 0.8;
	 */
	public DialModelRange newRange(double lower, double upper, String color, double innerRadius, double outerRadius) {
		final DialModelRange range = new DialModelRange(this);
		range.setRange(lower, upper, color, innerRadius, outerRadius);
		_ranges.add(range);
		fireEvent(ChartDataEvent.CHANGED);
		return range;
	}

	/** Returns the number of ranges associaed with this scale.
	 * 
	 * @return the number of ranges associaed with this scale.
	 */
	public int rangeSize() {
		return _ranges.size();
	}
	
	/** Returns the color range of the specified index.
	 * 
	 * @param index the spcified index.
	 * @return the color range of the specified index.
	 */
	public DialModelRange getRange(int index) {
		return (DialModelRange) _ranges.get(index);
	}
	
	/**
	 * Removes the specified range from this scale.
	 * @param range the range to be removed.
	 */
	public void removeRange(DialModelRange range) {
		_ranges.remove(range);
		fireEvent(ChartDataEvent.CHANGED);
	}

	/**
	 * Returns the needle type of this scale ("pointer" or "pin")
	 * @return the needle type of this scale ("pointer" or "pin")
	 */
	public String getNeedleType() {
		return _needleType;
	}
	
	/**
	 * Sets the needle type of this scale ("pointer" or "pin")
	 * @param type the needle type of this scale ("pointer" or "pin")
	 */
	public void setNeedleType(String type) {
		if (_needleType != type) {
			_needleType = type;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Set the needle color.
	 * @param color in #RRGGBB format (hexdecimal).
	 */
	public void setNeedleColor(String color) {
		if (Objects.equals(color, _needleColor)) {
			return;
		}
		_needleColor = color;
		if (_needleColor == null) {
			_needleRGB = null;
		} else {
			_needleRGB = new int[3];
			Chart.decode(_needleColor, _needleRGB);
		}
		fireEvent(ChartDataEvent.CHANGED);
	}

	/**
	 * Get the needle color of this scale(in string as #RRGGBB).
	 * null means default.
	 */
	public String getNeedleColor() {
		return _needleColor;
	}
	
	/**
	 * Get the needle color of this scale in int array (0: red, 1: green, 2:blue).
	 * null means default.
	 */
	public int[] getNeedleRGB() {
		return _needleRGB;
	}
	
	/**
	 * Sets the radius percentage(0 ~ 1) of the scale's needle; default to 0.9.
	 * @param radius the radius percentage(0 ~ 1) of the scale's needle; default to 0.9.
	 */
	public void setNeedleRadius(double radius) {
		if (_needleRadius != radius) {
			_needleRadius = radius;
			fireEvent(ChartDataEvent.CHANGED);
		}
	}
	
	/**
	 * Return the radius percentage(0 ~ 1) of the scale's needle; default to 0.9.
	 * @return the radius percentage(0 ~ 1) of the scale's needle; default to 0.9.
	 */
	public double getNeedleRadius() {
		return _needleRadius;
	}
	/**
	 * Utility method to delegate event to {@link DialModel}
	 * @param evt the {@link ChartDataEvent}.
	 */
	/*package*/ void fireEvent(int evt) {
		if (_model != null)
			_model.fireEvent(evt, null, null);
	}
}
