/** DialChartDataEvent.java.

	Purpose:
		
	Description:
		
	History:
		11:19:20 AM Jan 23, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.event;

import org.zkoss.zul.ChartModel;

/**
 * A dial chart data event to handle the property changed.
 * @author jumperchen
 * @since 7.0.1
 */
public class DialChartDataEvent extends ChartDataEvent {
	private String _prop;

	// DialModel
	public static String SCALE = "scale";
	public static String FRAME_BG_ALPHA = "frameBgAlpha";
	public static String FRAME_BG_COLOR = "frameBgColor";
	public static String FRAME_BG_COLOR_1 = "frameBgColor1";
	public static String FRAME_BG_COLOR_2 = "frameBgColor2";
	public static String FRAME_FG_COLOR = "frameFgColor";
	public static String GRADIENT_DIRECTION = "gradientDirection";
	public static String CAP_RADIUS = "capRadius";
	
	// DialModelRange
	public static String RANGE_COLOR = "rangeColor";
	public static String INNER_RADIUS = "innerRadius";
	public static String OUTER_RADIUS = "outerRadius";
	public static String LOWER_BOUND = "lowerBound";
	public static String UPPER_BOUND = "upperBound";
	
	// DialModelScale
	public static String SCALE_VALUE = "scaleValue";
	public static String SCALE_TEXT = "scaleText";
	public static String SCALE_FONT = "scaleFont";
	public static String TEXT_RADIUS = "textRadius";
	public static String VALUE_FONT = "valueFont";
	public static String VALUE_RADIUS = "valueRadius";
	public static String VALUE_ANGLE = "valueAngle";
	public static String TICK_FONT = "tickFont";
	public static String TICK_LABEL_OFFSET = "tickLabelOffset";
	public static String TICK_COLOR = "tickColor";
	public static String TICK_RADIUS = "tickRadius";
	public static String RANGE = "range";
	public static String NEEDLE_TYPE = "needleType";
	public static String NEEDLE_COLOR = "needleColor";
	public static String NEEDLE_RADIUS = "needleRadius";
	
	public DialChartDataEvent(ChartModel model, int type, String prop, Object data) {
		super(model, type, null, null, -1, -1, data);
		_prop = prop;
	}
	/**
	 * Return the key of the property.
	 */
	public String getPropertyKey() {
		return _prop;
	}
}
