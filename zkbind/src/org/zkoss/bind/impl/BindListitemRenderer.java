/* BindListitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:35:42 PM, Created by henrichen

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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
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
		final int size = listbox.getModel().getSize();
		final String tmn = item instanceof Listgroup?"model:group":item instanceof Listgroupfoot?"model:groupfoot":"model";
		final Template tm = resolveTemplate(listbox,item,data,index,size,tmn);
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
			Object oldVar = listbox.getAttribute(varnm);
			Object oldIter = listbox.getAttribute(itervarnm);
			listbox.setAttribute(varnm, data);
			listbox.setAttribute(itervarnm, iterStatus);
			
			final Component[] items = tm.create(listbox, item, null, null);
			
			listbox.setAttribute(varnm, oldVar);
			listbox.setAttribute(itervarnm, oldIter);
			
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Listitem nli = (Listitem)items[0];
			nli.setAttribute(BinderImpl.VAR, varnm); // for the converter to get the value
			addItemReference(listbox, nli, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			
			nli.setAttribute(itervarnm, iterStatus);
			
			//sync open state
			if (nli instanceof Listgroup && item instanceof Listgroup) {
				((Listgroup)nli).setOpen(((Listgroup)item).isOpen());
			}
			
			//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
			//move TEMPLATE_OBJECT (was set in resoloveTemplate) to current for check in addTemplateTracking
			nli.setAttribute(TemplateResolver.TEMPLATE_OBJECT, item.removeAttribute(TemplateResolver.TEMPLATE_OBJECT));
			//add template dependency
			addTemplateTracking(listbox, nli, data, index, size);
			
			if (nli.getValue() == null) //template might set it
				nli.setValue(data);
			item.setAttribute("org.zkoss.zul.model.renderAs", nli);
				//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
