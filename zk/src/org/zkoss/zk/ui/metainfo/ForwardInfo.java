/* ForwardInfo.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 18 14:50:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;

/**
 * The forward directive in the ZUML page.
 *
 * <p>It is not serializable.
 *
 * @author tomyeh
 * @since 3.0.2
 */
public class ForwardInfo {
	private final ExValue _uri;
	private final ConditionImpl _cond;

	public ForwardInfo(String uri, ConditionImpl cond) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException();
		_uri = new ExValue(uri, String.class);
		_cond = cond;
	}

	/** Returns the URI to forward to.
	 */
	public String getURI() {
		return _uri.getRawValue();
	}
	/** Evaluates and returns the URI, or null if not to foward.
	 *
	 * <p>Note: if URI contains EL expressions and is evaluated to an empty
	 * string, this method returns null to indicate no need to forward.
	 */
	public String resolveURI(PageDefinition pgdef, Page page) {
		final Evaluator eval = pgdef.getEvaluator();
		if (_cond == null || _cond.isEffective(eval, page)) {
			final String uri = (String)_uri.getValue(eval, page);
			if (uri != null && uri.length() != 0)
				return uri;
		}
		return null;
	}
}
