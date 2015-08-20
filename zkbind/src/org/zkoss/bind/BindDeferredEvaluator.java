/* BindDeferredEvaluator.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 14 12:24:31 CST 2015, Created by chunfu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.xel.DeferredEvaluator;
import org.zkoss.zk.xel.Evaluators;

/**
 * A binding implementation of {@link DeferredEvaluator}.
 * @author chunfu
 * @since 8.0.0
 */
public class BindDeferredEvaluator implements DeferredEvaluator {
	public void evaluate(Component comp, Object data) {
		//ZK-2831: evaluate deferred syntax (#{ })
		if (data instanceof Map) {
			Map<Property, String> deferredProps = (Map<Property, String>)data;
			Binder binder = BinderUtil.getBinder(comp, true);
			if (binder != null) {
				BindEvaluatorX eval = binder.getEvaluatorX();
				for (Map.Entry<Property, String> deferredProp : deferredProps.entrySet()) {
					Property prop = deferredProp.getKey();
					String value = deferredProp.getValue();

					Object o = Evaluators.evaluate(eval, comp, value, Object.class);

					prop.setDeferredVal(o); //to avoid evaluate value again
					prop.assign(comp);
				}
			}
		}
	}
}
