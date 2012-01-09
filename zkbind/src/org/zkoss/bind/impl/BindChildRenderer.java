/* BindChildRenderer.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.bind.IterationStatus;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Label;

/**
 * to renderer children of component
 * @author dennis
 * @since 6.0.0
 */
public class BindChildRenderer extends AbstractRenderer{
	private static final long serialVersionUID = 1L;

	public BindChildRenderer(){
		setAttributeName(AnnotateBinderHelper.CHILDREN_KEY);
	}
	
	public void render(final Component owner, final Object data, final int index){
		final Template tm = resoloveTemplate(owner,owner,data,index,"children");
		if (tm == null) {
			Label l = new Label(data==null?"":data.toString());
			l.setParent(owner);
			return;
		}
		
		final IterationStatus iterStatus = new AbstractIterationStatus(){//provide iteration status in this context
			private static final long serialVersionUID = 1L;
			@Override
			public int getIndex() {
				return Integer.valueOf(index);
			}
		};
		
		final String var = (String) tm.getParameters().get("var");
		final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
		
		final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
		final String itervarnm = itervar == null ? varnm+STATUS_POST_VAR : itervar; //provide default value if not specified

		final Component[] items = tm.create(owner, null, 
			new VariableResolverX() {//this resolver is for EL ${} not for binding 
				public Object resolveVariable(String name) {
					//shall never call here
					return varnm.equals(name) ? data : null;
				}

				public Object resolveVariable(XelContext ctx, Object base, Object name) throws XelException {
					if (base == null) {
						if(varnm.equals(name)){
							return data;
						}else if(itervarnm.equals(name)){//iteration status
							return iterStatus;
						}
					}
					return null;
				}
			}, null);

		//add template dependency
		if (items != null && items.length > 0)
			addTemplateDependency(owner, items[0], data, index);

		for(Component comp:items){
			comp.setAttribute(BinderImpl.VAR, varnm);
			comp.setAttribute(varnm, data); //kept the value
			comp.setAttribute(itervarnm, iterStatus);
			
			//to force init and load
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, comp));
		}
	}
}
