/* AbstractUiFactory.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 11:32:23     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;

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

	public boolean isRichlet(RequestInfo ri, boolean bRichlet) {
		return bRichlet;
	}
	public Richlet getRichlet(RequestInfo ri, String path) {
		return ri.getWebApp().getConfiguration().getRichletByPath(path);
	}

	public Desktop newDesktop(RequestInfo ri, String updateURI, String path) {
		String deviceType = null;
		if (path != null) { //convert to directory
			//though UiEngine.execNewPage will set the device type later,
			//we 'guess' a value first by use of the extension
			//reason: less dependent of how UiEngine is implemented
			final int k = path.lastIndexOf('.') + 1;
			if (k > 0 && path.indexOf('/', k) < 0 && k < path.length()) {
				final String ext = path.substring(k);
				try {
					deviceType =
						LanguageDefinition.getByExtension(ext).getDeviceType();
				} catch (DefinitionNotFoundException ex) { //ignore
				} 
			}
		}
		return new DesktopImpl(ri.getWebApp(), updateURI, path, deviceType,
			ri.getNativeRequest());
	}
	public Page newPage(RequestInfo ri, PageDefinition pagedef, String path) {
		return new PageImpl(pagedef);
	}
	public Page newPage(RequestInfo ri, Richlet richlet, String path) {
		return new PageImpl(richlet, path);
	}
	public Component newComponent(Page page, Component parent,
	ComponentInfo compInfo) {
		final Component comp = compInfo.newInstance(page, parent);

		if (parent != null) comp.setParent(parent);
		else comp.setPage(page);

		compInfo.applyProperties(comp); //include comp's definition
		return comp;
	}
	public Component newComponent(Page page, Component parent,
	ComponentDefinition compdef, String clsnm) {
		final Component comp = compdef.newInstance(page, clsnm);

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
