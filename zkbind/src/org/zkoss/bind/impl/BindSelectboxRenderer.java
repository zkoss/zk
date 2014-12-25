/* BindSelectboxRenderer.java

	Purpose:
		
	Description:
		
	History:
		Dec 15, 2011 3:47:56 PM, Created by dennischen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Selectbox;

/**
 * selectbox renderer.
 * @author dennischen
 * @since 6.0.0
 */
public class BindSelectboxRenderer extends AbstractRenderer implements ItemRenderer<Object>,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	
	public String render(final Component owner, final Object data, final int index) throws Exception {
		final int size = ((Selectbox)owner).getModel().getSize();
		final Template tm = resolveTemplate(owner,owner,data,index,size,"model");
		if (tm == null) {
			return Objects.toString(data);
		} else {
			
			final ForEachStatus iterStatus = new AbstractForEachStatus(){//provide iteration status in this context
				private static final long serialVersionUID = 1L;
				
				public int getIndex() {
					return index;
				}
				
				public Object getCurrent(){
					return data;
				}
				
				public Integer getEnd(){
					return size;
				}
			};
			
			final String var = (String) tm.getParameters().get(EACH_ATTR);
			final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
			final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
			final String itervarnm = itervar == null ? ( var==null?EACH_STATUS_VAR:varnm+STATUS_POST_VAR) : itervar; //provide default value if not specified
			
			//bug 1188, EL when nested var and itervar
			Object oldVar = owner.getAttribute(varnm);
			Object oldIter = owner.getAttribute(itervarnm);
			owner.setAttribute(varnm, data);
			owner.setAttribute(itervarnm, iterStatus);
			
			final Component[] items = tm.create(owner, null, null, null);
			
			owner.setAttribute(varnm, oldVar);
			owner.setAttribute(itervarnm, oldIter);
			
			if (items.length != 1)
				throw new UiException(
						"The model template must have exactly one item, not "
								+ items.length);
			if (!(items[0] instanceof Label))
				throw new UiException(
						"The model template can only support Label component, not "
								+ items[0]);
			final Label lbl = ((Label) items[0]);
			lbl.setAttribute(BinderImpl.VAR, varnm);
			addItemReference(owner, lbl, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			lbl.setAttribute(itervarnm, iterStatus);

			//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
			//selectbox doesn't support 1787 because it attaching comp is always detached after render
//			//lbl.setAttribute(TemplateResolver.TEMPLATE_OBJECT, owner.removeAttribute(TemplateResolver.TEMPLATE_OBJECT));
			
			//add template dependency
			addTemplateTracking(owner, lbl, data, index, size);

			//to force init and load
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, lbl));
			lbl.detach();
			return lbl.getValue();
		}
	}
}
