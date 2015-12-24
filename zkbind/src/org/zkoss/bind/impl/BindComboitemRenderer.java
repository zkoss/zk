/* BindComboitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:47:56 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Attributes;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListSubModel;

/**
 * comboitem renderer for binding.
 * @author henrichen
 * @since 6.0.0
 */
public class BindComboitemRenderer extends AbstractRenderer implements ComboitemRenderer<Object>{
	private static final long serialVersionUID = 1463169907348730644L;
	
	public void render(final Comboitem item, final Object data, final int index)
	throws Exception {
		final Combobox cb = (Combobox)item.getParent();
		final ListModel<?> model = cb.getModel();
		final int size = model.getSize();
		final Template tm = resolveTemplate(cb,item,data,index,size,"model");
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
			Object oldVar = cb.getAttribute(varnm);
			Object oldIter = cb.getAttribute(itervarnm);
			cb.setAttribute(varnm, data);
			cb.setAttribute(itervarnm, iterStatus);
			
			final Component[] items = filterOutShadows(cb, tm.create(cb, item, null, null));

			// Bug ZK-2882
			if (oldVar == null) {
				cb.removeAttribute(varnm);
			} else {
				cb.setAttribute(varnm, oldVar);
			}
			if (oldIter == null) {
				cb.removeAttribute(itervarnm);
			} else {
				cb.setAttribute(itervarnm, oldIter);
			}
			
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Comboitem nci = (Comboitem)items[0];
			nci.setAttribute(BinderCtrl.VAR, varnm); // for the converter to get the value
			
			if(model instanceof ListSubModel){
				//ZK-992 wrong item when binding to combbox with submodel implementation
				//combobox has a internal model as the submodel, 
				//I don't have way to access the submodel, and user doesn't has info to notify model[index] changed.
				//so I set the value directly.
				nci.setAttribute(varnm, data);
			}else{
				// ZK-2552
				recordRenderedIndex(cb, items.length);

				nci.setAttribute(AbstractRenderer.IS_TEMPLATE_MODEL_ENABLED_ATTR, true);
				nci.setAttribute(AbstractRenderer.CURRENT_INDEX_RESOLVER_ATTR, new IndirectBinding() {
					public Binder getBinder() {
						return BinderUtil.getBinder(nci, true);
					}

					@SuppressWarnings("unchecked")
					public void setValue(BindELContext ctx, Object value) {
						ListModel<?> listmodel = cb.getModel();
						int idx = ((ListModelArray<Object>) listmodel).indexOf(data);

						if (listmodel instanceof ListModelArray){
							((ListModelArray<Object>)listmodel).set(idx, value);
						} else if(listmodel instanceof ListModelList<?>){
							((ListModelList<Object>)listmodel).set(idx, value);
						}
					}
					
					public Component getComponent() {
						return nci;
					}

					public Object getValue(BindELContext ctx) {
						return data;
					}
				});
				addItemReference(cb, nci, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			}
			
			nci.setAttribute(itervarnm, iterStatus);
			
			//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
			//move TEMPLATE_OBJECT (was set in resoloveTemplate) to current for check in addTemplateTracking
			nci.setAttribute(TemplateResolver.TEMPLATE_OBJECT, item.removeAttribute(TemplateResolver.TEMPLATE_OBJECT));
			//add template dependency
			addTemplateTracking(cb, nci, data, index, size);
			
			if (nci.getValue() == null) //template might set it
				nci.setValue(data);
			item.setAttribute(Attributes.MODEL_RENDERAS, nci);
				//indicate a new item is created to replace the existent one
			item.detach();
			
			//bug #ZK-677: combobox selection is lost after reload model
			//binding Comboitem immediately, @see BindUiLifeCycle#afterComponentAttached
			Events.sendEvent(new Event(BinderCtrl.ON_BIND_INIT, nci));
		}
	}
}
