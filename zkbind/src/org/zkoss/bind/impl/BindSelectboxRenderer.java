/* BindSelectboxRenderer.java

	Purpose:
		
	Description:
		
	History:
		Dec 15, 2011 3:47:56 PM, Created by dennischen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
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
	@Override
	public String render(final Component owner, final Object data, final int index) throws Exception {
		final int size = ((Selectbox)owner).getModel().getSize();
		final Template tm = resoloveTemplate(owner,owner,data,index,size,"model");
		if (tm == null) {
			return Objects.toString(data);
		} else {
			
			final ForEachStatus iterStatus = new AbstractForEachStatus(){//provide iteration status in this context
				private static final long serialVersionUID = 1L;
				@Override
				public int getIndex() {
					return index;
				}
				@Override
				public Object getEach(){
					return data;
				}
				@Override
				public Integer getEnd(){
					return size;
				}
			};
			
			final String var = (String) tm.getParameters().get(EACH_ATTR);
			final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
			final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
			final String itervarnm = itervar == null ? ( var==null?EACH_STATUS_VAR:varnm+STATUS_POST_VAR) : itervar; //provide default value if not specified
			
			final Component[] items = tm.create(owner, null,
				new VariableResolverX() {
					public Object resolveVariable(String name) {
						//shall never call here
						return varnm.equals(name) ? data : null;
					}	
					public Object resolveVariable(XelContext ctx, Object base, Object name) throws XelException {
						if (base == null) {
							if(varnm.equals(name)){
								return data;
							} else if(itervarnm.equals(name)){//iteration status
								return iterStatus;
							}
						}
						return null;
					}
				}, null);			
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
			addItemReference(lbl, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			lbl.setAttribute(itervarnm, iterStatus);

			//add template dependency
			addTemplateTracking(owner, lbl, data, index, size);

			//to force init and load
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, lbl));
			lbl.detach();
			return lbl.getValue();
		}
	}
}