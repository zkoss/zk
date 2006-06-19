/* PageCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 13:55:09     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import java.util.Collection;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.jsp.el.FunctionMapper;

import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;

/**
 * Addition interface to {@link com.potix.zk.ui.Page} for implementation
 * purpose.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface PageCtrl {
	/** Initializes a page by assigning an identifier, a title, and adding it
	 * to a desktop (by use of {@link Execution#getDesktop}).
	 *
	 * <p>This method shall be called only after the current execution
	 * is activated.
	 *
	 * @param id the page identifier, or null if auto-generation is required
	 * @param title the page title, or null if no title at all.
	 * @param style the CSS style, or null if no special style is required.
	 */
	public void init(String id, String title, String style);

	/** Returns the CSS style of this page, or empty if not specified.
	 */
	public String getStyle();

	/** Returns the owner of this page, or null if it is not owned by
	 * any component.
	 * A page is included by a component. We say it is owned by the component.
	 */
	public Component getOwner();
	/** Sets the owner of this page.
	 * <p>Used only internally.
	 */
	public void setOwner(Component comp);

	/** Redraws the whole page into the specified output.
	 *
	 * @param responses a list of responses that the page has to generate
	 * corresponding javascript to process them; or null if no such responses.
	 * The responses is not null, if and only if the page is creating
	 */
	public void redraw(Collection responses, Writer out) throws IOException;

	//-- Used for component implementation --//
	/** Adds a root component to a page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicityly.
	 */
	public void addRoot(Component comp);
	/** Detaches a root component from this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicitly
	 */
	public void removeRoot(Component comp);

	/** Adds a fellow. */
	public void addFellow(Component comp);
	/** Removes a fellow. */
	public void removeFellow(Component comp);
	/** Returns whether a fellow exists with the specified component ID.
	 */
	public boolean hasFellow(String compId);

 	/** Returns the default parent, or null if no such parent.
 	 * If a default parent is defined (by use of {@link #setDefaultParent}),
 	 * {@link com.potix.zk.ui.Executions#createComponents(String, Component, java.util.Map)} will
 	 * use it as the default parent, if developers didn't specify one.
 	 */
 	public Component getDefaultParent();
 	/** Sets the default parent.
 	 *
 	 * <p>It is rarely used by application developers. Rather, it is used
 	 * by ZHTML's body to make sure new created compnents are placed
 	 * correctly.
 	 *
 	 * <p>Caller has to ensure the comp is part of the page. Otherwise,
 	 * the result is unpreditable.
 	 *
 	 * @see #getDefaultParent
 	 */
 	public void setDefaultParent(Component comp);

	/** Notification that the session, which owns this page,
	 * is about to be passivated (aka., serialized).
	 */
	public void sessionWillPassivate(Desktop desktop);
	/** Notification that the session, which owns this page,
	 * has just been activated (aka., deserialized).
	 */
	public void sessionDidActivate(Desktop desktop);
}
