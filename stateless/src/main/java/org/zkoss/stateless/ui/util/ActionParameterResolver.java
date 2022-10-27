/* ActionParameterResolver.java

	Purpose:

	Description:

	History:
		2:36 PM 2021/12/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.stateless.sul.IComponentCtrl;
import org.zkoss.stateless.action.data.ActionData;
import org.zkoss.stateless.action.data.RequestData;
import org.zkoss.stateless.action.data.RequestDataFactory;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.Self;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.zk.ui.Executions;

/**
 * Action handler's parameter resolver.
 * @author jumperchen
 */
public class ActionParameterResolver {

	public static Object[] resolve(String uuid, Map requestData,  Class<?>[] types) {
		if (types.length == 0) return new Object[0];
		List argData = (List) requestData.remove(ActionData.ARGUMENTS);
		Object[] args = new Object[types.length];
		for (int i = 0, j = types.length; i < j; i++) {
			Class<?> type = types[i];
			if (IComponentCtrl.isBuiltinType(type)) {
				if (Self.class.isAssignableFrom(type)) {
					args[i] = Locator.of(uuid);
				} else if (UiAgent.class.isAssignableFrom(type)) {
					args[i] = UiAgent.of(Executions.getCurrent());
				} else if (ActionData.class.isAssignableFrom(type)) {
					if (argData != null) {
						Object actionData = argData.get(i);
						args[i] = ObjectMappers.convert(mergeMap(requestData, actionData), type);
					} else if (type == RequestData.class) {
						args[i] = RequestDataFactory.newInstance(requestData);
					} else {
						args[i] = ObjectMappers.convert(requestData, type);
					}
				} else {
					throw new UnsupportedOperationException("Not supported yet");
				}
			} else {
				Object o = argData.get(i);
				if (o == null) {
					args[i] = null;
				} else {
					args[i] = ObjectMappers.convert(o, type);
				}
			}
		}
		return args;
	}


	private static Map mergeMap(Map m1, Object m2) {
		if (m2 instanceof Map) {
			Map result = new HashMap<>(m1);
			result.putAll((Map) m2);
			return result;
		} else {
			return m1;
		}
	}
}
