/* ChildrenBindingListDataListener.java

	Purpose:
		
	Description:
		
	History:
		2015/01/16 Created by James Chu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

/**
 * listen the model data onChange event (support list model in children binding)
 * @author James Chu
 * @since 8.0.0
 */
public class ChildrenBindingListDataListener implements ListDataListener, java.io.Serializable{
	private static final long serialVersionUID = 20150116151900L;
	private final Component _owner;
	private final BindContext _ctx;
	private final Converter<?, ListModel<?>, Component> _conv;
	
	public ChildrenBindingListDataListener(Component comp, BindContext ctx, Converter<?, ListModel<?>, Component> conv) {
		this._owner = comp;
		this._ctx = ctx;
		this._conv = conv;
	}
	
	public void onChange(ListDataEvent event) {
		onListModelDataChange(new ChildrenBindingListModelDataEvent(event));
	}
	
	@SuppressWarnings("unchecked")
	private void onListModelDataChange(ListDataEvent event) {
		final ListModel<?> model = event.getModel();
		int type = event.getType();
		int index0 = event.getIndex0();
		int index1 = event.getIndex1();
		List<Component[]> cbrCompsList = (List<Component[]>) _owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
		int oldsz = cbrCompsList == null ? 0 : cbrCompsList.size();
		int newsz = model.getSize();
		boolean refreshOwnerCBAttr = false;
		if (type == ListDataEvent.INTERVAL_ADDED) {
			int addedCount = index1 - index0 + 1;
			if ((newsz - oldsz) < 0)
				throw new UiException("Adding causes a smaller list?");
			else if ((oldsz + addedCount) != newsz) { //check live data changed
				index0 = oldsz;
				index1 = newsz - 1;
			}
			renderModelData(model, index0, index1);
		} else if (type == ListDataEvent.CONTENTS_CHANGED) {
			if (index0 >= 0 && index1 >=0) {
				renderModelData(model, index0, index1);
				cbrCompsList = (List<Component[]>) _owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
				for (int i = 0; i <= (index1 - index0); i++) {
					//replace old with new
					int oIndex = index0 + i;
					Component[] oldComps = cbrCompsList.get(oIndex);
					int nIndex = oldsz + i;
					Component[] newComps = cbrCompsList.get(nIndex);
					for (Component nc : newComps)
						_owner.insertBefore(nc, oldComps[0]);
					for (Component oc : oldComps)
						oc.detach();
					cbrCompsList.add(oIndex, newComps);
					cbrCompsList.remove(oIndex + 1);
					cbrCompsList.remove(nIndex);
				}
				refreshOwnerCBAttr = true;
			} else //sync model
				syncModel(model);
		} else if (type == ListDataEvent.INTERVAL_REMOVED) {
			if (oldsz - newsz <= 0)
				throw new UiException("Removal causes a larger list?");
			for (int i = index0; i <= index1; i++) {
				Component[] oldComps = cbrCompsList.get(index0);
				if (oldComps != null) {
					for (Component oc : oldComps) {
						oc.detach();
					}
				}
				cbrCompsList.remove(index0);
			}
			refreshOwnerCBAttr = true;
		}
		if (refreshOwnerCBAttr) 
			_owner.setAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS, cbrCompsList);
	}
	
	@SuppressWarnings("unchecked")
	private void syncModel(ListModel<?> model) {
		//clear all
		List<Component[]> cbrCompsList = (List<Component[]>) _owner.getAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
		if (cbrCompsList != null) {
			_owner.removeAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
			for (Component[] oldComps : cbrCompsList) {
				if (oldComps != null) {
					for (Component oc : oldComps) {
						oc.detach();
					}
				}
			}
		}
		renderModelData(model, 0, model.getSize() - 1);
	}
	
	@SuppressWarnings("unchecked")
	private void renderModelData(ListModel<?> model, int from, int to) {
		BindChildRenderer renderer = new BindChildRenderer();
		if (_conv != null) {
			List<Object> data = (List<Object>) _conv.coerceToUi(model, _owner, _ctx);
			BindELContext.addModel(_owner, data);
			if (data != null) {
				int size = data.size();
				if (to >= size) to = size - 1;
				for(int i = from; i <= to; i++)
					renderer.render(_owner, data.get(i), i, size, true);
			}
		}
	}
}
