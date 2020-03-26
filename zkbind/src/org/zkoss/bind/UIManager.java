/* UIManager.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 30 12:46:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.tracker.impl.BindUiLifeCycle;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Callback;
import org.zkoss.zk.ui.util.Clients;

/**
 *
 * @author jameschu
 */
public class UIManager {
	private final Component _rootComponent;

	public UIManager(Object viewModel) {
		Map<Object, Component> vmCompRelationMap = (Map<Object, Component>) Executions.getCurrent().getDesktop()
				.getAttribute(BinderCtrl.VIEWMODEL_COMPONENT_MAP_KEY);
		_rootComponent = vmCompRelationMap.get(viewModel);
	}

	private Binder getBinder() {
		return (Binder) _rootComponent.getAttribute((String) _rootComponent.getAttribute(BindComposer.BINDER_ID));
	}

	public void scrollIntoView(Object bean) {
		if (Objects.equals(_rootComponent.getAttribute(BinderCtrl.ON_BIND_PROPERITIES_READY),true))
			Clients.scrollIntoView(getBinder().getBindingComponent(bean));
		else
			((ComponentCtrl) _rootComponent).addCallback(BinderCtrl.ON_BIND_PROPERITIES_READY, new Callback() {
				public void call(Object data) {
					try {
						scrollIntoView(bean);
					} catch (Exception e) {
						throw UiException.Aide.wrap(e);
					}
				}
			});
	}

	public void focus(Object bean) {
		if (Objects.equals(_rootComponent.getAttribute(BinderCtrl.ON_BIND_PROPERITIES_READY),true)) {
			Component component = getBinder().getBindingComponent(bean);
			if (component instanceof HtmlBasedComponent)
				((HtmlBasedComponent) component).setFocus(true);
		} else
			((ComponentCtrl) _rootComponent).addCallback(BinderCtrl.ON_BIND_PROPERITIES_READY, new Callback() {
				public void call(Object data) {
					try {
						focus(bean);
					} catch (Exception e) {
						throw UiException.Aide.wrap(e);
					}
				}
			});
	}
}
