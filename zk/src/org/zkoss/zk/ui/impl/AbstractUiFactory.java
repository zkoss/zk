/* AbstractUiFactory.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 11:32:23     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.idom.Document;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.util.Composer;

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
	private Map<Component,ComponentInfo> getInfoMap(){
		Execution exec = Executions.getCurrent();
		if(exec == null ){
			return null;
		}
		Map<Component,ComponentInfo> result = (Map<Component,ComponentInfo>) exec.getAttribute(Attributes.COMPONENT_INFO);
		if(result == null){
			result = new HashMap<Component,ComponentInfo>();
			exec.setAttribute(Attributes.COMPONENT_INFO, result);
		}
		return result;
	}
	public Component newComponent(Page page, Component parent,
	ComponentInfo compInfo, Component insertBefore) {
		final Component comp = compInfo.newInstance(page, parent);
		Map<Component,ComponentInfo> map = getInfoMap();
		if(map != null)
			map.put(comp, compInfo);
		
		if (parent != null)
			parent.insertBefore(comp, insertBefore);
		else
			comp.setPageBefore(page, insertBefore);

		if (comp instanceof BeforeCompose)
			((BeforeCompose)comp).beforeCompose();
		compInfo.applyProperties(comp); //include comp's definition
		
		if(map != null)
			map.remove(comp);
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

	/** Instantiates a composer of the given class.
	 * <p>Default: creates an instance of klass by use of its no-arg constructor.
	 * @since 6.0.0
	 */
	public Composer newComposer(Page page, Class klass) {
		if (!Composer.class.isAssignableFrom(klass))
			throw new UiException(klass + " must implement "+Composer.class);
		try {
			return (Composer)klass.newInstance();
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex, "Unable to instantiate "+klass);
		}
	}
	/** Instantiates a composer of the given class name.
	 * <p>Default: use {@link Page#resolveClass} to resolve the class
	 * and then invoke {@link #newComposer(Page, Class)} to instantiate an instance.
	 * @since 6.0.0
	 */
	public Composer newComposer(Page page, String className)
	throws ClassNotFoundException {
		return newComposer(page,
			page != null ? page.resolveClass(className): Classes.forNameByThread(className));
	}
	/** Instantiates a server push of the given class.
	 * <p>Default: creates an instance of klass by use of its no-arg constructor.
	 * @since 6.0.0
	 */
	public ServerPush newServerPush(Desktop desktop, Class klass) {
		if (!ServerPush.class.isAssignableFrom(klass))
			throw new UiException(klass + " must implement "+ServerPush.class);
		try {
			return (ServerPush)klass.newInstance();
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex, "Unable to instantiate "+klass);
		}
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
