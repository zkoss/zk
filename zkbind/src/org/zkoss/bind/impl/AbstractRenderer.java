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
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Template;

/**
 * to handle the common task of resolver a template of a renderer
 * @author dennis
 * @since 6.0.0
 */
public abstract class AbstractRenderer implements TemplateRendererCtrl, Serializable {
	private static final long serialVersionUID = 3738037033671761825L;

	protected static final String EACH_ATTR = TemplateResolver.EACH_ATTR;
	protected static final String EACH_VAR = TemplateResolver.EACH_VAR;
	protected static final String STATUS_ATTR = TemplateResolver.STATUS_ATTR;
	protected static final String STATUS_POST_VAR = "Status";
	protected static final String EACH_STATUS_VAR = TemplateResolver.EACH_STATUS_VAR;
	
	private String _attributeName;
	
	
	public void setAttributeName(String name) {
		_attributeName = name;
	}

	private Template lookupTemplate(Component comp, String name) {
		if(comp==null) return null;
		Template template = comp.getTemplate(name);
		return template==null?lookupTemplate(comp.getParent(),name):template;
	}
	protected Template resolveTemplate(Component templateComp, Component comp, Object data, int index, int size, String defaultName) {
		return resolveTemplate(templateComp,comp,data,index,size,defaultName,null);
	}
	protected Template resolveTemplate(Component templateComp, Component comp, Object data, int index, int size, String defaultName, String subType) {
		//a detached component(ex,grid.onInitRender) will still call the render, see test case collection-template-grid.zul
		//TODO need to check is this a zk bug and report it
		if(comp.getPage()==null) return null;//no template
		
		final Binder binder = BinderUtil.getBinder(comp, true);
		final TemplateResolver resolver = ((BinderCtrl)binder).getTemplateResolver(templateComp, _attributeName);
		Template template = null;
		if(resolver!=null){
			template = resolver.resolveTemplate(comp,data,index,size,subType);
			if(template==null){
				throw new UiException("template not found for component "+comp+" by resolver "+resolver);
			}
		}else{
			template = lookupTemplate(comp, subType==null?defaultName:defaultName+":"+subType);
		}
		return template;
	}
    //ZK-739: Allow dynamic template for collection binding.
	protected void addTemplateTracking(Component templateComp, final Component eachComp,final Object data, final int index, final int size) {
		final Binder binder = BinderUtil.getBinder(eachComp, true);
		if(binder == null) return; //no binder
		final TemplateResolver resolver = ((BinderCtrl)binder).getTemplateResolver(templateComp, _attributeName);
		if(resolver == null) return;//no resolver
		Object old = null;
		Object oldStatus = null;
		try {
			old = eachComp.setAttribute(EACH_VAR, data); //kept the value for template resolving
			oldStatus = eachComp.setAttribute(EACH_STATUS_VAR, new AbstractForEachStatus(){//provide iteration status in this context
				private static final long serialVersionUID = 1L;
				
				public int getIndex() {
					return index;
				}
				
				public Integer getEnd(){
					if(size<0){
						throw new UiException("end attribute is not supported");// the tree case
					}
					return size;
				}

				public Object getCurrent() {
					return data;
				}
			});
			resolver.addTemplateTracking(eachComp,data,index,size);
		} finally {
			eachComp.setAttribute(EACH_STATUS_VAR, oldStatus);
			eachComp.setAttribute(TemplateResolver.EACH_VAR, old);
		}
	}
	//ZK-758: Unable to NotifyChange with indirect reference on an Array/List
	protected void addItemReference(Component modelOwner, final Component comp, int index, String varnm) {
		// ZK-2456: if comp is native, add reference to all of its children
		if (comp.getDefinition().isNative()) {
			for (Component child : comp.getChildren()) {
				addItemReference(modelOwner, child, index, varnm);
			}
		} else {
			final Binder binder = BinderUtil.getBinder(comp, true);
			if (binder == null) return; //no binder
			final String expression = BindELContext.getModelName(modelOwner)+"["+index+"]";
			//should not use binder.addReferenceBinding(comp, varnm, expression, null); here, it will mark comp bound.
			//it is safe that we set to comp attr here since the component is created by renderer/binder. 
			comp.setAttribute(varnm, new ReferenceBindingImpl(binder, comp, varnm, expression)); //reference
		}
	}
}
