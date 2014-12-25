/* BindRadioRenderer.java

	Purpose:
		
	Description:
		
	History:
		Feb 3, 2012 3:35:42 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Radio;
import org.zkoss.zul.RadioRenderer;
import org.zkoss.zul.Radiogroup;

/**
 * radio renderer for binding.
 * @author dennis
 * @since 6.0.0
 */
public class BindRadioRenderer extends AbstractRenderer implements RadioRenderer<Object>,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	
	public void render(final Radio item, final Object data, final int index)
	throws Exception {
		final Radiogroup radiogroup = item.getRadiogroup();
		final int size = radiogroup.getModel().getSize();
		final Template tm = resolveTemplate(radiogroup,item,data,index,size,"model");
		if (tm == null) {
			item.setLabel(Objects.toString(data));
			item.setValue(data);
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
			Object oldVar = radiogroup.getAttribute(varnm);
			Object oldIter = radiogroup.getAttribute(itervarnm);
			radiogroup.setAttribute(varnm, data);
			radiogroup.setAttribute(itervarnm, iterStatus);
			
			final Component[] items = tm.create(radiogroup, item, null, null);
			
			radiogroup.setAttribute(varnm, oldVar);
			radiogroup.setAttribute(itervarnm, oldIter);
			
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Radio nr = (Radio)items[0];
			nr.setAttribute(BinderImpl.VAR, varnm); // for the converter to get the value
			addItemReference(radiogroup, nr, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			
			nr.setAttribute(itervarnm, iterStatus);
			
			//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
			//move TEMPLATE_OBJECT (was set in resoloveTemplate) to current for check in addTemplateTracking
			nr.setAttribute(TemplateResolver.TEMPLATE_OBJECT, item.removeAttribute(TemplateResolver.TEMPLATE_OBJECT));
			//add template dependency
			addTemplateTracking(radiogroup, nr, data, index, size);
			
			if (nr.getValue() == null) //template might set it
				nr.setValue(data);

			item.setAttribute("org.zkoss.zul.model.renderAs", nr);
			//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
