/* Immutables.java

	Purpose:

	Description:

	History:
		2:00 PM 2021/9/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import static org.reflections.ReflectionUtils.Methods;
import static org.reflections.util.ReflectionUtilsPredicates.withModifier;
import static org.reflections.util.ReflectionUtilsPredicates.withParametersCount;
import static org.reflections.util.ReflectionUtilsPredicates.withPrefix;
import static org.reflections.util.ReflectionUtilsPredicates.withReturnTypeAssignableFrom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * An immutables utils.
 * @author jumperchen
 */
public final class Immutables {
	private static final Logger _log = LoggerFactory.getLogger(Immutables.class);

	private static Map _methodCache = new ConcurrentHashMap();

	private static final String ZEPHYR_PACKAGE = "org.zkoss.zephyr.zpr.I";

	private static final String ZEPHYREX_PACKAGE = "org.zkoss.zephyrex.zpr.I";

	/**
	 * Makes a proxy of {@link Component} into {@link IComponent}.
	 * @param t a ZK {@link Component}
	 * @return a proxy of {@link IComponent}
	 */
	public static <T extends Component, I extends IComponent> I proxyIComponent(T t) {
		Class<? extends Component> tClass = t.getClass();
		Class<? extends Component> zClass = tClass;
		Object o = _methodCache.get(tClass);
		String tClassName = tClass.getSimpleName();
		Class<?> proxyClass;
		Class<?> iCtrlClass;
		if (o == null) {
			Class<?> iClass;
			ProxyMeta meta = null;
			try {
				meta = lookupZephyrClass(tClass);
			} catch (ClassNotFoundException e) {
				throw new UiException("Unsupported Zephyr component for [" + tClassName + "]");
			}

			iClass = meta.iClass;
			iCtrlClass = meta.iCtrlClass;
			zClass = meta.zClass;

			ProxyFactory factory = new ProxyFactory();
			factory.setUseWriteReplace(false);

			factory.setSuperclass(tClass);
			factory.setInterfaces(new Class[] { iClass, ZKChildrenCtrl.class });
			factory.setFilter(GetterMethodHandlerForC2I.GETTER_METHOD_FILTER);
			 proxyClass = factory.createClass();
			 _methodCache.put(tClass, Arrays.asList(proxyClass, meta));
		} else {
			List data = (List) o;
			proxyClass = (Class<?>) data.get(0);
			ProxyMeta meta = (ProxyMeta) data.get(1);
			iCtrlClass = meta.iCtrlClass;
			zClass = meta.zClass;
		}
		try {
			IComponent iCompProxy = (IComponent) proxyClass.newInstance();
			((Proxy) iCompProxy).setHandler(new GetterMethodHandlerForC2I(t));
			Object returned = iCtrlClass.getDeclaredMethod("from", zClass).invoke(iCtrlClass.getClass(), iCompProxy);
			return (I) returned;
		} catch (Throwable ignore) {
			_log.error("", ignore);
			throw new UiException("Unsupported new instance for the type of [" + tClass + "]", ignore);
		}
	}

	private static ProxyMeta lookupZephyrClass(Class tclass)
			throws ClassNotFoundException {
		// Cases
		// 1. Detail belongs to zul in ZK, move Detail to zephyrex in Zephyr.
		// 2. MeshInternalPaging is extended from Paging when MeshElement is mold=paging.
		Class iClass = null;
		Class iCtrlClass = null;
		String packageName = ZEPHYR_PACKAGE;
		int tryingCount = 1;

		// try one level parent class only, if possible.
		while (tryingCount-- >= 0) {
			String tClassName = tclass.getSimpleName();
			try {
				iClass = Classes.forNameByThread(packageName + tClassName);
			} catch (ClassNotFoundException e) {
				// try zephyrex again
				try {
					iClass = Classes.forNameByThread(
							(packageName = ZEPHYREX_PACKAGE) + tClassName);
				} catch (ClassNotFoundException ee) {
					if (tryingCount < 0) {
						throw ee;
					}
					packageName = ZEPHYR_PACKAGE; // reset to Zephyr package

					// try parent class again if possible
					tclass = tclass.getSuperclass();
				}
			}
			if (iClass != null) {
				iCtrlClass = Classes.forNameByThread(packageName + tClassName + "Ctrl");
				break;
			}
		}
		return new ProxyMeta(iClass, iCtrlClass, tclass);
	}

	private static class ProxyMeta {
		/*package*/ Class iClass, iCtrlClass, zClass;
		public ProxyMeta(Class iClass, Class iCtrlClass, Class zClass) {
			this.iClass = iClass;
			this.iCtrlClass = iCtrlClass;
			this.zClass = zClass;
		}
	}

	public static List<? extends IComponent> proxyIChildren(Component instance) {
		if (instance instanceof ZKChildrenCtrl) {
			List<Component> children = ((ZKChildrenCtrl) instance).getZKChildren();
			if (!children.isEmpty()) {
				// filter out all getter components contain in getChildren();
				final Set<Component> components;
				try {
					Set<String> allValidNames = ReflectionUtils.get(Methods.of(
									lookupZephyrClass(instance.getClass()).iClass)
							.filter(withModifier(Modifier.PUBLIC))
							.filter(withPrefix("get").and(
									withParametersCount(0)))
							.filter(withReturnTypeAssignableFrom(
									IComponent.class)).as(Method.class)
							.map(Method::getName));
					components = ReflectionUtils.get(
							Methods.of(instance.getClass())
									.filter(withModifier(Modifier.PUBLIC))
									.filter(withPrefix("get").and(
											withParametersCount(0)))
									.filter(withReturnTypeAssignableFrom(
											Component.class)).as(Method.class)
									.filter(method -> allValidNames.contains(method.getName()))
									.map(method -> {
										try {
											return (Component) method.invoke(
													instance);
										} catch (IllegalAccessException e) {
											throw new RuntimeException(e);
										} catch (InvocationTargetException e) {
											throw new RuntimeException(e);
										}
									}).filter(Objects::nonNull));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				return children.stream()
						.filter(component -> !components.contains(component))
						.map(c -> (IComponent) Immutables.proxyIComponent(c))
						.collect(Collectors.toList());
			}
		}
		return new ArrayList<>();
	}
	public static IComponent proxyIChild(Component instance) {
		if (instance instanceof ZKChildrenCtrl) {
			List<Component> children = ((ZKChildrenCtrl) instance).getZKChildren();
			if (!children.isEmpty())
				return Immutables.proxyIComponent(children.get(0));
		}
		return null;
	}

	public static IComponent proxyIChild(Component instance, Class<? extends Component> skipClass) {
		if (instance instanceof ZKChildrenCtrl) {
			List<Component> children = ((ZKChildrenCtrl) instance).getZKChildren();
			if (!children.isEmpty()) {
				Optional<Component> any = children.stream()
						.filter(component -> !skipClass.isInstance(component))
						.findAny();
				if (any.isPresent()) {
					return Immutables.proxyIComponent(any.get());
				}
			}
		}
		return null;
	}


	/** Creates {@link IComponent}s from {@link Component}s that don't belong to any page
	 * from the specified page definition.
	 *
	 * @param pagedef the page definition to use. It cannot be null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the first component being created.
	 * @see #createComponents(String, Map)
	 * @since 2.4.0
	 */
	public static <T extends IComponent> List<T> createComponents(PageDefinition pagedef, Map<?, ?> arg) {
		// if some composers in the pagedef use StatelessComposer implementations, we should use
		// their proxy IComponents instead.
		// refer to UiEngineImpl#createComponents() implementation
		final VolatileIPage page = new VolatileIPage(pagedef);
		((PageCtrl) page).preInit();
		pagedef.init(page, false);

		final Execution exec = Executions.getCurrent();
		((WebAppCtrl) exec.getDesktop()
				.getWebApp()).getUiEngine()
				.createComponents(exec, pagedef, page, null, null, null, arg);
		try {
			return page.getAllIComponents();
		} finally {
			page.mergeActionsTo(((ExecutionCtrl) Executions.getCurrent()).getCurrentPage());
			// refer to UiEngineImpl#createComponents() implementation
			try {
				((DesktopCtrl) page.getDesktop()).removePage(page);
			} catch (Throwable ex) {
				_log.warn("", ex);
			}
			((PageCtrl) page).destroy();
		}
	}

	/** Creates {@link IComponent}s from {@link Component}s that don't belong to any page
	 * from a page file specified by an URI.
	 *
	 * <p>It loads the page definition from the specified URI (by
	 * use {@link Execution#getPageDefinition} ), and then
	 * invokes {@link #createComponents(PageDefinition,Map)}
	 * to create components.
	 *
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Map)
	 */
	public static <T extends IComponent> List<T> createComponents(String uri, Map<?, ?> arg) {
		return createComponents(Executions.getCurrent().getPageDefinition(uri), arg);
	}

	/** Creates {@link IComponent}s from {@link Component}s that don't belong to any page
	 * from the raw content specified by a string.
	 *
	 * <p>The raw content is parsed to a page definition by use of
	 * {@link Execution#getPageDefinitionDirectly(String, String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Map)}
	 * to create components.
	 *
	 * @param content the raw content of the page. It must be in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Map)
	 * @see #createComponents(String, Map)
	 */
	public static <T extends IComponent> List<T> createComponentsDirectly(String content, String extension, Map<?, ?> arg) {
		return createComponents(Executions.getCurrent().getPageDefinitionDirectly(content, extension), arg);
	}
}
