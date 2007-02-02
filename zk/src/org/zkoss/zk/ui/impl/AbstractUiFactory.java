/* AbstractUiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 11:32:23     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.Reader;
import java.io.IOException;

import org.zkoss.idom.Document;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.Milieu;

/**
 * The sketetal implementation of {@link UiFactory}.
 *
 * <p>Derived class must immplement {@link UiFactory#newSession}, which
 * depends on the Web container.
 *
 * @author tomyeh
 */
abstract public class AbstractUiFactory implements UiFactory {
	//-- UiFactory --//
	public void start(WebApp wapp) {
	}
	public void stop(WebApp wapp) {
	}
	public Desktop newDesktop(RequestInfo ri, String updateURI, String path) {
		String dir = null, clientType = null;
		if (path != null) { //convert to directory
			final int j = path.lastIndexOf('/');
			dir = j >= 0 ? path.substring(0, j + 1): null;

			//though UiEngine.execNewPage will set the client type later,
			//we 'guess' a value first by use of the extension
			//reason: less dependent of how UiEngine is implemented
			final int k = path.lastIndexOf('.');
			if (k > j && k + 1 < path.length()) {
				final String ext = path.substring(k + 1);
				try {
					clientType =
						LanguageDefinition.getByExtension(ext).getClientType();
				} catch (DefinitionNotFoundException ex) { //ignore
				} 
			}
		}
		return new DesktopImpl(ri.getWebApp(), updateURI, dir, clientType);
	}
	public Page newPage(RequestInfo ri, PageDefinition pagedef, String path) {
		return new PageImpl(pagedef);
	}
	public Page newPage(RequestInfo ri, Richlet richlet, String path) {
		return new PageImpl(richlet, path);
	}
	public Component newComponent(Page page, Component parent,
	ComponentDefinition instdef) {
		final Component comp = instdef.newInstance(page);

		if (parent != null) comp.setParent(parent);
		else comp.setPage(page);

		comp.applyProperties(); //including custom-attributes
		return comp;
	}

	/** Returns the page definition of the specified path, or null if not found.
	 *
	 * <p>Dependency: Execution.createComponents -&amp; Execution.getPageDefinition
	 * -&amp; UiFactory.getPageDefiition -&amp; PageDefinitions.getPageDefinition
	 */
	public PageDefinition getPageDefinition(RequestInfo ri, String path) {
		return PageDefinitions.getPageDefinition(
			ri.getWebApp(), ri.getLocator(), path);
	}
	/** Returns the page definition of the specified content; never null.
	 *
	 * <p>Dependency: Execution.createComponentsDirectly -&amp; Execution.getPageDefinitionDirectly
	 * -&amp; UiFactory.getPageDefiitionDirectly -&amp; PageDefintions.getPageDefinitionDirectly
	 */
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, String content, String extension) {
		return PageDefinitions.getPageDefinitionDirectly(
			ri.getWebApp(), ri.getLocator(), content, extension);
	}

	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, Document content, String extension) {
		return PageDefinitions.getPageDefinitionDirectly(
			ri.getWebApp(), ri.getLocator(), content, extension);
	}
	public PageDefinition getPageDefinitionDirectly(
	RequestInfo ri, Reader reader, String extension) throws IOException {
		return PageDefinitions.getPageDefinitionDirectly(
			ri.getWebApp(), ri.getLocator(), reader, extension);
	}
}
