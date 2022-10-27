/* StatelessComposer.java

	Purpose:
		
	Description:
		
	History:
		2:22 PM 2021/9/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import org.zkoss.stateless.ui.util.ActionHelper;
import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Composer;

/**
 * A state less composer to compose of ZK {@link Component}s into Stateless immutable {@link IComponent}s
 * @author jumperchen
 */
public interface StatelessComposer<I extends IComponent>
		extends Composer, IComponentBuilder<I>, java.io.Serializable {

	/**
	 * Marks as final implementation for subclass; Instead, implements {@link IComponentBuilder#build(BuildContext)}
	 * for building an immutables components tree to the given view if any.
	 * @param t
	 * @throws Exception
	 */
	default void doAfterCompose(Component t) throws Exception {
		I owner = Immutables.proxyIComponent(t);
		I icomp = build(BuildContext.newInstance(owner));
		if (icomp != owner) {
			// replace to the owner if any.
			Component tParent = t.getParent();
			IStubComponent iStubComponent = IStubComponent.of(icomp);

			// root component
			if (tParent == null) {
				Page tPage = t.getPage();
				t.detach();
				iStubComponent.setPage(tPage);
			} else {
				Component tNext = t.getNextSibling();
				tParent.insertBefore(iStubComponent, tNext);
				t.detach();
			}
			ActionHelper.wireAction(this, iStubComponent);
		}
	}

}
