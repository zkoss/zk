/* AbstractRenderer.java

	Purpose:
		
	Description:
		
	History:
		2012/1/4 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Template;

/**
 * to handle the common task of resolver a template fo a renderer
 * @author dennis
 * @since 6.0.0
 */
public abstract class AbstractRenderer implements TemplateRendererCtrl, Serializable {
	private static final long serialVersionUID = 3738037033671761825L;

	protected static final String EACH_ATTR = TemplateResolver.EACH_ATTR;
	protected static final String EACH_VAR = TemplateResolver.EACH_VAR;
	protected static final String STATUS_ATTR = TemplateResolver.STATUS_ATTR;
	protected static final String STATUS_POST_VAR = TemplateResolver.STATUS_POST_VAR;
	protected static final String EACH_STATUS_VAR = TemplateResolver.EACH_STATUS_VAR;

	static private String TREE_PATH = "$TREEPATH$";//for tree model only
	
	private String _attributeName;
	
	@Override
	public void setAttributeName(String name) {
		_attributeName = name;
	}

	private Template lookupTemplate(Component comp, String name) {
		if(comp==null) return null;
		Template template = comp.getTemplate(name);
		return template==null?lookupTemplate(comp.getParent(),name):template;
	}
	
	protected Template resoloveTemplate(Component templateComp, Component comp, Object data, int index, String defaultName) {
		//a detached component(ex,grid.onInitRender) will still calling the render, see test case collection-template-grid.zul
		//TODO need to check is this a zk bug and repor it
		if(comp.getPage()==null || comp.getParent()==null) return null;//no template
		
		final Binder binder = (Binder)comp.getAttribute(BinderImpl.BINDER,true);
		final TemplateResolver resolver = ((BinderCtrl)binder).getTemplateResolver(templateComp, _attributeName);
		Template template = null;
		if(resolver!=null){
			template = resolver.resolveTemplate(comp,data,index);
			if(template==null){
				throw new UiException("template not found for component "+comp+" by resolver "+resolver);
			}
		}else{
			template = lookupTemplate(comp, defaultName);
		}
		return template;
	}
    //ZK-739: Allow dynamic template for collection binding.
	protected void addTemplateTracking(Component templateComp, final Component eachComp, Object data, final int index) {
		final Binder binder = (Binder)eachComp.getAttribute(BinderImpl.BINDER,true);
		if(binder == null) return; //no binder
		final TemplateResolver resolver = ((BinderCtrl)binder).getTemplateResolver(templateComp, _attributeName);
		if(resolver == null) return;//no resolver
		Object old = null;
		Object oldStatus = null;
		try {
			old = eachComp.setAttribute(EACH_VAR, data); //kept the value for template resolving
			oldStatus = eachComp.setAttribute(EACH_STATUS_VAR, new AbstractIterationStatus(){//provide iteration status in this context
				private static final long serialVersionUID = 1L;
				@Override
				public int getIndex() {
					return Integer.valueOf(index);
				}
			});
			resolver.addTemplateTracking(eachComp);
		} finally {
			eachComp.setAttribute(EACH_STATUS_VAR, oldStatus);
			eachComp.setAttribute(TemplateResolver.EACH_VAR, old);
		}
	}
	//ZK-758: Unable to NotifyChange with indirect reference on an Array/List
	protected void addItemReference(final Component comp, int index, String varnm) {
		final Binder binder = (Binder)comp.getAttribute(BinderImpl.BINDER, true);
		if (binder == null) return; //no binder
		final String expression = BinderImpl.MODEL+"["+index+"]";
		comp.setAttribute(varnm, new ReferenceImpl(binder, expression, comp)); //reference
	}
	
	//ZK-758: Unable to NotifyChange with indirect reference on an Array/List, for tree model only
	protected void addItemReference(final Component comp, int[] path, String varnm) {
		final Binder binder = (Binder)comp.getAttribute(BinderImpl.BINDER, true);
		if (binder == null) return; //no binder
		comp.setAttribute(TREE_PATH, path);
		final String expression = BinderImpl.MODEL+"["+TREE_PATH+"]";
		comp.setAttribute(varnm, new ReferenceImpl(binder, expression, comp)); //reference
	}
	
	
}
