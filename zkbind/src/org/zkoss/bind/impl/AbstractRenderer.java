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
public class AbstractRenderer implements TemplateRendererCtrl, Serializable {

	private static final long serialVersionUID = 3738037033671761825L;

	static final protected String EACH_ATTR = "var";
	static final protected String EACH_VAR = "each";
	static final protected String STATUS_ATTR = "status";
	static final protected String STATUS_POST_VAR = "Status";
	
	protected String _attributeName;
	
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
}
