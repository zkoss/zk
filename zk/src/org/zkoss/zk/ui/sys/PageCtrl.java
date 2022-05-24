/* PageCtrl.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 13:55:09     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ZScript;

/**
 * Addition interface to {@link Page} for implementation purpose.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author tomyeh
 */
public interface PageCtrl {
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

	/** Called when this page is about to be destroyed.
	 * It is called by desktop, after removing it from the desktop.
	 */
	public void destroy();

	/** Returns the tags that shall be generated inside the head element
	 * and before ZK's default tags (never null).
	 * For example, it might consist of &lt;meta&gt; and &lt;link&gt;.
	 *
	 * <p>Since it is generated before ZK's default tags (such as CSS and JS),
	 * it cannot override ZK's default behaviors.
	 *
	 * @see #getAfterHeadTags
	 * @since 5.0.5
	 */
	public String getBeforeHeadTags();

	/** Returns the tags that shall be generated inside the head element
	 * and after ZK's default tags (never null).
	 * For example, it might consist of &lt;meta&gt; and &lt;link&gt;.
	 *
	 * <p>Since it is generated after ZK's default tags (such as CSS and JS),
	 * it could override ZK's default behaviors.
	 *
	 * @see #getBeforeHeadTags
	 * @since 5.0.5
	 */
	public String getAfterHeadTags();

	/** Adds the tags that will be generated inside the head element
	 * and before ZK's default tags. For example,
	 * <pre>{@code ((PageCtrl)page).addBeforeHeadTags("<meta name=\"robots\" content=\"index,follow\"/>");}</pre>
	 *
	 * <p>You could specify the link, meta and script directive to have the similar
	 * result.
	 * @since 5.0.5
	 */
	public void addBeforeHeadTags(String tags);

	/** Adds the tags that will be generated inside the head element
	 * and after ZK's default tags. For example,
	 * <pre>{@code ((PageCtrl)page).addBeforeHeadTags("<meta name=\"robots\" content=\"index,follow\"/>");}</pre>
	 *
	 * <p>You could specify the link, meta and script directive to have the similar
	 * result.
	 * @since 5.0.5
	 */
	public void addAfterHeadTags(String tags);

	/** Returns a readonly collection of response headers (never null).
	 * The entry is a three-element object array.
	 * The first element is the header name.
	 * The second element of the array is the value which is an instance of
	 * {@link java.util.Date} or {@link String} (and never null).
	 * The third element indicates whether to add (rather than set)
	 * theader. It is an instance of Boolean (and never null).
	 * @since 5.0.2
	 */
	public Collection<Object[]> getResponseHeaders();

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

	/** Returns the widget class of this page, or null to use the device default.
	 *
	 * @since 5.0.5
	 */
	public String getWidgetClass();

	/** Sets the widget class of this page.
	 *
	 * @param wgtcls the widget class. The device default is assumed if wgtcls
	 * is null or empty.
	 * @since 5.0.5
	 */
	public void setWidgetClass(String wgtcls);

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

	/** Returns whether to automatically redirect to the timeout URI.
	 *
	 * @see #setAutomaticTimeout
	 * @since 3.6.3
	 */
	public Boolean getAutomaticTimeout();

	/** Sets whether to automatically redirect to the timeout URI.
	 *
	 * <p>Default: null (use the device default).
	 * <p>If it is set to false, it means this page is redirected to the timeout URI
	 * when the use takes some action after timeout. In other words,
	 * nothing happens if the user does nothing.
	 * If it is set to true, it is redirected as soon as timeout,
	 * no matter the user takes any action.
	 *
	 * <p>Refer to {@link org.zkoss.zk.ui.util.Configuration#setAutomaticTimeout}
	 * for how to configure the device default (default: false).
	 * @since 3.6.3
	 */
	public void setAutomaticTimeout(Boolean autoTimeout);

	/** Returns the owner of this page, or null if it is not owned by
	 * any component.
	 * A page is included by a component. We say it is owned by the component.
	 * <p>Note: the owner, if not null, must implement {@link org.zkoss.zk.ui.ext.Includer}.
	 */
	public Component getOwner();

	/** Sets the owner of this page.
	 * <p>Called only internally
	 * <p>Since 5.0.6, the owner must implement {@link org.zkoss.zk.ui.ext.Includer}.
	 */
	public void setOwner(Component comp);

	/** Redraws the whole page into the specified output.
	 *
	 * <p>You could use {@link org.zkoss.zk.ui.sys.Attributes#PAGE_REDRAW_CONTROL}
	 * and/or {@link org.zkoss.zk.ui.sys.Attributes#PAGE_RENDERER}
	 * to control how to render manually.
	 *
	 * @since 5.0.0
	 */
	public void redraw(Writer out) throws IOException;

	/** Adds a deferred zscript.
	 *
	 * @param parent the component that is the parent of zscript (in
	 * the ZUML page), or null if it belongs to the page.
	 * @param zscript the zscript that shall be evaluated as late as
	 * when the interpreter of the same language is being loaded.
	 */
	public void addDeferredZScript(Component parent, ZScript zscript);

	/** Notification that the session, which owns this page,
	 * is about to be passivated (a.k.a., serialized).
	 */
	public void sessionWillPassivate(Desktop desktop);

	/** Notification that the session, which owns this page,
	 * has just been activated (a.k.a., deserialized).
	 */
	public void sessionDidActivate(Desktop desktop);
}
