/* BindListitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:35:42 PM, Created by henrichen

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
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Listitem renderer for binding.
 * @author henrichen
 * @since 6.0.0
 */
public class BindListitemRenderer extends AbstractRenderer implements ListitemRenderer<Object>,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	
	public void render(final Listitem item, final Object data, final int index)
	throws Exception {
		final Listbox listbox = (Listbox)item.getParent();
		final Template tm = resoloveTemplate(listbox,item,data,index,"model");
		if (tm == null) {
			item.setLabel(Objects.toString(data));
			item.setValue(data);
		} else {
			final IterationStatus iterStatus = new AbstractIterationStatus(){//provide iteration status in this context
				private static final long serialVersionUID = 1L;
				@Override
				public int getIndex() {
					return index;
				}
			};
			
			final String var = (String) tm.getParameters().get(EACH_ATTR);
			final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
			final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
			final String itervarnm = itervar == null ? varnm+STATUS_POST_VAR : itervar; //provide default value if not specified
			
			final Component[] items = tm.create(listbox, item, 
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
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Listitem nli = (Listitem)items[0];
			nli.setAttribute(BinderImpl.VAR, varnm); // for the converter to get the value
			addItemReference(nli, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			
			nli.setAttribute(itervarnm, iterStatus);
			
			//add template dependency
			addTemplateTracking(listbox, nli, data, index);
			
			if (nli.getValue() == null) //template might set it
				nli.setValue(data);
			item.setAttribute("org.zkoss.zul.model.renderAs", nli);
				//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
