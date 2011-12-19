/* BindComboitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:47:56 PM, Created by henrichen

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

/**
 * comboitem renderer for binding.
 * @author henrichen
 *
 */
public class BindComboitemRenderer implements ComboitemRenderer<Object>, Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	
	public void render(final Comboitem item, final Object data) throws Exception {
		final Combobox cb = (Combobox)item.getParent();
		final Template tm = cb.getTemplate("model");
		if (tm == null) {
			item.setLabel(Objects.toString(data));
			item.setValue(data);
		} else {
			//will call into BindUiLifeCycle#afterComponentAttached, and apply binding management there
			final String varnm = (String) cb.getAttribute(BinderImpl.VAR); //see BinderImpl#initRendererIfAny
			final String itervarnm = (String) cb.getAttribute(BinderImpl.ITERATION_VAR); //see BinderImpl#initRendererIfAny
			final Component[] items = tm.create(cb, item,
				new VariableResolverX() {
					public Object resolveVariable(String name) {
						//shall never call here
						return varnm.equals(name) ? data : null;
					}
	
					public Object resolveVariable(XelContext ctx, Object base, Object name) throws XelException {
						if (base == null) {
							if(varnm.equals(name)){
								return data;
							}else if(itervarnm.equals(name)){//iteration status
								return new IterationStatus(){
									@Override
									public int getIndex() {
										return Integer.valueOf(item.getIndex());
									}
								};
							}
						}
						return null;
					}
				}, null);
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Comboitem nci = (Comboitem)items[0];
			nci.setAttribute(varnm, data); //kept the value
			
			nci.setAttribute(itervarnm, new IterationStatus(){//provide iteration status in this context
				@Override
				public int getIndex() {
					return Integer.valueOf(nci.getIndex());
				}
			});
			
			if (nci.getValue() == null) //template might set it
				nci.setValue(data);
			item.setAttribute("org.zkoss.zul.model.renderAs", nci);
				//indicate a new item is created to replace the existent one
			item.detach();
			
			//bug #ZK-677: combobox selection is lost after reload model
			//binding Comboitem immediately, @see BindUiLifeCycle#afterComponentAttached
			Events.sendEvent(new Event(BinderImpl.ON_BIND_INIT, nci));
		}
	}
}
