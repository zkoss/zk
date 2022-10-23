/* ITreecolCtrl.java

	Purpose:
		
	Description:
		
	History:
		4:59 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Comparator;
import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zul.Treecol;

/**
 * An addition interface to {@link ITreecol}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreecolCtrl {
	static ITreecol from(Treecol instance) {
		ITreecol.Builder builder = new ITreecol.Builder().from((ITreecol) instance);
		List children = Immutables.proxyIChildren(instance);
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
