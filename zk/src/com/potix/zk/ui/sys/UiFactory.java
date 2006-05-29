/* UiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 10:50:48     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.metainfo.PageDefinition;

/**
 * Used to create {@link Desktop}, {@link Page} and to convert path/URL
 * to {@link PageDefinition}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface UiFactory {
	/** Starts this factory.
	 */
	public void start(WebApp wapp);
	/** Stops this factory.
	 */
	public void stop(WebApp wapp);

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

	/** Creates an instance of {@link Page}.
	 *
	 * <p>Note: the returned instance must also implement {@link PageCtrl}.
	 *
	 * @param ri the additional request information.
	 * @param pagedef the page definition.
	 * @param path the path to request this page, or null if it is caused
	 * by a filter.
	 */
	public Page newPage(RequestInfo ri, PageDefinition pagedef, String path);

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
	 * @param ri the additional request information.
	 * @param path the path to request this page.
	 * @see #getPageDefinitionDirectly
	 */
	public PageDefinition getPageDefinition(RequestInfo ri, String path);
	/** Returns the page definition of the specified content; never null.
	 *
	 * <p>It is called when a filter is going to generate a page definition
	 * for the content it intercepts.
	 *
	 * @param content the raw content of the page. It must be a XML and
	 * compliant to the page format (such as ZUL).
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, String content, String extension);
}
