/* Remove.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 16:14:44     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.IOException;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;

/**
 * The remove action used to remove an attribute.
 * 
 * @author tomyeh
 */
public class Remove extends AbstractAction {
	private int _scope = ActionContext.PAGE_SCOPE;
	private String _var;

	/** Returns the scope. */
	public int getScope() {
		return _scope;
	}
	/** Sets the scope. */
	public void setScope(String scope) {
		_scope = toScope(scope);
	}
	/** Returns the attribute name. */
	public String getVar() {
		return _var;
	}
	/** Sets the attribute name. */
	public void setVar(String var) {
		_var = var;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (!isEffective())
			return;
		if (nested)
			throw new DspException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});
		if (_var == null)
			throw new DspException(MWeb.DSP_ATTRIBUTE_REQUIRED,
				new Object[] {this, "var", new Integer(ac.getLineNumber())});
		ac.removeAttribute(_var, _scope);
	}

	//-- Object --//
	public String toString() {
		return "remove";
	}
}
