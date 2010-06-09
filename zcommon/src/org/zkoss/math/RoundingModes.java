/* RoundingModes.java

	Purpose:
		
	Description:
		
	History:
		Fri May  4 12:24:48     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.math;

import java.math.BigDecimal;

/**
 * Utilities to handle the rounding mode.
 * @author tomyeh
 */
public class RoundingModes {
	private RoundingModes() {}

	/** Returns the name of the rounding mode.
	 * Possible values include 
<dl>
<dt>CEILING</dt>
	<dd>Rounding mode to round towards positive infinity.</dd>
<dt>DOWN</dt>
	<dd>Rounding mode to round towards zero.</dd>
<dt>FLOOR</dt>
	<dd>Rounding mode to round towards negative infinity.</dd>
<dt>HALF_DOWN</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round down.</dd>
<dt>HALF_EVEN</dt>
	<dd>Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant, in which case, round towards the even neighbor.</dd>
<dt>HALF_UP</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round up.</dd>
<dt>UNNECESSARY</dt>
	<dd>Rounding mode to assert that the requested operation has an exact result, hence no rounding is necessary.</dd>
<dt>UP</dt>
	<dd>Rounding mode to round away from zero.</dd>
</dl>
	 */
	public static final String toString(int roundingMode) {
		switch (roundingMode) {
		case BigDecimal.ROUND_CEILING: return "CEILING";
		case BigDecimal.ROUND_DOWN: return "DOWN";
		case BigDecimal.ROUND_FLOOR: return "FLOOR";
		case BigDecimal.ROUND_HALF_DOWN: return "HALF_DOWN";
		case BigDecimal.ROUND_HALF_EVEN: return "HALF_EVEN";
		case BigDecimal.ROUND_HALF_UP: return "HALF_UP";
		case BigDecimal.ROUND_UNNECESSARY: return "UNNECESSARY";
		case BigDecimal.ROUND_UP: return "UP";
		default:
			throw new IllegalArgumentException("Unknown rounding mode: "+roundingMode);
		}
	}
	/** Returns the rounding mode of the specified name.
	 *
	 * @param name the rounding mode's name. Allowed values include:
<dl>
<dt>CEILING</dt>
	<dd>Rounding mode to round towards positive infinity.</dd>
<dt>DOWN</dt>
	<dd>Rounding mode to round towards zero.</dd>
<dt>FLOOR</dt>
	<dd>Rounding mode to round towards negative infinity.</dd>
<dt>HALF_DOWN</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round down.</dd>
<dt>HALF_EVEN</dt>
	<dd>Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant, in which case, round towards the even neighbor.</dd>
<dt>HALF_UP</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round up.</dd>
<dt>UNNECESSARY</dt>
	<dd>Rounding mode to assert that the requested operation has an exact result, hence no rounding is necessary.</dd>
<dt>UP</dt>
	<dd>Rounding mode to round away from zero.</dd>
</dl>
	 */
	public static final int valueOf(String name) {
		name = name.toUpperCase();
		if ("CEILING".equals(name)) return BigDecimal.ROUND_CEILING;
		if ("DOWN".equals(name)) return BigDecimal.ROUND_DOWN;
		if ("FLOOR".equals(name)) return BigDecimal.ROUND_FLOOR;
		if ("HALF_DOWN".equals(name)) return BigDecimal.ROUND_HALF_DOWN;
		if ("HALF_EVEN".equals(name)) return BigDecimal.ROUND_HALF_EVEN;
		if ("HALF_UP".equals(name)) return BigDecimal.ROUND_HALF_UP;
		if ("UNNECESSARY".equals(name)) return BigDecimal.ROUND_UNNECESSARY;
		if ("UP".equals(name)) return BigDecimal.ROUND_UP;
		throw new IllegalArgumentException("Unknown rounding mode: "+name);
	}
}
