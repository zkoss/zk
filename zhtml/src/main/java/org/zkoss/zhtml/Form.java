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
	}
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
	}
	/**
	 * Returns the autocomplete of this form tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isAutocomplete() {
		return !"off".equals(getDynamicProperty("autocomplete"));
	}

	/**
	 * Sets the autocomplete of this form tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setAutocomplete(boolean autocomplete) throws WrongValueException {
		setDynamicProperty("autocomplete", autocomplete ? "on" : "off");
	}
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
	}
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
	}
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
	}
	/**
	 * Returns the novalidate of this form tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isNovalidate() {
		final Boolean b = (Boolean) getDynamicProperty("novalidate");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the novalidate of this form tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setNovalidate(boolean novalidate) throws WrongValueException {
		setDynamicProperty("novalidate", novalidate ? Boolean.valueOf(novalidate) : null);
	}
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
	}

	/**
	 * Returns the rel of this tag.
	 *
	 * @since 10.0.0
	 */
	public String getRel() {
		return (String) getDynamicProperty("rel");
	}

	/**
	 * Sets the rel of this tag.
	 * <p>Controls the annotations and what kinds of links the form creates.
	 * <p>Annotations include {@code external}, {@code nofollow}, {@code opener},
	 * {@code noopener}, and {@code noreferrer}
	 * @since 10.0.0
	 */
	public void setRel(String rel) throws WrongValueException {
		setDynamicProperty("rel", rel);
	}
}
