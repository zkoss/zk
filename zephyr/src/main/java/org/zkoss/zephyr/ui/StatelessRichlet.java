/* StatelessRichlet.java

	Purpose:

	Description:

	History:
		3:01 PM 2021/9/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui;

import java.io.Serializable;
import java.util.Collection;

import org.zkoss.zephyr.ui.http.DispatcherRichletFilter;
import org.zkoss.zephyr.ui.util.ActionHelper;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * A state less richlet interface to construct an immutable component tree to the given
 * view.
 * @author jumperchen
 */
public interface StatelessRichlet extends Richlet,
		Serializable {

	default void init(RichletConfig richletConfig) {
		// empty
	}

	default void destroy() {
		// empty
	}

	default void service(Page page) throws Exception {
		DispatcherRichletFilter dispatcherRichletFilter = (DispatcherRichletFilter) page.getDesktop().getWebApp()
				.getAttribute(DispatcherRichletFilter.class.getName());
		if (dispatcherRichletFilter != null) {
			Object owner = dispatcherRichletFilter.lookup(
									page.getRequestPath())
							.invoke(this, (Object[]) null);
			if (owner instanceof IComponent) {
				IStubComponent stubComponent = IStubComponent.of((IComponent) owner);
				stubComponent.setPage(page);
				ActionHelper.wireAction(this, stubComponent);
			} else if (owner instanceof IComponent[]) {
				IStubComponent firstOne = null;
				for (IComponent icom : (IComponent[]) owner) {
					IStubComponent stubComponent = IStubComponent.of(icom);
					stubComponent.setPage(page);
					if (firstOne == null) {
						firstOne = stubComponent;
					}
				}
				// wire action to one component is enough.
				if (firstOne != null) {
					ActionHelper.wireAction(this, firstOne);
				}
			} else if (owner instanceof Collection) {
				IStubComponent firstOne = null;
				for (IComponent icom : (Collection<IComponent>) owner) {
					IStubComponent stubComponent = IStubComponent.of(icom);
					stubComponent.setPage(page);
					if (firstOne == null) {
						firstOne = stubComponent;
					}
				}
				// wire action to one component is enough.
				if (firstOne != null) {
					ActionHelper.wireAction(this, firstOne);
				}
			}
			if (dispatcherRichletFilter.isCloudMode()) {
				Executions.getCurrent().addAuResponse(new AuScript("zephyr.cloudMode = true;"));
			}
		} else {
			throw new UiException("The config of DispatcherRichletFilter is not found!");
		}
	}

	default LanguageDefinition getLanguageDefinition() {
		return LanguageDefinition.lookup("zephyr/html");
	}
}
