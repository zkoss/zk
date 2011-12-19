/* BindSelectboxRenderer.java

	Purpose:
		
	Description:
		
	History:
		Dec 15, 2011 3:47:56 PM, Created by dennischen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.IterationStatus;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.Label;

/**
 * selectbox renderer.
 * @author dennischen
 *
 */
public class BindSelectboxRenderer implements ItemRenderer<Object>,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	@Override
	public String render(final Component owner, final Object data, final int index) throws Exception {
		final Template tm = owner.getTemplate("model");
		if (tm == null) {
			return Objects.toString(data);
		} else {
			//will call into BindUiLifeCycle#afterComponentAttached, and apply binding management there
			final String varnm = (String) owner.getAttribute(BinderImpl.VAR); //see BinderImpl#initRendererIfAny
			final String itervarnm = (String) owner.getAttribute(BinderImpl.ITERATION_VAR); //see BinderImpl#initRendererIfAny
			
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
								return new IterationStatus(){
									@Override
									public int getIndex() {
										return Integer.valueOf(index);
									}
								};
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
			lbl.setAttribute(varnm, data);
			
			lbl.setAttribute(itervarnm, new IterationStatus(){//provide iteration status in this context
				@Override
				public int getIndex() {
					return Integer.valueOf(index);
				}
			});
			
			//to force init and load
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, lbl));
			lbl.detach();
			return lbl.getValue();
		}
	}
}