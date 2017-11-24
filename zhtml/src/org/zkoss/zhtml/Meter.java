/* Meter.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 14 10:19:42 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The METER tag.
 * 
 * @author jameschu
 */
public class Meter extends AbstractTag {
	public Meter() {
		super("meter");
	}

	/**
	 * Returns the value of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getValue() {
		return (Integer) getDynamicProperty("value");
	}

	/**
	 * Sets the value of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setValue(Integer value) throws WrongValueException {
		setDynamicProperty("value", value);
	}
	/**
	 * Returns the min of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getMin() {
		return (Integer) getDynamicProperty("min");
	}

	/**
	 * Sets the min of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setMin(Integer min) throws WrongValueException {
		setDynamicProperty("min", min);
	}
	/**
	 * Returns the max of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getMax() {
		return (Integer) getDynamicProperty("max");
	}

	/**
	 * Sets the max of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setMax(Integer max) throws WrongValueException {
		setDynamicProperty("max", max);
	}
	/**
	 * Returns the low of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getLow() {
		return (Integer) getDynamicProperty("low");
	}

	/**
	 * Sets the low of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setLow(Integer low) throws WrongValueException {
		setDynamicProperty("low", low);
	}
	/**
	 * Returns the high of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getHigh() {
		return (Integer) getDynamicProperty("high");
	}

	/**
	 * Sets the high of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setHigh(Integer high) throws WrongValueException {
		setDynamicProperty("high", high);
	}
	/**
	 * Returns the optimum of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getOptimum() {
		return (Integer) getDynamicProperty("optimum");
	}

	/**
	 * Sets the optimum of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setOptimum(Integer optimum) throws WrongValueException {
		setDynamicProperty("optimum", optimum);
	}
	/**
	 * Returns the volume of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public Integer getVolume() {
		return (Integer) getDynamicProperty("volume");
	}

	/**
	 * Sets the volume of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setVolume(Integer volume) throws WrongValueException {
		setDynamicProperty("volume", volume);
	}

	/**
	 * Returns the form of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getForm() {
		return (String) getDynamicProperty("form");
	}

	/**
	 * Sets the form of this meter tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setForm(String form) throws WrongValueException {
		setDynamicProperty("form", form);
	}
}
