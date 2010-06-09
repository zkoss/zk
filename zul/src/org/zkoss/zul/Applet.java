/* Applet.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 17:32:48 TST 2008, Created by davidchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

package org.zkoss.zul;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zk.au.out.AuInvoke;

/**
 * A generic applet component.
 * <p>See also <a href="http://www.w3schools.com/TAGS/tag_applet.asp">HTML applet tag</a>.
 * 
 * <p>
 * Non XUL extension.
 * <p>Note: {@link #setVisible(boolean)} with false cannot work in IE. (Browser's limitation) 
 * @author Davidchen
 * @author Tomyeh
 * @since 3.6.0
 */
public class Applet extends HtmlBasedComponent implements DynamicPropertied,
org.zkoss.zul.api.Applet {
	private String _code, _codebase, _archive,
		_align, _hspace, _vspace;
	private final Map _params = new LinkedHashMap();
	private boolean _mayscript;

	/** Return the applet class to run.
	 * Example: MyApplet.
	 */
	public String getCode() {
		return _code;
	}
	/** Sets the applet class to run.
	 */
	public void setCode(String code) {
		if (!Objects.equals(_code, code)) {
			_code = code;
			invalidate();
		}
	}
	
	/** Returns a relative base URL for applets specified in {@link #setCode} (URL).
	 * <p>Default: null (no codebase at all).
	 * @since 3.6.2
	 */
	public String getCodebase() {
		return _codebase;
	}
	/** Sets a relative base URL for applets specified in {@link #setCode} (URL).
	 * <p>Notice that, if URI is specified, it will be encoded ({@link org.zkoss.zk.ui.Execution#encodeURL}).
	 * @since 3.6.2
	 */
	public void setCodebase(String codebase) {
		if (!Objects.equals(_codebase, codebase)) {
			_codebase = codebase;
			invalidate();
		}
	}
	/** Returns whether the applet is allowed to access the scripting object.
	 * <p>Default: false.
	 * <p>It is only necessary for the applet to control the page script objects.
	 * It is not necessary for the page objects to control the applet.
	 * @since 5.0.3
	 */
	public boolean isMayscript() {
		return _mayscript;
	}
	/** Sets whether the applet is allowed to access the scripting object.
	 * @since 5.0.3
	 */
	public void setMayscript(boolean mayscript) {
		if (_mayscript != mayscript) {
			_mayscript = mayscript;
			invalidate();
		}
	}

	/** Returns the location of an archive file (URL).
	 * <p>Default: null (no archive at all)
	 * @since 5.0.3
	 */
	public String getArchive() {
		return _archive;
	}
	/** Sets the location of an archive file (URL).
	 * <p>Notice that, if URI is specified, it will be encoded ({@link org.zkoss.zk.ui.Execution#encodeURL}).
	 * @since 5.0.3
	 */
	public void setArchive(String  archive) {
		if (!Objects.equals(_archive, archive)) {
			_archive = archive;
			invalidate();
		}
	}

	/** Returns the alignment of an applet according to surrounding elements.
	 * <p>Default: null (browser default)
	 * @since 5.0.3
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the alignment of an applet according to surrounding elements.
	 * @since 5.0.3
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			invalidate();
		}
	}

	/** Returns the horizontal spacing around an applet.
	 * <p>Default: null (browser default)
	 * @since 5.0.3
	 */
	public String getHspace() {
		return _hspace;
	}
	/** Sets the horizontal spacing around an applet.
	 * @since 5.0.3
	 */
	public void setHspace(String hspace) {
		if (!Objects.equals(_hspace, hspace)) {
			_hspace = hspace;
			invalidate();
		}
	}
	/** Returns the vertical spacing around an applet.
	 * <p>Default: null (browser default)
	 * @since 5.0.3
	 */
	public String getVspace() {
		return _vspace;
	}
	/** Sets the vertical spacing around an applet.
	 * @since 5.0.3
	 */
	public void setVspace(String vspace) {
		if (!Objects.equals(_vspace, vspace)) {
			_vspace = vspace;
			invalidate();
		}
	}

	/** Sets a map of parameters (all existent parameters are removed first).
	 */
	public void setParams(Map params) {
		_params.clear();
		if (params != null)
			_params.putAll(params);
		invalidate();
	}
	/** Returns a map of parameters (never null).
	 */
	public Map getParams() {
		return _params;
	}
	/** Sets a parameter.
	 * If the value is null, the parameter is removed.
	 */
	public String setParam(String name, String value) {
		return value != null ? (String)_params.put(name, value):
			(String)_params.remove(name);
	}

	public Object getDynamicProperty(String name) {
		return _params.get(name);
	}

	public boolean hasDynamicProperty(String name) {
		return _params.containsKey(name);
	}

	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		setParam(name, Objects.toString(value));
	}

	/** Invokes the function of the applet running at the client.
	 */
	public void invoke(String function) {
		response(new AuInvoke(this, "invoke", function));
	}
	/** Invokes the function of the applet running at the client with
	 * one argument.
	 */
	public void invoke(String function, String argument) {
		response(new AuInvoke(this, "invoke", function, argument));
	}
	/** Invokes the function of the applet running at the client with
	 * variable number argument.
	 */
	public void invoke(String function, String[] arguments) {
		final int len = arguments != null ? arguments.length: 0;
		final String[] args = new String[len + 1];
		args[0] = function;
		for (int j = 0; j < len; ++j)
			args[j + 1] = arguments[j];
		response(new AuInvoke(this, "invoke", args));
	}

	/** Sets the value of the specified filed.
	 */
	public void setField(String field, String value) {
		response(new AuInvoke(this, "setField", field, value));
	}

	//super//
	/** No child is allowed.
	 */
	public boolean isChildable() {
		return false;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "code", getCode());
		render(renderer, "codebase", getEncodedCodebase());
		render(renderer, "archive", getEncodedArchive());
		render(renderer, "align", getAlign());
		render(renderer, "hspace", getHspace());
		render(renderer, "vspace", getVspace());
		if (isMayscript())
			renderer.render("mayscript", true);

		for (Iterator it = _params.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			render(renderer, "param",
				new String[] {(String)me.getKey(), (String)me.getValue()});
		}
	}

	private String getEncodedArchive() {
		final Desktop dt = getDesktop();
		return _archive != null && dt != null ? dt.getExecution().encodeURL(_archive): null;
			//if desktop is null, it doesn't belong to any execution
	}
	private String getEncodedCodebase() {
		final Desktop dt = getDesktop();
		return _codebase != null && dt != null ? dt.getExecution().encodeURL(_codebase): null;
			//if desktop is null, it doesn't belong to any execution
	}
}
