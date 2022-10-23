/* GetterMethodHandler.java

	Purpose:

	Description:

	History:
		5:03 PM 2021/9/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

import org.zkoss.zephyr.immutable.ZephyrOnly;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IPagingCtrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Area;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Track;
import org.zkoss.zul.Tree;
import org.zkoss.zul.impl.InputElement;

/**
 * A getter method handle for {@link Component} to {@link org.zkoss.zephyr.zpr.IComponent}.
 *
 * @author jumperchen
 */
public class GetterMethodHandlerForC2I implements MethodHandler, Serializable {
	protected static MethodFilter GETTER_METHOD_FILTER = m -> (isAttribute(m));
	protected Component _origin;

	public GetterMethodHandlerForC2I(Component origin) {
		_origin = origin;
	}

	final static Map<String, String> transferTable = new HashMap<>(); // ClassName.Method: Alias
	final static Map<String, Object> ignoreTable = new HashMap<>(); // ClassName.Method: ReturnValue

	static {
		transferTable.put("org.zkoss.zkmax.zul.Dropupload.isBinary", "isNative");
		transferTable.put(Tree.class.getName() + ".getAutosort", "isAutosort");
		transferTable.put(Grid.class.getName() + ".getAutosort", "isAutosort");
		transferTable.put(Listbox.class.getName() + ".getAutosort", "isAutosort");
		transferTable.put(Track.class.getName() + ".isAsDefault", "isDefault");
		transferTable.put("org.zkoss.zkmax.zul.Cropper.getCrossorigin", "getCrossOrigin");
		transferTable.put(Tabpanel.class.getName() + ".isVisible", "isRawVisible");

		ignoreTable.put(Panelchildren.class.getName() + ".getVflex", null); // default value is null
		ignoreTable.put(Panelchildren.class.getName() + ".getHflex", null); // default value is null
	}

	public Object invoke(Object self, Method method, Method proceed,
			Object[] args) throws Exception {
		try {
			final String mname = method.getName();
			final String queryName = _origin.getClass().getName() + "." + mname;

			// ignore some getter, such as Panelchildren.getVflex()/getHflex()
			if (ignoreTable.containsKey(queryName)) {
				return ignoreTable.get(queryName);
			}

			final String aliasName = transferTable.get(queryName);
			if (aliasName != null) {
				Method fromMethod = null;
				try {
					// lookup all public inherited method if any.
					fromMethod = _origin.getClass().getMethod(aliasName);
				} catch (Throwable t) {
					// try the class method in any modifier
					fromMethod = _origin.getClass().getDeclaredMethod(aliasName);
					fromMethod.setAccessible(true);
				}
				try {
					Object result = fromMethod.invoke(_origin, args);
					if (fromMethod.getReturnType() != method.getReturnType()) {
						Method valueOf = method.getReturnType()
								.getMethod("valueOf", fromMethod.getReturnType());
						return valueOf.invoke(fromMethod.getReturnType(), result);
					} else {
						return result;
					}
				} finally {
					if (!Modifier.isPublic(fromMethod.getModifiers())) {
						fromMethod.setAccessible(false);
					}
				}
			}
			switch (mname) {
			case "getZclass":
				// avoid test case fail, we assume the returned value is the same as zk component.
				if (_origin instanceof Space) return null; // Space getZclass() is inherited from Separator.
				if (_origin instanceof Fileupload) return null; // Fileupload getZclass() is inherited from Button.
				if (_origin instanceof HtmlBasedComponent) {
					String comopnentName = _origin.getClass().getSimpleName().toLowerCase();
					String zclass = ((HtmlBasedComponent) _origin).getZclass();
					if (("z-" + comopnentName).equals(zclass)) {
						return null;
					} else {
						return zclass;
					}
				}
				return null;
			case "getTabindex":
				if (_origin instanceof HtmlBasedComponent) {
					return ((HtmlBasedComponent) _origin).getTabindexInteger();
				} else if (_origin instanceof Area) {
					return ((Area) _origin).getTabindexInteger();
				} else {
					return null;
				}
			case "getChild":
				return null;
			// To avoid addAllChildren ClassCastException in builder().from().
			case "getChildren":
				return Collections.emptyList();
			case "getZKChildren":
				//setChildren will be call by getZKChildren in IXXXCtrl.
				return _origin.getChildren();
			case "getPageSize":
				// To avoid pgi() IllegalStateException in builder().from().
				if (!(_origin instanceof Paging) && _origin.getMold() != "paging")
					return IPagingCtrl.PAGE_SIZE;
				break;
			case "getWidgetListeners":
				Set<String> widgetListenerNames = _origin.getWidgetListenerNames();
				if (widgetListenerNames.isEmpty()) {
					return null;
				} else {
					Map result = new LinkedHashMap<>();
					for (String name : widgetListenerNames) {
						result.put(name, _origin.getWidgetListener(name));
					}
					return result;
				}
			case "getWidgetOverrides":
				Set<String> widgetOverrideNames = _origin.getWidgetOverrideNames();
				if (widgetOverrideNames.isEmpty()) {
					return null;
				} else {
					Map result = new LinkedHashMap<>();
					for (String name : widgetOverrideNames) {
						result.put(name, _origin.getWidgetOverride(name));
					}
					return result;
				}
			case "getClientAttributes":
				Set<String> widgetAttributeNames = _origin.getWidgetAttributeNames();
				if (widgetAttributeNames.isEmpty()) {
					return null;
				} else {
					Map result = new LinkedHashMap<>();
					for (String name : widgetAttributeNames) {
						result.put(name, _origin.getClientAttribute(name));
					}
					return result;
				}
			case "isFocus": // client widget only.
				return false;
			case "getConstraint":
				if (_origin instanceof InputElement) {
					return ((InputElement) _origin).getConstraintString();
				}
				break;
			}

			if (Modifier.isPublic(method.getModifiers())) {
				if (IComponent.class.isAssignableFrom(method.getReturnType())) {
					Object result = _origin.getClass().getMethod(method.getName()).invoke(_origin, args);
					return result != null ? Immutables.proxyIComponent((Component) result) : null;
				} else if (method.getAnnotation(ZephyrOnly.class) != null) {
					return proceed != null ? // invoke default method if any
							proceed.invoke(self, args) :
							ActionHandler.DEFAULT_VALUES.getOrDefault(
									method.getReturnType(),
									null); // ignore for Zephyr internal use method
				}
				if (method.getDeclaringClass().isInstance(_origin)) {
					return method.invoke(_origin, args);
				} else {
					// fixing the duplicated method name issue for IInputElement#getValue()
					return _origin.getClass().getMethod(method.getName()).invoke(_origin, args);
				}
			} else {
				// some method is not public, such as MeshElement.isNativeScrollbar()
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				return method.invoke(_origin, args);
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}

	public static boolean isAttribute(Method method) {
		if (!Modifier.isPublic(method.getModifiers()))
			return false;

		final String nm = method.getName();
		final int len = nm.length();
		switch (method.getParameterTypes().length) {
		case 0:
			if (len >= 3 && nm.startsWith("is"))
				return true;
			return len >= 4 && nm.startsWith("get");
		default:
			return false;
		}
	}
}