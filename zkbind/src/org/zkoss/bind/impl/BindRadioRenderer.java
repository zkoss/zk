/* BindRadioRenderer.java

	Purpose:
		
	Description:
		
	History:
		Feb 3, 2012 3:35:42 PM, Created by dennis

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
		final Template tm = resoloveTemplate(radiogroup,item,data,index,size,"model");
		if (tm == null) {
			item.setLabel(Objects.toString(data));
			if(data instanceof String){//the value of radio will pass to client, (meaningful in html submit case)
				item.setValue((String)data);
			}
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
			
			final Component[] items = tm.create(radiogroup, item, 
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

			final Radio nr = (Radio)items[0];
			nr.setAttribute(BinderImpl.VAR, varnm); // for the converter to get the value
			addItemReference(nr, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			
			nr.setAttribute(itervarnm, iterStatus);
			
			//add template dependency
			addTemplateTracking(radiogroup, nr, data, index, size);
			
			if (nr.getValue() == null && data instanceof String) //template might set it
				nr.setValue((String)data);

			item.setAttribute("org.zkoss.zul.model.renderAs", nr);
			//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
