/* IColumnCtrl.java

	Purpose:

	Description:

	History:
		Tue Dec 28 14:45:02 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.Comparator;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zul.Column;

/**
 * An addition interface to {@link IColumn}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IColumnCtrl {
	static IColumn from(Column instance) {
		return new IColumn.Builder()
				.from((IColumn) instance)
				.build();
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