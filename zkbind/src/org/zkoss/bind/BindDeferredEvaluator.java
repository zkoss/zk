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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.xel.DeferredEvaluator;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.Evaluators;

/**
 * A binding implementation of {@link DeferredEvaluator}.
 * @author chunfu
 * @since 8.0.0
 */
public class BindDeferredEvaluator implements DeferredEvaluator {
	public void evaluate(Component comp, Object data) {
		//ZK-2831: evaluate deferred syntax (#{ })
		if (data instanceof Class) { //data is expected type
			Map<Property, String> deferredProps = (Map<Property, String>)comp.getAttribute(Attributes.DEFERRED_PROPERTIES);
			Binder binder;
			ShadowElement se = null;
			if (comp instanceof ShadowElement) {
				se = (ShadowElement) comp;
				binder = BinderUtil.getBinder(se.getShadowHost(), true);
			} else {
				binder = BinderUtil.getBinder(comp, true);
			}
			Evaluator eval = binder == null ? Executions.getEvaluator(comp, null) : binder.getEvaluatorX();
			for (Map.Entry<Property, String> deferredProp : deferredProps.entrySet()) {
				Property prop = deferredProp.getKey();
				String value = deferredProp.getValue();

				Object o = Evaluators.evaluate(eval, comp, value, (Class) data);

				prop.setDeferredVal(o); //to avoid evaluate value again
				prop.assign(comp);
			}
			if (se != null) { //if comp is shadow, trigger recreate
				se.recreate();
			}
		} else if (comp instanceof Native && "prolog".equals(data.toString())) {
			Native nc = (Native) comp;
			String prolog = nc.getPrologContent();
			if (prolog != null && prolog.contains("#{")) {
				Binder binder = BinderUtil.getBinder(comp, true);
				Evaluator eval = binder == null ? Executions.getEvaluator(comp, null) : binder.getEvaluatorX();

				Object o = Evaluators.evaluate(eval, comp, prolog, String.class);

				nc.setPrologContent(o.toString());
			}
		}
	}
}
