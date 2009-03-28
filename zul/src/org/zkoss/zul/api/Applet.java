/* Applet.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 18 12:18:54     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul.api;

import java.util.Map;

/**
 * A generic applet component.
 *
 * @author tomyeh
 * @since 3.6.0
 */
public interface Applet extends org.zkoss.zul.impl.api.XulElement {
	/** Return the code of the applet, i.e., the URI of the Java class.
	 */
	public String getCode();
	/** Sets the code of the applet, i.e., the URI of the Java class.
	 */
	public void setCode(String code);

	/** Sets a map of parameters (all existent parameters are removed first).
	 */
	public void setParams(Map params);
	/** Returns a map of parameters (never null).
	 */
	public Map getParams();
	/** Sets a parameter.
	 * If the value is null, the parameter is removed.
	 */
	public String setParam(String name, String value);

	/** Invokes the function of the applet running at the client.
	 */
	public void invoke(String function);
	/** Invokes the function of the applet running at the client with
	 * one argument.
	 */
	public void invoke(String function, String argument);
	/** Invokes the function of the applet running at the client with
	 * variable number argument.
	 */
	public void invoke(String function, String[] arguments);

	/** Sets the value of the specified filed.
	 */
	public void setField(String field, String value);
}
