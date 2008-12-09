/* Desktop.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec  9 16:27:21     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Map;
import java.util.Collection;

import org.zkoss.util.media.Media;

import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.device.Device;

/**
 * Represents a desktop.
 * All pages that is created from the same URL is called a desktop.
 *
 * <p>A desktop is created automatically when the first page is created
 * during a request.
 *
 * <p>To access a {@link Page}, its desktop must be locked first.
 * Once a desktop is locked to a request, all pages
 * contained in this desktop are free to access.
 * 
 * @author tomyeh
 */
public interface Desktop {
	/** Returns the device type that this desktop belongs to.
	 *
	 * <p>A device type identifies the type of a client. For example, "ajax"
	 * represents the Web browsers with Ajax support,
	 * while "mil" represents clients that supports
	 * <i>Mobile Interactive markup Language</i> (on Limited Connected Device,
	 * such as mobile phones).
	 *
	 * <p>A desktop can use the languages belonging to the same device type.
	 * See also {@link org.zkoss.zk.ui.metainfo.LanguageDefinition#getDeviceType}.
	 *
	 * <p>A component can be added to a desktop only if they belong to the same
	 * device type.
	 *
	 * <p>Default: depends on the extension of the resource path,
	 * "ajax" if the path or extension not available.
	 * If {@link Richlet} is used, its language definition's device type is
	 * assumed.
	 */
	public String getDeviceType();
	/** Sets the device type that this desktop belongs to.
	 *
	 * <p>Note: you can change the device type only before any component
	 * is attached to a page of the desktop.
	 * In other words, you can set the device type only at the initialization
	 * stage.
	 *
	 * <p>If the device type is changed, any device allocated for this desktop
	 * ({@link #getDevice}) will be dropped and recreated at the next
	 * invocation to {@link #getDevice}.
	 *
	 * @exception UiException if any component is attached to a page of the desktop.
	 */
	public void setDeviceType(String deviceType);
	/** Returns the device that is associated with this desktop.
	 * <p>Note: the device is shared by all desktops of the same device type.
	 */
	public Device getDevice();

	/** Returns whether the desktop is still alive.
	 * It returns false once it is destroyed.
	 * @see org.zkoss.zk.ui.sys.DesktopCtrl#destroy
	 */
	public boolean isAlive();

	/** Returns ID of this desktop.
	 * It is unique in the whole session.
	 */
	public String getId();
	/** Returns the execution, or null if this desktop is not under
	 * seving any execution (aka., not locked).
	 */
	public Execution getExecution();
	/** Returns the session of this desktop.
	 */
	public Session getSession();
	/** Returns the Web application this desktop belongs to.
	 */
	public WebApp getWebApp();

	/** Returns the page of the specified ID.
	 *
	 * <p>This is one of the only few method you could access
	 * before activating an execution.
	 *
	 * @exception ComponentNotFoundException if page not found
	 */
	public Page getPage(String pageId)
	throws ComponentNotFoundException;
	/** Returns the page of the specified ID, or null if no such page.
	 *
	 * @since 2.4.1
	 */
	public Page getPageIfAny(String pageId);
	/** Returns a readonly collection of all {@link Page} in this desktop.
	 */
	public Collection getPages();
	/** Returns whether a page exists.
	 */
	public boolean hasPage(String id);

	/** Returns all custom attributes associated with this desktop.
	 */
	public Map getAttributes();
	/** Returns the value of the specified custom attribute associated with the desktop.
	 */
	public Object getAttribute(String name);
	/** Sets the value of the specified custom attribute associated with the desktop.
	 */
	public Object setAttribute(String name, Object value);
	/** Removes the specified custom attribute associated with the desktop.
	 */
	public Object removeAttribute(String name);

	/** Returns all components contained in this desktop.
	 */
	public Collection getComponents();
	/** Returns the component of the specified UUID ({@link Component#getUuid}).
	 * @exception ComponentNotFoundException if component not found
	 */
	public Component getComponentByUuid(String uuid);
	/** Returns the component of the specified UUID
	 * ({@link Component#getUuid}), or null if not found.
	 */
	public Component getComponentByUuidIfAny(String uuid);

	/** Returns the URI for asynchronous update.
	 *
	 * <p>You rarely need this method unless for implementing special
	 * components, such as file upload.
	 *
	 * @param pathInfo the path to append to the returned URI, or null
	 * to ignore
	 */
	public String getUpdateURI(String pathInfo);

	/** Returns the path of the request that causes
	 * this desktop to be created, or "" if not available.
	 * In other words, it is the request path of the first page
	 * (see {@link Page#getRequestPath}).
	 *
	 * <p>For example, "/userguide/index.zul" (a ZUML page is requested)
	 * or /test (a richlet).
	 *
	 * @see Page#getRequestPath
	 * @since 3.0.0
	 */
	public String getRequestPath();
	/** Returns the current directory (never null).
	 * It is empty if no current directory at all.
	 * Otherwise, it must end with '/'.
	 * In other words, you could use getCurrentDirectory() + relative_path.
	 */
	public String getCurrentDirectory();
	/** Sets the current directory.
	 * @param dir the current directory. If null, an empty string is assumed
	 * (means no current directory at all).
	 */
	public void setCurrentDirectory(String dir);

	/** Returns the current bookmark (never null).
	 * The returned might be the same as the last call to {@link #setBookmark},
	 * because user might use BACK, FORWARD or others to change the bookmark.
	 */
	public String getBookmark();
	/** Sets a bookmark to this desktop. Then, when user press BACK, FORWARD
	 * or specify an URL with this bookmark, the onBookmarkChange event
	 * is sent to all pages of the desktop.
	 */
	public void setBookmark(String name);

	/** Returns URI for a dynamic generated media associated with a component.
	 * ZK Update Engine will then invoke invoke
	 * {@link org.zkoss.zk.ui.ext.render.DynamicMedia#getMedia} to response.
	 *
	 * <p>Note: to use with this method, {@link org.zkoss.zk.ui.ext.render.DynamicMedia}
	 * must be implemented as part of the object returned by
	 * {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}.
	 *
	 * <p>Used mainly for component implementation.
	 */
	public String getDynamicMediaURI(Component comp, String pathInfo);
	/** Returns URI for a media that is used to download to the client.
	 * The client will open a Save As dialog to save the specified file.
	 *
	 * <p>Note: once called, the media belongs to desktop and it is
	 * purged automatically. Thus, don't access it again after calling
	 * this method.
	 */
	public String getDownloadMediaURI(Media media, String pathInfo);

	/** Enables or disables the server-push feature.
	 * Before using any server-push threads, you have to enable it
	 * for the particular desktop first by use of this method.
	 * Refer to {@link Executions#activate} for more details.
	 *
	 * <p>Default: false
	 *
	 * <p>This method uses the default class
	 * (defined by {@link Device#getServerPushClass})
	 * to instantiate the server-push controller.
	 *
	 * @param enable whether to enable or to disable the server-push
	 * feature.
	 * @see Executions#activate
	 * @see Device#getServerPushClass
	 * @since 3.0.0
	 */
	public boolean enableServerPush(boolean enable);
	/** Sets the delay between each polling.
	 * It can be called only the server push is enabled for this desktop
	 * (by use of {@link #enableServerPush}).
	 *
	 * <p>Note: not all server-push controllers support this method.
	 * Currently, only on the client-polling-based controller (the default)
	 * supports this method.
	 *
	 * <p>To make the system more scalable, the implementation usually
	 * change the delay dynamically based on the loading.
	 * By specifying the minimal and maximal values, you can control
	 * the frequence to poll the server depending on the character
	 * of your Web applications.
	 *
	 * <p>Default: It looks up the value defined in the preferences
	 * ({@link Configuration#getPreference}):
	 * <code>PollingServerPush.delay.min</code>
	 * <code>PollingServerPush.delay.max</code>,
	 and <code>PollingServerPush.delay.factor</code>.
	 * If not defined, min is 1100, max is 10000 and factor is 5.
	 *
	 * @param min the minimal delay to poll the server for any pending
	 * server-push threads.
	 * Ignore (aka., the default value is used) if non-positive.
	 * Unit: milliseconds.
	 * @param max the maximal delay to poll the server for any pending
	 * server-push threads.
	 * Ignore (aka., the default value is used) if non-positive.
	 * Unit: milliseconds.
	 * @param factor the delay factor. The real delay is the processing
	 * time multiplies the delay factor. For example, if the last request
	 * took 1 second to process, then the client polling will be delayed
	 * for 1 x factor seconds, unless it is value 
	 * Ignore (aka., the default value is used) if non-positive.
	 * @since 3.0.0
	 */
	public void setServerPushDelay(int min, int max, int factor);
	/** Returns whether the server-push feature is enabled for this
	 * desktop.
	 *
	 * <p>Default: false.
	 *
	 * @since 3.0.0
	 */
	public boolean isServerPushEnabled();

	/** @deprecated As of release 3.0.6, replaced by {@link #addListener}.
	 */
	public void addEventInterceptor(EventInterceptor ei);
	/** @deprecated As of release 3.0.6, replaced by {@link #removeListener}.
	 */
	public boolean removeEventInterceptor(EventInterceptor ei);

	/** Adds a listener.
	 *
	 * <p>Note: if the listener is added twice, it will be invoked
	 * twice when a corresponding event occurs.
	 *
	 * @param listener the listener. It cannot be null.
	 * It must be an instance that implements
	 * {@link org.zkoss.zk.ui.util.DesktopCleanup},
	 * {@link org.zkoss.zk.ui.util.ExecutionInit},
	 * {@link org.zkoss.zk.ui.util.ExecutionCleanup},
	 * {@link org.zkoss.zk.ui.util.UiLifeCycle},
	 * {@link EventInterceptor}
	 * and/or {@link org.zkoss.zk.ui.util.AuRequestProcessor}.<br/>
	 * Note: {@link org.zkoss.zk.ui.event.EventThreadInit},
	 * {@link org.zkoss.zk.ui.event.EventThreadCleanup},
	 * {@link org.zkoss.zk.ui.event.EventThreadSuspend} and
	 * {@link org.zkoss.zk.ui.event.EventThreadResume} are not supported.
	 * @exception IllegalArgumentException if not a valid listener.
	 * @since 3.0.6
	 */
	public void addListener(Object listener);
	/** Removes a listener.
	 *
	 * @return whether the listener is removed successfully.
	 * {@link Object#equals} is used to check whether a listener is added.
	 * @since 3.0.6
	 */
	public boolean removeListener(Object listener);

	/** Invalidates the desktop.
	 * All pages will be redrawn.
	 * @since 3.0.6
	 */
	public void invalidate();
}
