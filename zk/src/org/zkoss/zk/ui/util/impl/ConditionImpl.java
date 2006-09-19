/* ConditionImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  2 22:41:20     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util.impl;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.util.Condition;

/**
 * An implementation of {@link Condition}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ConditionImpl implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 20060622L;

	private String _if;
	private String _unless;

	/** Returns an instance of Condition, or null if both ifc and unless
	 * are empty.
	 * In other words, it is useful if you use null or empty to denote true.
	 */
	public static Condition getInstance(String ifc, String unless) {
		if ((ifc == null || ifc.length() == 0)
		&& (unless == null || unless.length() == 0))
			return null;
		return new ConditionImpl(ifc, unless);
	}
	/** Construct.
	 * In most cases, use {@link #getInstance} instead of this constructor.
	 */
	public ConditionImpl(String ifc, String unless) {
		setIf(ifc);
		setUnless(unless);
	}

	/** Sets the if condition.
	 * @see #isEffective
	 * @param cond the condition.
	 * Note: If null (not specified), it is considered effective.
	 */
	public void setIf(String cond) {
		_if = cond != null && cond.length() > 0 ? cond: null;
	}
	/** Sets the unless condition.
	 * @see #isEffective
	 * @param cond the condition.
	 * Note: If null (not specified), it is considered ineffective.
	 */
	public void setUnless(String cond) {
		_unless = cond != null && cond.length() > 0 ? cond: null;
	}

	//Condition//
	public boolean isEffective(Component comp) {
		try {
			return (_if == null
				|| ((Boolean)Executions.evaluate(comp, _if, Boolean.class)).booleanValue())
			&& (_unless == null
				|| !((Boolean)Executions.evaluate(comp, _unless, Boolean.class)).booleanValue());
		} catch (ClassCastException ex) {
			throw new UiException("An boolean expression is required: "+ex.getMessage(), ex);
		}
	}
	public boolean isEffective(Page page) {
		try {
			return (_if == null
				|| ((Boolean)Executions.evaluate(page, _if, Boolean.class)).booleanValue())
			&& (_unless == null
				|| !((Boolean)Executions.evaluate(page, _unless, Boolean.class)).booleanValue());
		} catch (ClassCastException ex) {
			throw new UiException("An boolean expression is required: "+ex.getMessage(), ex);
		}
	}
}
