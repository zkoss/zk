/* BindChildRenderer.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ShadowElementsCtrl;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;

/**
 * to renderer children of component
 * @author dennis
 * @since 6.0.0
 */
public class BindChildRenderer extends AbstractRenderer {
	private static final long serialVersionUID = 1L;

	public BindChildRenderer(){
		setAttributeName(AnnotateBinderHelper.CHILDREN_KEY);
	}
	
	public void render(final Component owner, final Object data, final int index, final int size) {
		render(owner, data, index, size, false);
	}

	protected void recordRenderedIndex(Component owner, int itemSize) {
		List<Component[]> indexList = (List<Component[]>) owner
				.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
		if (indexList == null) {
			super.recordRenderedIndex(owner, itemSize);
		}
	}

	protected int getRenderedIndex(Component owner, int childIndex) {
		List<Component[]> indexList = (List<Component[]>) owner
				.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
		if (indexList == null) {
			return super.getRenderedIndex(owner, childIndex);
		} else {
			if (childIndex == 0) return 0; // speed up
			if (indexList != null) {
				int index = 0;
				int currentIndex = 0;
				for (Component[] list : indexList) {
					if ((currentIndex += list.length) > childIndex)
						break;
					index++;
				}
				return index;
			}
			return childIndex;
		}
	}
	@SuppressWarnings("unchecked")
	public void render(final Component owner, final Object data, final int index, final int size, final boolean isListModel){
		final Template tm = resolveTemplate(owner,owner,data,index,size,"children");
		if (tm == null) {
			Label l = new Label(data==null?"":data.toString());
			l.setParent(owner);
			return;
		}
		
		final ForEachStatus iterStatus = new ChildrenBindingForEachStatus(index, data, size);//provide iteration status in this context
		
		final String var = (String) tm.getParameters().get("var");
		final String varnm = var == null ? EACH_VAR : var; //var is not specified, default to "each"
		
		final String itervar = (String) tm.getParameters().get(STATUS_ATTR);
		final String itervarnm = itervar == null ? ( var==null?EACH_STATUS_VAR:varnm+STATUS_POST_VAR) : itervar; //provide default value if not specified

		//bug 1188, EL when nested var and itervar
		Object oldVar = owner.getAttribute(varnm);
		Object oldIter = owner.getAttribute(itervarnm);
		owner.setAttribute(varnm, data);
		owner.setAttribute(itervarnm, iterStatus);

		// For bug ZK-2552
		Component insertBefore = null;
		List<Component[]> cbrCompsList = (List<Component[]>) owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
		if (cbrCompsList != null) {
			int newIndex = 0;
			int steps = 0;
			for (Component[] cmps : cbrCompsList) {
				if (steps++ >= index)
					break;
				newIndex += cmps.length;
			}
			if (owner.getChildren().size() > newIndex) {
				insertBefore = owner.getChildren().get(newIndex);
			} 
		}
		final Component[] items = ShadowElementsCtrl.filterOutShadows(tm.create(owner, insertBefore, null, null));
		
		// Bug ZK-2789: do not use setAttribute when actually trying to removeAttribute
		if (oldVar != null) {
			owner.setAttribute(varnm, oldVar);
		} else {
			owner.removeAttribute(varnm);
		}
		if (oldIter != null) {
			owner.setAttribute(itervarnm, oldIter);
		} else {
			owner.removeAttribute(itervarnm);
		}
		

		boolean templateTracked = false;
		
		//ZK-2545 - Children binding support list model
		if (isListModel) {
			cbrCompsList = (List<Component[]>) owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
			if (cbrCompsList == null) cbrCompsList = new LinkedList<Component[]>();
			cbrCompsList.add(items);
			owner.setAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS, cbrCompsList);
		} else {
			recordRenderedIndex(owner, items.length);
		}
		
		for(final Component comp: items){
			comp.setAttribute(BinderCtrl.VAR, varnm);
			// ZK-2552
			comp.setAttribute(AbstractRenderer.IS_TEMPLATE_MODEL_ENABLED_ATTR, true);
			comp.setAttribute(AbstractRenderer.CURRENT_INDEX_RESOLVER_ATTR, new IndirectBinding(data) {
				public Binder getBinder() {
					return BinderUtil.getBinder(comp, true);
				}

				public Component getComponent() {
					return comp;
				}

				protected ListModel getModel() {
					return null;
				}

				public void setValue(BindELContext ctx, Object value) {
					Collection<?> collection = (Collection<?>) owner.getAttribute(BindELContext.getModelName(owner));
					if (collection instanceof List<?>) {
						List<Object> list = (List<Object>) collection;
						int index = list.indexOf(data);
						try {
							list.set(index, value);
							setData(value);
						} catch (UnsupportedOperationException e) {
							throw new PropertyNotWritableException(e);
						} catch (IndexOutOfBoundsException e) {
							throw new PropertyNotFoundException(e);
						}
					}
				}
			});

			addItemReference(owner, comp, index, varnm); //kept the reference to the data, before ON_BIND_INIT
			comp.setAttribute(itervarnm, iterStatus);
			
			//add template dependency
			if (!templateTracked) {
				//ZK-1787 When the viewModel tell binder to reload a list, the other component that bind a bean in the list will reload again
				//move TEMPLATE_OBJECT (was set in resoloveTemplate) to current for check in addTemplateTracking
				Object obj = owner.removeAttribute(TemplateResolver.TEMPLATE_OBJECT);
				// Bug ZK-2789: do not use setAttribute when actually trying to removeAttribute
				if (obj != null) {
					comp.setAttribute(TemplateResolver.TEMPLATE_OBJECT, obj);
				} else {
					comp.removeAttribute(TemplateResolver.TEMPLATE_OBJECT);
				}
				addTemplateTracking(owner, comp, data, index, size);
				templateTracked = true;
			}
			
			//to force init and load
			Events.sendEvent(new Event(BinderCtrl.ON_BIND_INIT, comp));
		}
	}

	private class ChildrenBindingForEachStatus extends AbstractForEachStatus {
		private static final long serialVersionUID = 1L;
		
		private int index;
		private transient Object data;
		private Integer size;
		
		public ChildrenBindingForEachStatus(int index, Object data, Integer size) {
			this.index = index;
			this.data = data;
			this.size = size;
		}
		
		public int getIndex() {
			return index;
		}
		
		public Object getCurrent(){
			return data;
		}
		
		public Integer getEnd(){
			return size;
		}
	}
}
