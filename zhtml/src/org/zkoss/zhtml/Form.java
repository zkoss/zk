/* Form.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:52:28     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The FORM tag.
 * 
 * @author tomyeh
 */
public class Form extends AbstractTag {
	public Form() {
		super("form");
	}
	/**
	 * Returns the accept-charset of this form tag.
	 * @since 8.0.3
	 */
	public String getAcceptcharset() {
		return (String) getDynamicProperty("accept-charset");
	}

	/**
	 * Sets the accept-charset of this form tag.
	 * @since 8.0.3
	 */
	public void setAcceptcharset(String acceptcharset) throws WrongValueException {
		setDynamicProperty("accept-charset", acceptcharset);
	};
	/**
	 * Returns the action of this form tag.
	 * @since 8.0.3
	 */
	public String getAction() {
		return (String) getDynamicProperty("action");
	}

	/**
	 * Sets the action of this form tag.
	 * @since 8.0.3
	 */
	public void setAction(String action) throws WrongValueException {
		setDynamicProperty("action", action);
	};
	/**
	 * Returns the autocomplete of this form tag.
	 * @since 8.0.3
	 */
	public String getAutocomplete() {
		return (String) getDynamicProperty("autocomplete");
	}

	/**
	 * Sets the autocomplete of this form tag.
	 * @since 8.0.3
	 */
	public void setAutocomplete(String autocomplete) throws WrongValueException {
		setDynamicProperty("autocomplete", autocomplete);
	};
	/**
	 * Returns the enctype of this form tag.
	 * @since 8.0.3
	 */
	public String getEnctype() {
		return (String) getDynamicProperty("enctype");
	}

	/**
	 * Sets the enctype of this form tag.
	 * @since 8.0.3
	 */
	public void setEnctype(String enctype) throws WrongValueException {
		setDynamicProperty("enctype", enctype);
	};
	/**
	 * Returns the method of this form tag.
	 * @since 8.0.3
	 */
	public String getMethod() {
		return (String) getDynamicProperty("method");
	}

	/**
	 * Sets the method of this form tag.
	 * @since 8.0.3
	 */
	public void setMethod(String method) throws WrongValueException {
		setDynamicProperty("method", method);
	};
	/**
	 * Returns the name of this form tag.
	 * @since 8.0.3
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this form tag.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	};
	/**
	 * Returns the novalidate of this form tag.
	 * @since 8.0.3
	 */
	public String getNovalidate() {
		return (String) getDynamicProperty("novalidate");
	}

	/**
	 * Sets the novalidate of this form tag.
	 * @since 8.0.3
	 */
	public void setNovalidate(String novalidate) throws WrongValueException {
		setDynamicProperty("novalidate", novalidate);
	};
	/**
	 * Returns the target of this form tag.
	 * @since 8.0.3
	 */
	public String getTarget() {
		return (String) getDynamicProperty("target");
	}

	/**
	 * Sets the target of this form tag.
	 * @since 8.0.3
	 */
	public void setTarget(String target) throws WrongValueException {
		setDynamicProperty("target", target);
	};
}
