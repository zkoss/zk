/* UiFactory.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 10:50:48     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.Reader;
import java.io.IOException;

import org.zkoss.idom.Document;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;

/**
 * Used to create {@link Desktop}, {@link Page} and to convert path/URL
 * to {@link PageDefinition}.
 *
 * @author tomyeh
 */
public interface UiFactory {
	/** Starts this factory.
	 */
	public void start(WebApp wapp);
	/** Stops this factory.
	 */
	public void stop(WebApp wapp);

	/** Creates an instance of {@link Session}.
	 *
	 * @param wapp the web application
	 * @param nativeSess the native session depending on the Web server.
	 * If HTTP is used, it is javax.servlet.http.HttpSession.
	 * @param request the original request. If HTTP, it is
	 * javax.servlet.http.HttlServletRequest.
	 * @since 3.0.1
	 */
	public Session newSession(WebApp wapp, Object nativeSess, Object request);

	/** Tests whether the specified request is a richlet.
	 *
	 * <p>If this method returns true, {@link #getRichlet} is called to create
	 * the richlet. Otherwise, {@link #getPageDefinition} is called to retrieve
	 * the page definition from a ZUML page.
	 *
	 * @param bRichlet the default value that this method shall return.
	 * It is a suggetion. If you don't know what to do, just return bRichlet.
	 * @return true if it is a richlet, or false if it is a ZUML page.
	 */
	public boolean isRichlet(RequestInfo ri, boolean bRichlet);
	/** Returns a richlet for specified request, or null if the richlet is not found..
	 */
	public Richlet getRichlet(RequestInfo ri, String path);

	/** Creates an instance of {@link Desktop}.
	 *
	 * <p>Note: the returned instance must also implement {@link DesktopCtrl}.
	 *
	 * @param ri the additional request information.
	 * @param updateURI the URI that is used as the base to generate
	 * URI for asynchronous updates. In other words, it is used to
	 * implement {@link Desktop#getUpdateURI}
	 * @param path the path to request this desktop, such as "/test/test.zul",
	 * or null if it is caused by a filter.
	 */
	public Desktop newDesktop(RequestInfo ri, String updateURI, String path);

	/** Creates an instance of {@link Page} for the specified page definition.
	 *
	 * <p>Note: the returned instance must also implement {@link PageCtrl}.
	 *
	 * @param ri the additional request information.
	 * @param pagedef the page definition. If null, it means the page is served
	 * by a {@link Richlet} instance.
	 * @param path the path to request this page, or null if it is caused
	 * by a filter.
	 */
	public Page newPage(RequestInfo ri, PageDefinition pagedef, String path);
	/** Creates an instance of {@link Page} for the specified richlet.
	 *
	 * <p>Note: the returned instance must also implement {@link PageCtrl}.
	 */
	public Page newPage(RequestInfo ri, Richlet richlet, String path);

	/** Creates and initializes a component based on the specified
	 * {@link ComponentInfo}.
	 *
	 * <p>After called, the new component is added to the page, and becomes a
	 * child of the specified parent, if any. In addition, the properties
	 * and custom attributes defined in {@link ComponentDefinition}
	 * and {@link ComponentInfo} are all
	 * applied to the new component.
	 *
	 * @param page the page that the new component belongs to (never null).
	 * @param parent the parent compoent, or null if the new component is root.
	 * @param compInfo the component information
	 * @since 2.4.0
	 */
	public Component newComponent(Page page, Component parent,
	ComponentInfo compInfo);

	/** Creates and initializes a component based on the specified
	 * {@link ComponentDefinition}.
	 *
	 * <p>After called, the new component is added to the page, and becomes a
	 * child of the specified parent, if any. In addition, the properties
	 * and custom attributes defined in {@link ComponentDefinition} are all
	 * applied to the new component.
	 *
	 * @param page the page that the new component belongs to (never null).
	 * @param parent the parent compoent, or null if the new component is root.
	 * @param clsnm the implementation class of the component.
	 * If null, {@link ComponentDefinition#getImplementationClass} will
	 * be used.
	 */
	public Component newComponent(Page page, Component parent,
	ComponentDefinition compdef, String clsnm);

	/** Returns the page definition of the specified path, or null if
	 * not found.
	 *
	 * <p>Note: unlike {@link #newDesktop} and {@link #newPage},
	 * it won't be called if it is created thru filter. If filter,
	 * {@link #getPageDefinitionDirectly} is called.
	 *
	 * <p>Typical use: retrieve the content from a database based on
	 * the specified path, and then invoke {@link #getPageDefinitionDirectly}
	 * to 'convert' the content into a page definition ({@link PageDefinition}).
	 *
	 * <p>Notice that a page definition usually contains taglib or zscript
	 * files. These files are located by {@link RequestInfo#getLocator}.
	 * If you want them
	 * to be loaded from the database too, you must invoke 
	 * {@link RequestInfo#setLocator} to change the default locator.
	 *
	 * <p>Implementation NOTE: DO NOT invoke
	 * {@link org.zkoss.zk.ui.Execution#getPageDefinition(String)}.
	 * Otherwise, an endless loop occurs.
	 *
	 * @param ri the additional request information.
	 * @param path the path to request this page.
	 * @see #getPageDefinitionDirectly
	 */
	public PageDefinition getPageDefinition(RequestInfo ri, String path);
	/** Returns the page definition of the specified content; never null.
	 *
	 * <p>It is called when a filter or {@link org.zkoss.zk.ui.Execution#getPageDefinitionDirectly(String, String)}
	 * is going to generate a page definition for the content it intercepts.
	 *
	 * <p>Implementation NOTE: DO NOT invoke
	 * {@link org.zkoss.zk.ui.Execution#getPageDefinitionDirectly(String, String)}.
	 * Otherwise, an endless loop occurs.
	 *
	 * @param content the raw content of the page. It must be in ZUML.
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, String content, String extension);

	/** Returns the page definition of the specified content; never null.
	 *
	 * <p>It is called when {@link org.zkoss.zk.ui.Execution#getPageDefinitionDirectly(Document, String)}
	 * is going to generate a page definition for the content it intercepts.
	 *
	 * <p>Implementation NOTE: DO NOT invoke
	 * {@link org.zkoss.zk.ui.Execution#getPageDefinitionDirectly(Document, String)}.
	 * Otherwise, an endless loop occurs.
	 *
	 * @param content the raw content of the page in DOM.
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, Document content, String extension);
	/** Returns the page definition of the specified reader; never null.
	 *
	 * <p>It is called when {@link org.zkoss.zk.ui.Execution#getPageDefinitionDirectly(Reader, String)}
	 * is going to generate a page definition for the content it intercepts.
	 *
	 * <p>Implementation NOTE: DO NOT invoke
	 * {@link org.zkoss.zk.ui.Execution#getPageDefinitionDirectly(Reader, String)}.
	 * Otherwise, an endless loop occurs.
	 *
	 * @param reader the reader to retrieve the raw content in ZUML.
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, Reader reader, String extension) throws IOException;
}
