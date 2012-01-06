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
 * 
 */
public abstract class AbstractRenderer implements TemplateRendererCtrl, Serializable {
	private static final long serialVersionUID = 3738037033671761825L;

	protected static final String EACH_ATTR = TemplateResolver.EACH_ATTR;
	protected static final String EACH_VAR = TemplateResolver.EACH_VAR;
	protected static final String STATUS_ATTR = TemplateResolver.STATUS_ATTR;
	protected static final String STATUS_POST_VAR = TemplateResolver.STATUS_POST_VAR;
	protected static final String EACH_STATUS_VAR = TemplateResolver.EACH_STATUS_VAR;

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
	protected void addTemplateDependency(Component templateComp, final Component eachComp, Object data, final int index) {
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
			final Binder binder = (Binder)eachComp.getAttribute(BinderImpl.BINDER,true);
			final TemplateResolver resolver = ((BinderCtrl)binder).getTemplateResolver(templateComp, _attributeName);
			resolver.addTemplateDependency(eachComp);
		} finally {
			eachComp.setAttribute(EACH_STATUS_VAR, oldStatus);
			eachComp.setAttribute(TemplateResolver.EACH_VAR, old);
		}
	}
}
