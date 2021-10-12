/* Output.java

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
 * The OUTPUT tag.
 * 
 * @author jameschu
 */
public class Output extends AbstractTag {
	public Output() {
		super("output");
	}

	/**
	 * Returns the for of this output tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getFor() {
		return (String) getDynamicProperty("for");
	}

	/**
	 * Sets the for of this output tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setFor(String propFor) throws WrongValueException {
		setDynamicProperty("for", propFor);
	}
	/**
	 * Returns the form of this output tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getForm() {
		return (String) getDynamicProperty("form");
	}

	/**
	 * Sets the form of this output tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setForm(String form) throws WrongValueException {
		setDynamicProperty("form", form);
	}
	/**
	 * Returns the name of this output tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this output tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
}
