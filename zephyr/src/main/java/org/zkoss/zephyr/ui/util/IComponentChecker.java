/* IComponentChecker.java

	Purpose:

	Description:

	History:
		Thu Oct 21 17:19:23 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import java.util.Arrays;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/**
 * An IComponent check utils.
 * @author katherine
 */
public class IComponentChecker {
	public static void checkMode(String mode, String... availableMolds) {
		if (!Arrays.asList(availableMolds).contains(mode))
			throw new UiException("Unknown mode: " + mode);
	}
	public static void checkOrient(String orient) {
		if(!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);
	}
	public static void checkOrient(String orient, String... availableOrient) {
		if (!Arrays.asList(availableOrient).contains(orient))
			throw new WrongValueException("Unknown orient : "  + orient);
	}
	public static void checkAlign(String align, String... availableAlign) {
		if (!Arrays.asList(availableAlign).contains(align))
			throw new WrongValueException("Unknown align : "  + align);
	}
	public static void checkWidth(String width) {
		if (width != null) {
			throw new UiException("Not allowed to set width.");
		}
	}
	public static void checkHflex(String hflex) {
		if (hflex != null) {
			throw new UiException("Not allowed to set hflex.");
		}
	}
	public static void checkValue(int value) {
		if (value < 0)
			throw new WrongValueException("cannot be negative");
	}
	public static void checkValue(double value) {
		if (value < 0)
			throw new WrongValueException("cannot be negative");
	}
	public static void checkValue(String value, String log, String... availableValue) {
		if (!Arrays.asList(availableValue).contains(value))
			throw new WrongValueException(log);
	}
}
