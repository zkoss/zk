/* IListheaderCtrl.java

	Purpose:

	Description:

	History:
		Fri Jan 07 18:38:58 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.Comparator;
import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zul.Listheader;

/**
 * An addition interface to {@link IListheader}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListheaderCtrl {
	static IListheader from(Listheader instance) {
		ImmutableIListheader.Builder builder = new IListheader.Builder().from((IListheader) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}

	static Comparator<?> toComparator(String clsnm)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (clsnm == null || clsnm.length() == 0)
			return null;

		Page page = null;
		if (Executions.getCurrent() != null) {
			page = ((ExecutionCtrl) Executions.getCurrent()).getCurrentPage();
		}

		final Class cls = page != null ? page.resolveClass(clsnm) : Classes.forNameByThread(clsnm);
		if (cls == null)
			throw new ClassNotFoundException(clsnm);
		if (!Comparator.class.isAssignableFrom(cls))
			throw new UiException("Comparator must be implemented: " + clsnm);
		return (Comparator<?>) cls.newInstance();
	}
}