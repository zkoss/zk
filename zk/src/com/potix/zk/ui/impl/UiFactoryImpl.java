/* UiFactoryImpl.java

{{IS_NOTE
	$Id: UiFactoryImpl.java,v 1.3 2006/04/24 16:36:37 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 11:32:23     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import javax.servlet.ServletContext;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.sys.UiFactory;
import com.potix.zk.ui.sys.RequestInfo;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.http.PageDefinitions;

/**
 * The default implementation of {@link UiFactory}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/04/24 16:36:37 $
 */
public class UiFactoryImpl implements UiFactory {
	//-- UiFactory --//
	public void start(WebApp wapp) {
	}
	public void stop(WebApp wapp) {
	}
	public Desktop newDesktop(RequestInfo ri, String updateURI, String path) {
		if (path != null) { //convert to directory
			final int j = path.lastIndexOf('/');
			path = j >= 0 ? path.substring(0, j+1): null;
		}
		return new DesktopImpl(ri.getWebApp(), updateURI, path);
	}
	public Page newPage(RequestInfo ri, PageDefinition pagedef, String path) {
		return new PageImpl(pagedef);
	}
	public PageDefinition getPageDefinition(RequestInfo ri, String path) {
		//FUTURE: better isolation of PageDefinitions
		final Object ctx = ri.getWebApp().getNativeContext();
		if (ctx instanceof ServletContext) {
			return PageDefinitions.getPageDefinition((ServletContext)ctx, path);
		} else {
			throw new UnsupportedOperationException("Unknown context: "+ctx);
		}
	}
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, String content, String extension) {
		return PageDefinitions.getPageDefinitionDirectly(
			ri.getLocator(), content, extension);
	}
}
