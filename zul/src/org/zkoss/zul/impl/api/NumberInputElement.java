/* NumberInputElement.java

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
package org.zkoss.zul.impl.api;

import java.math.BigDecimal; //for javadoc
import org.zkoss.math.RoundingModes;//for javadoc

/**
 * A skeletal implementation for number-type input box.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface NumberInputElement extends FormatInputElement {

	/**
	 * Sets the rounding mode. Note: You cannot change the rounding mode unless
	 * you are using Java 6 or later.
	 * 
	 * @param mode
	 *            the rounding mode. Allowed value:
	 *            {@link BigDecimal#ROUND_CEILING},
	 *            {@link BigDecimal#ROUND_DOWN}, {@link BigDecimal#ROUND_FLOOR},
	 *            {@link BigDecimal#ROUND_HALF_DOWN},
	 *            {@link BigDecimal#ROUND_HALF_UP},
	 *            {@link BigDecimal#ROUND_HALF_EVEN},
	 *            {@link BigDecimal#ROUND_UNNECESSARY} and
	 *            {@link BigDecimal#ROUND_UP}
	 * 
	 * @exception UnsupportedOperationException
	 *                if Java 5 or below
	 */
	public void setRoundingMode(int mode);

	/**
	 * Sets the rounding mode by the name. Note: You cannot change the rounding
	 * mode unless you are using Java 6 or later.
	 * 
	 * @param name
	 *            the rounding mode's name. Allowed value:
	 *            <dl>
	 *            <dt>CEILING</dt>
	 *            <dd>Rounding mode to round towards positive infinity.</dd>
	 *            <dt>DOWN</dt>
	 *            <dd>Rounding mode to round towards zero.</dd>
	 *            <dt>FLOOR</dt>
	 *            <dd>Rounding mode to round towards negative infinity.</dd>
	 *            <dt>HALF_DOWN</dt>
	 *            <dd>Rounding mode to round towards "nearest neighbor" unless
	 *            both neighbors are equidistant, in which case round down.</dd>
	 *            <dt>HALF_EVEN</dt>
	 *            <dd>Rounding mode to round towards the "nearest neighbor"
	 *            unless both neighbors are equidistant, in which case, round
	 *            towards the even neighbor.</dd>
	 *            <dt>HALF_UP</dt>
	 *            <dd>Rounding mode to round towards "nearest neighbor" unless
	 *            both neighbors are equidistant, in which case round up.</dd>
	 *            <dt>UNNECESSARY</dt>
	 *            <dd>Rounding mode to assert that the requested operation has
	 *            an exact result, hence no rounding is necessary.</dd>
	 *            <dt>UP</dt>
	 *            <dd>Rounding mode to round away from zero.</dd>
	 *            </dl>
	 * @exception UnsupportedOperationException
	 *                if Java 5 or below
	 * @see RoundingModes
	 */
	public void setRoundingMode(String name);

	/**
	 * Returns the rounding mode.
	 * <p>
	 * Default: {@link BigDecimal#ROUND_HALF_EVEN}.
	 */
	public int getRoundingMode();

}
