/* BindSelectboxRenderer.java

	Purpose:
		
	Description:
		
	History:
		Dec 15, 2011 3:47:56 PM, Created by dennischen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

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
public class BindSelectboxRenderer implements ItemRenderer<Object> {
	@Override
	public String render(final Component owner, final Object data) throws Exception {
		final Template tm = owner.getTemplate("model");
		if (tm == null) {
			return Objects.toString(data);
		} else {
			//will call into BindUiLifeCycle#afterComponentAttached, and apply binding management there
			final String varnm = (String) owner.getAttribute(BinderImpl.VAR); //see BinderImpl#initRendererIfAny
			//TODO how to support this if there is no index directly, does look the model a stupid way?
//			final String itervarnm = (String) owner.getAttribute(BinderImpl.ITERATION_VAR); //see BinderImpl#initRendererIfAny
			
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
							}/*else if(itervarnm.equals(name)){//iteration status
								throw new UiException(itervarnm +" is not supported in selectbox binding");
							}*/
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
			final Label l = ((Label) items[0]);
			l.setAttribute(varnm, data);
			
			//to force init and load
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, l));
			final String val = l.getValue();
			l.detach();
			return val;
		}
	}
}