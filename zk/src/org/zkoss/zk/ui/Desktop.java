/* Desktop.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  9 16:27:21     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.Map;

import org.zkoss.util.media.Media;
import org.zkoss.zk.device.Device;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.sys.Storage;

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
public interface Desktop extends Scope {
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

	/** Returns the execution, or null if this desktop is not 
	 * serving any execution (a.k.a., not locked).
	 */
	public Execution getExecution();

	/** Returns the session of this desktop.
	 */
	public Session getSession();

	/** Returns the Web application this desktop belongs to.
	 */
	public WebApp getWebApp();

	/** Returns the page of the specified ID or UUID.
	 *
	 * <p>This is one of the only few method you could access
	 * before activating an execution.
	 *
	 * @param pageId the page's ID or UUID. ID has the higher priority.
	 * @exception ComponentNotFoundException if page not found
	 */
	public Page getPage(String pageId) throws ComponentNotFoundException;

	/** Returns the page of the specified ID or UUID, or null if no such page.
	 *
	 * @param pageId the page's ID or UUID. ID has the higher priority.
	 * @since 2.4.1
	 */
	public Page getPageIfAny(String pageId);

	/** Returns a readonly collection of all {@link Page} in this desktop.
	 */
	public Collection<Page> getPages();

	/** Returns the first page, or null if no page at all (happens when the desktop
	 * has been destroyed)
	 * @since 5.0.3
	 */
	public Page getFirstPage();

	/** Returns whether a page exists.
	 */
	public boolean hasPage(String pageId);

	/** Returns all custom attributes associated with this desktop.
	 */
	public Map<String, Object> getAttributes();

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
	public Collection<Component> getComponents();

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
	 * <p>For example, "/foo/index.zul" (a ZUML page is requested)
	 * or /test (a richlet).
	 *
	 * @see Page#getRequestPath
	 * @since 3.0.0
	 */
	public String getRequestPath();

	/** Returns the query string that is contained in the request URL after the
	 * path ({@link #getRequestPath}), or null if the URL does not have a query
	 * string.
	 * @since 5.0.2
	 */
	public String getQueryString();

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
	 * <p>It is the same as <code>setBookmark(name, false)</code>
	 */
	public void setBookmark(String name);

	/**
	 * Sets the bookmark to this desktop. with more control.
	 * @param name the name of the bookmark
	 * @param replace if true, the bookmark is replaced (in the history list)
	 * @since 3.6.4
	 * @see #setBookmark(String)
	 */
	public void setBookmark(String name, boolean replace);

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
	 * @see org.zkoss.zk.ui.sys.DesktopCtrl#enableServerPush(org.zkoss.zk.ui.sys.ServerPush)
	 * @since 3.0.0
	 */
	public boolean enableServerPush(boolean enable);

	/** Returns whether the server-push feature is enabled for this
	 * desktop.
	 *
	 * <p>Default: false.
	 *
	 * @since 3.0.0
	 */
	public boolean isServerPushEnabled();

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
	 * {@link org.zkoss.zk.ui.util.EventInterceptor}
	 * and/or {@link org.zkoss.zk.au.AuService}.<br/>
	 * Note: {@link org.zkoss.zk.ui.util.DesktopInit},
	 * {@link org.zkoss.zk.ui.event.EventThreadInit},
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

	/**
	 * Returns the storage in a desktop scope.
	 * @return a storage implementation, never null.
	 * @since 8.0.0
	 */
	public Storage getStorage();

	/**
	 * Pushes a new history state.
	 * @param state a state object.
	 * @param title a title for the state. May be ignored by some browsers.
	 * @param url the history entry's URL. Could be null.
	 * @since 8.5.0
	 */
	public void pushHistoryState(Object state, String title, String url);

	/**
	 * Replaces the current history state.
	 * @param state a state object.
	 * @param title a title for the state. May be ignored by some browsers.
	 * @param url the history entry's URL. Could be null.
	 * @since 8.5.0
	 */
	public void replaceHistoryState(Object state, String title, String url);

	/**
	 * Returns the URI for ZK resource.
	 * @param pathInfo the path to append to the returned URI, or null to ignore
	 * @since 9.1.0
	 */
	public String getResourceURI(String pathInfo);
}
