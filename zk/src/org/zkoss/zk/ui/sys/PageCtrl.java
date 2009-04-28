/* PageCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 13:55:09     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Collection;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ZScript;

/**
 * Addition interface to {@link org.zkoss.zk.ui.Page} for implementation
 * purpose.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author tomyeh
 */
public interface PageCtrl {
	/** The execution attribute used to control {@link #redraw} to use
	 * include instead of forward to redraw the page
	 */
	public static final String ATTR_REDRAW_BY_INCLUDE = "org.zkoss.zk.ui.redrawByInclude";

	/** Pre-initializes this page.
	 * It initializes {@link org.zkoss.zk.ui.Page#getDesktop},
	 * but it doesn't add this page to the desktop yet
	 * (which is done by {@link #init}).
	 *
	 * <p>Note: it is called before 
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit} and {@link #init}.
	 * Since {@link org.zkoss.zk.ui.Page#getDesktop} is initialized in this
	 * method, it is OK to create components in 
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit}.
	 *
	 * @since 3.5.2
	 */
	public void preInit();
	/** Initializes this page by assigning the info provided by
	 * the specified {@link PageConfig}, and then adds it
	 * to a desktop (by use of {@link Execution#getDesktop}).
	 *
	 * <p>Note: this method is called after {@link #preInit} and
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit}.
	 *
	 * <p>This method shall be called only after the current execution
	 * is activated.
	 *
	 * @param config the info about how to initialize this page
	 * @since 3.0.0
	 */
	public void init(PageConfig config);
	/** Called when this page is about to be detroyed.
	 * It is called by desktop, after removing it from the desktop.
	 */
	public void destroy();

	/** Returns the content of the specified condition
	 * that shall be generated inside the header element
	 * (never null).
	 *
	 * <p>For HTML, the header element is the HEAD element.
	 *
	 * @param before whether to return the headers that shall be shown
	 * before ZK's CSS/JS headers.
	 * If true, only the headers that shall be shown before (such as meta)
	 * are returned.
	 * If true, only the headers that shall be shown after (such as link)
	 * are returned.
	 * @see #getHeaders()
	 * @since 3.6.1
	 */
	public String getHeaders(boolean before);
	/** Returns all content that will be generated inside the header element
	 * (never null).
	 * <p>For HTML, the header element is the HEAD element.
	 * <p>It returns all header no matter it shall be shown before or
	 * after ZK's CSS/JS headers. To have more control, use
	 * {@link #getHeaders(boolean)} instead.
	 *
	 * @see #getHeaders(boolean)
	 */
	public String getHeaders();
	/** Returns the attributes of the root element declared in this page
	 * (never null).
	 * An empty string is returned if no special attribute is declared.
	 *
	 * <p>For HTML, the root element is the HTML element.
	 * @since 3.0.0
	 */
	public String getRootAttributes();
	/** Set the attributes of the root element declared in this page
	 *
	 * <p>Default: "".
	 */
	public void setRootAttributes(String rootAttributes);
	/** Returns the doc type (&lt;!DOCTYPE&gt;),
	 * or null to use the device default.
	 *
	 * @since 3.0.0
	 */
	public String getDocType();
	/** Sets the doc type (&lt;!DOCTYPE&gt;).
	 *
	 * <p>Default: null (i.e., the device default)
	 * @since 3.0.0
	 */
	public void setDocType(String docType);
	/** Returns the first line to be generated to the output,
	 * or null if nothing to generate.
	 *
	 * <p>For XML devices, it is usually the xml processing instruction:<br/>
	 * <code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
	 *
	 * @since 3.0.0
	 */
	public String getFirstLine();
	/** Sets the first line to be generated to the output.
	 *
	 * <p>Default: null (i.e., nothing generated)
	 * @since 3.0.0
	 */
	public void setFirstLine(String firstLine);

	/** Returns the content type, or null to use the device default.
	 *
	 * @since 3.0.0
	 */
	public String getContentType();
	/** Sets the content type.
	 *
	 * @since 3.0.0
	 */
	public void setContentType(String contentType);
	/** Returns if the client can cache the rendered result, or null
	 * to use the device default.
	 *
	 * @since 3.0.0
	 */
	public Boolean getCacheable();
	/** Sets if the client can cache the rendered result.
	 *
	 * <p>Default: null (use the device default).
	 * @since 3.0.0
	 */
	public void setCacheable(Boolean cacheable);

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
	 * <p>You could use {@link #ATTR_REDRAW_BY_INCLUDE} to control
	 * whether to include, instead of forward, the page content.
	 * By default, {@link Execution#forward } is used if possible.
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
	 * @see Component#setPage
	 */
	public void addRoot(Component comp);
	/** Detaches a root component from this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicitly
	 * @see Component#setPage
	 */
	public void removeRoot(Component comp);
	/** Moves a root component before the reference component.
	 *
	 * <p>Note: it assumes removeRoot was called before for comp.
	 * Otherwise, nothing happens.
	 *
	 * <p>It is used internally and developers shall not invoke it
	 * explicitly
	 *
	 * @since 3.0.0
	 * @see Component#setPageBefore
	 */
	public void moveRoot(Component comp, Component refRoot);

	/** Adds a fellow. */
	public void addFellow(Component comp);
	/** Removes a fellow. */
	public void removeFellow(Component comp);
	/** Returns whether a fellow exists with the specified component ID.
	 */
	public boolean hasFellow(String compId);

	/** Adds a deferred zscript.
	 *
	 * @param parent the component that is the parent of zscript (in
	 * the ZUML page), or null if it belongs to the page.
	 * @param zscript the zscript that shall be evaluated as late as
	 * when the interpreter of the same language is being loaded.
	 */
	public void addDeferredZScript(Component parent, ZScript zscript);
 	/** Returns the default parent, or null if no such parent.
 	 * If a default parent is defined (by use of {@link #setDefaultParent}),
 	 * {@link org.zkoss.zk.ui.Executions#createComponents(String, Component, java.util.Map)} will
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
