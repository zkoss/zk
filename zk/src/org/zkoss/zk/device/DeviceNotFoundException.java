/* DeviceNotFoundException.java

	Purpose:
		
	Description:
		
	History:
		Tue May 15 14:34:50     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import org.zkoss.zk.ui.UiException;

/**
 * Denotes the interpreter of the requested device type is not found.
 * 
 * @author tomyeh
 * @since 2.4.0
 */
public class DeviceNotFoundException extends UiException {
	private final String _type;

	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, String msg, Throwable cause) {
		super(msg, cause);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, String s) {
		super(s);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, Throwable cause) {
		super(cause);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type) {
		_type = type;
	}

	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, int code, Object[] fmtArgs, Throwable cause) {
		super(code, fmtArgs, cause);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, int code, Object fmtArg, Throwable cause) {
		super(code, fmtArg, cause);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, int code, Object[] fmtArgs) {
		super(code, fmtArgs);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, int code, Object fmtArg) {
		super(code, fmtArg);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, int code, Throwable cause) {
		super(code, cause);
		_type = type;
	}
	/**
	 * @param type the device type.
	 */
	public DeviceNotFoundException(String type, int code) {
		super(code);
		_type = type;
	}

	/** Returns the device type that is not found.
	 */
	public String getDeviceType() {
		return _type;
	}
}
