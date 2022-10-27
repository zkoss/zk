/* IComponentCtrl.java

	Purpose:
		
	Description:
		
	History:
		10:35 AM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Strings;
import org.zkoss.util.Maps;
import org.zkoss.stateless.action.data.ActionData;
import org.zkoss.stateless.action.data.FileData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.ui.IStubComponent;
import org.zkoss.stateless.ui.Self;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.ui.util.StatelessContentRenderer;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.util.Oid;
import org.zkoss.zk.ui.Executions;

/**
 * An addition interface to {@link IComponent}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public class IComponentCtrl {
	private final static Logger _logger = LoggerFactory.getLogger(IComponentCtrl.class);

	private final static String PARAM_RICHLET_PATH = "_";
	private final static String PARAM_ACTION_ARGS = "0";
	private final static String PARAM_ACTION_METHOD = "0.1";
	private final static String PARAM_TYPE = "1";
	private final static String PARAM_ACTION_VARIABLE_TYPE = "2";
	private final static String PARAM_PRIMITIVE_TYPE = "2";
	private final static String PARAM_POJO_TYPE = "3";
	private final static String PARAM_DATETIME_TYPE = "4";
	private final static String PARAM_QUERY = "5";
	private final static String PARAM_QUERY_FIELD = "6";

	/**
	 * Renders {@code actions} to client widget with its bound parameters.
	 * @param actionHandlerList
	 * @param renderer
	 * @throws IOException
	 */
	public static void renderActions(List<ActionHandler> actionHandlerList, StatelessContentRenderer renderer)
			throws IOException {
		if (!actionHandlerList.isEmpty()) {
			Map<String, List<Map>> result = new HashMap<>();
			for(ActionHandler actionHandler: actionHandlerList) {
				Method method = actionHandler.method();
				Action action = lookupActionAnnotation(actionHandler, method);

				IStubComponent binding = renderer.getBinding();
				if (StatelessRichlet.class.isAssignableFrom(method.getDeclaringClass())) {
					for (String eventName : action.type()) {
						renderer.render("$" + eventName, true);
						// resolve the type of action handler arguments here
						List list = resolveMethodParameter(actionHandler, method);
						result.computeIfAbsent(eventName, (__) -> new ArrayList()).add(Maps.of(PARAM_ACTION_ARGS, list,
								PARAM_RICHLET_PATH, ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getRequestURI(),
								PARAM_ACTION_METHOD, method.getName()));
					}
				} else { // Composer case, it has at least one Stub/StubsComponent at server
					for (String eventName : action.type()) {
						renderer.render("$" + eventName, true);
						binding.addAction(eventName, actionHandler);
						// resolve the type of action handler arguments here
						List list = resolveMethodParameter(actionHandler, method);

						result.computeIfAbsent(eventName, (__) -> new ArrayList()).add(Maps.of(PARAM_ACTION_ARGS, list, PARAM_ACTION_METHOD, method.getName()));
					}
				}
			}

			renderer.render("'@action'", result);
		}
	}

	private static List resolveMethodParameter(ActionHandler actionHandler, Method method) {
		int size = actionHandler.getParameterCount();
		final Action action = lookupActionAnnotation(actionHandler, method);
		Parameter[] parameters = method.getParameters();
		int diff = parameters.length - size;
		return Arrays.stream(parameters).skip(diff).map(parameter -> {
			ActionVariable annotation = parameter.getAnnotation(
					ActionVariable.class);
			Class<?> type = parameter.getType();
			if (annotation != null) {
				String id = annotation.targetId();
				if (Strings.isBlank(id)) {
					id = parameter.getName();
				}
				// POJO Mapping if any
				if (isPojo(type)) {
					// scan if any field annotation exists
					Map<String, List<String>> resolvePojoType = resolvePojoType(type, true);

					if (resolvePojoType.isEmpty()) {
						// all fields are query fields
						List<List<String>> list = Arrays.stream(
										type.getDeclaredFields()).filter(field -> {
									int modifiers = field.getModifiers();
									return !Modifier.isStatic(modifiers);
								}).map(field -> Arrays.asList(field.getName()))
								.collect(Collectors.toList());
						return Maps.of(PARAM_TYPE, PARAM_POJO_TYPE, PARAM_QUERY,
								Maps.of(id, Maps.of("", list)));
					} else {
						// all id are overwritten by parameter annotation
						return Maps.of(PARAM_TYPE, PARAM_POJO_TYPE, PARAM_QUERY,
								Maps.of(id, Maps.of("", new ArrayList<>(
										resolvePojoType.values()))));
					}

				} else {
					return Maps.of(PARAM_TYPE, PARAM_ACTION_VARIABLE_TYPE,
							PARAM_QUERY, id, PARAM_QUERY_FIELD, annotation.field());
				}
			} else {
				if (isBuiltinType(type)) {
					Class<? extends ActionData>[] actionDataTypes = getActionDataTypes(
							type);
					if (actionDataTypes != null) {
						Map<String, List<String>> actionDataMap = new HashMap<>();
						Arrays.stream(actionDataTypes).filter(Objects::nonNull)
								.map(aClass -> resolvePojoType(aClass, true))
								.forEach(stringListMap -> actionDataMap.putAll(stringListMap));
						return Maps.of(PARAM_TYPE, PARAM_POJO_TYPE, PARAM_QUERY,
								actionDataMap);
					}
					return 0;
				} else if (isPrimitive(type)) {
					// do Primitive type with its name
					return Maps.of(PARAM_TYPE, PARAM_PRIMITIVE_TYPE, PARAM_QUERY, parameter.getName());
				} else if (isDateType(type)) {
					// do Primitive type with its name
					return Maps.of(PARAM_TYPE, PARAM_DATETIME_TYPE, PARAM_QUERY, parameter.getName());
				} else if (isFileType(type)) {
					return Maps.of(PARAM_TYPE, PARAM_POJO_TYPE, PARAM_QUERY, parameter.getName());
				} else {
					// POJO case
					Map<String, ?> stringMap = resolvePojoType(type, false);
					if (stringMap.isEmpty()) {
						_logger.warn("Unknown type: " + type);
						throw new IllegalArgumentException("Unknown Action argument type: [" + type + "] at " + method.getDeclaringClass().getCanonicalName() + "#" + method.getName());
					} else {
						return Maps.of(PARAM_TYPE, PARAM_POJO_TYPE, PARAM_QUERY,
								stringMap);
					}
				}
			}
		}).collect(Collectors.toList());
	}

	/**
	 * Tests if the given {@code type} is primitive type or primitive object type.
	 * @param type A class to test with
	 * @return {@code true} if it's the primitive type or primitive object type.
	 */
	public static boolean isPrimitive(Class<?> type) {
		if (type.isPrimitive() || type == String.class) {
			return true;
		}
		Object o = ActionHandler.DEFAULT_VALUES.get(type);
		if (o == null || o instanceof Collection || o instanceof Map) {
			return false;
		}
		return true;
	}

	/**
	 * Tests if the given {@code type} is Java POJO-like object.
	 * @param type A class to test with
	 * @return {@code true} if it's the POJO object type.
	 */
	public static boolean isPojo(Class<?> type) {
		return !isBuiltinType(type) && !isPrimitive(type) && !isDateType(type) && !type.isInterface();
	}

	/**
	 * Tests if the given {@code type} is Java Date type.
	 *
	 * <p><b>Note:</b> only support the types in {@code jackson.datatype.jsr310}</p>
	 * @param type A class to test with
	 * @return {@code true} if it's the Java Date type.
	 */
	// only support types which allow in jackson.datatype.jsr310
	public static boolean isDateType(Class<?> type) {
		if (JACKSON_DATE_TYPE.containsKey(type)) {
			return true;
		} else  if (Date.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	private static Map<String, List<String>> resolvePojoType(Class<?> type, boolean withFieldName) {
		Map<String, List<String>> result = new HashMap<>();
		for (Field field : type.getDeclaredFields()) {
			ActionVariable annotation = field.getAnnotation(
					ActionVariable.class);
			if (annotation == null) continue;
			String id = annotation.targetId();
			if (Strings.isBlank(id)) {
				id = field.getName();
			}

			// avoid duplicated id
			if (".".equals(id) && result.containsKey(id)) {
				id = Oid.generate(UUID.randomUUID(), ".");
			}
			if (withFieldName) {
				result.put(id, Arrays.asList(annotation.field(), field.getName()));
			} else {
				result.put(id, Arrays.asList(annotation.field()));
			}
		}
		return result;
	}

	// only support types which allow in jackson.datatype.jsr310
	final static Map<Class, Boolean> JACKSON_DATE_TYPE = Maps.of(ZonedDateTime.class, Boolean.TRUE,
			Duration.class, Boolean.TRUE, Instant.class, Boolean.TRUE, LocalDate.class, Boolean.TRUE,
			LocalTime.class, Boolean.TRUE, LocalDateTime.class, Boolean.TRUE,
			MonthDay.class, Boolean.TRUE, OffsetDateTime.class, Boolean.TRUE,
			OffsetTime.class, Boolean.TRUE, YearMonth.class, Boolean.TRUE,
			Year.class, Boolean.TRUE);

	/**
	 * Tests if the given {@code type} is a built-in type
	 * @param type a class to test with
	 * @return {@code true} if it's a built-in type.
	 */
	public static boolean isBuiltinType(Class<?> type) {
		if (Self.class.isAssignableFrom(type)) {
			return true;
		} else if (UiAgent.class.isAssignableFrom(type)) {
			return true;
		} else if (ActionData.class.isAssignableFrom(type)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tests if the given {@code type} is {@link FileData}.
	 * @param type A class to test with
	 * @return {@code true} if it's a {@link FileData}.
	 */
	public static boolean isFileType(Class<?> type) {
		if (FileData.class.isAssignableFrom(type))
			return true;
		return false;
	}

	private static Class<? extends ActionData>[] getActionDataTypes(Class<?> type) {
		if (ActionData.class.isAssignableFrom(type)) {
			Class<? extends ActionData> aType = (Class<? extends ActionData>) type;
			return new Class[] {aType};
		}
		return null;
	}

	/**
	 * Looks up the {@code @Action} annotation on the given method if any.
	 * @param actionHandler The lookup action handler.
	 * @param method The lookup action method
	 * @return Action if found or {@code null} is assumed.
	 */
	public static Action lookupActionAnnotation(ActionHandler actionHandler, Method method) {
		// Priority: Interface annotation is higher than method annotation.
		for (Class<?> interface0 : actionHandler.getClass().getInterfaces()) {
			Action annotation = interface0.getAnnotation(Action.class);
			if (annotation != null) {
				return annotation;
			}
		}
		if (method == null) {
			method = actionHandler.method();
		}
		return method.getAnnotation(Action.class);
	}

	/**
	 * Tests if the given {@link IComponent} contains {@link IComponent#getAction() aciton}
	 * or {@link IComponent#getActions() actions}.
	 * @param iComponent The immutable component to test with.
	 * @return {@code true}, if it contains.
	 */
	public static boolean hasAction(IComponent iComponent) {
		boolean hasAction = iComponent.getAction() != null;
		if (hasAction) return true;
		List<ActionHandler> actions = iComponent.getActions();
		return actions != null && !actions.isEmpty();
	}
}
