/* Applet.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 18 12:18:54     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul.api;

import java.util.Map;

/**
 * A generic applet component.
 * <p>See also <a href="http://www.w3schools.com/TAGS/tag_applet.asp">HTML applet tag</a>.
 *
 * @author tomyeh
 * @since 3.6.0
 */
public interface Applet extends org.zkoss.zk.ui.api.HtmlBasedComponent {
	/** Return the applet class to run.
	 * Example: MyApplet.
	 * <p>Default: null.
	 */
	public String getCode();
	/** Sets the applet class to run.
	 */
	public void setCode(String code);

	/** Returns a relative base URL for applets specified in {@link #setCode} (URL).
	 * <p>Default: null (no codebase at all).
	 * <p>Notice that, if URI is specified, it will be encoded ({@link org.zkoss.zk.ui.Execution#encodeURL}).
	 * @since 5.0.3
	 */
	public String getCodebase();
	/** Sets a relative base URL for applets specified in {@link #setCode} (URL).
	 * @since 5.0.3
	 */
	public void setCodebase(String codebase);

	/** Returns whether the applet is allowed to access the scripting object.
	 * <p>Default: false.
	 * <p>It is only necessary for the applet to control the page script objects.
	 * It is not necessary for the page objects to control the applet.
	 * @since 5.0.3
	 */
	public boolean isMayscript();
	/** Sets whether the applet is allowed to access the scripting object.
	 * @since 5.0.3
	 */
	public void setMayscript(boolean may);

	/** Returns the location of an archive file (URL).
	 * <p>Default: null (no archive at all)
	 * @since 5.0.3
	 */
	public String getArchive();
	/** Sets the location of an archive file (URL).
	 * <p>Notice that, if URI is specified, it will be encoded ({@link org.zkoss.zk.ui.Execution#encodeURL}).
	 * @since 5.0.3
	 */
	public void setArchive(String  archive);

	/** Returns the alignment of an applet according to surrounding elements.
	 * <p>Default: null (browser default)
	 * @since 5.0.3
	 */
	public String getAlign();
	/** Sets the alignment of an applet according to surrounding elements.
	 * @since 5.0.3
	 */
	public void setAlign(String align);

	/** Returns the horizontal spacing around an applet.
	 * <p>Default: null (browser default)
	 * @since 5.0.3
	 */
	public String getHspace();
	/** Sets the horizontal spacing around an applet.
	 * @since 5.0.3
	 */
	public void setHspace(String hspace);
	/** Returns the vertical spacing around an applet.
	 * <p>Default: null (browser default)
	 * @since 5.0.3
	 */
	public String getVspace();
	/** Sets the vertical spacing around an applet.
	 * @since 5.0.3
	 */
	public void setVspace(String vspace);

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
