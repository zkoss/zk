/* ActionHandler.java

	Purpose:
		
	Description:
		Refer to https://github.com/benjiman/lambda-type-references/blob/master/src/main/java/com/benjiweber/typeref/MethodFinder.java under Apache-2.0 License
		
	History:
		11:18 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.zkoss.lang.Library;
import org.zkoss.util.Maps;
import org.zkoss.zel.impl.util.ConcurrentCache;

/**
 * An interface to indicates an action handler functional.
 * @author jumperchen
 */
public interface ActionHandler extends Serializable {

	/**
	 * Returns the parameter count for this action handler.
	 * @return A number of parameters of a lambda method or a method reference.
	 */
	int getParameterCount();

	/**
	 * The cache size of the method resolver.
	 * <p>Default: {@code 1000}, this can be changed by specifying the value of the key
	 * {@code "org.zkoss.zephyr.util.ActionHandler.CACHE_SIZE"} in ZK library property</p>
	 */
	int CACHE_SIZE = Library.getIntProperty("org.zkoss.zephyr.util.ActionHandler.CACHE_SIZE", 1000);

	/**
	 * The loaded {@code Class} cache.
	 */
	ConcurrentCache<String, Class> LOADED_CLASS_CACHE = new ConcurrentCache(CACHE_SIZE);

	/**
	 * The loaded {@code Method} cache.
	 */
	ConcurrentCache<String, Method> LOADED_METHOD_CACHE = new ConcurrentCache(CACHE_SIZE);

	/**
	 * The loaded {@code Lambda Method} cache.
	 */
	ConcurrentCache<String, SerializedLambda> LOADED_LAMBDA_CACHE = new ConcurrentCache(CACHE_SIZE);

	/**
	 * The default values of Java primitive types, primitive Objects, Collection, and Map.
	 */
	Map<Class<?>, Object> DEFAULT_VALUES = Maps.of(int.class, 0,
			Integer.class, 0, boolean.class, false, Boolean.class, false,
			byte.class, (byte) 0, Byte.class, 0, char.class, ' ',
			Character.class, ' ', short.class, (short) 0.0, Short.class,
			(short) 0.0, long.class, 0l, Long.class, 0L, float.class, 0.0f,
			Float.class, 0.0f, double.class, 0.0d, Double.class, 0.0d, Map.class,
			Collections.EMPTY_MAP, List.class, Collections.EMPTY_LIST,
			Set.class, Collections.EMPTY_SET);

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static ActionHandler of(ActionHandler a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static ActionHandler of(ActionHandler0 a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A> ActionHandler of(ActionHandler1<A> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B> ActionHandler of(ActionHandler2<A, B> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C> ActionHandler of(ActionHandler3<A, B, C> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C, D> ActionHandler of(ActionHandler4<A, B, C, D> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C, D, E> ActionHandler of(ActionHandler5<A, B, C, D, E> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C, D, E, F> ActionHandler of(ActionHandler6<A, B, C, D, E, F> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C, D, E, F, G> ActionHandler of(ActionHandler7<A, B, C, D, E, F, G> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C, D, E, F, G, H> ActionHandler of(ActionHandler8<A, B, C, D, E, F, G, H> a) {
		return a;
	}

	/**
	 * Returns a casting of {@link ActionHandler}.
	 */
	static <A, B, C, D, E, F, G, H, I> ActionHandler of(ActionHandler9<A, B, C, D, E, F, G, H, I> a) {
		return a;
	}

	/**
	 * Invokes this handler with zero arguments.
	 * @throws Throwable
	 */
	default void call() throws Throwable {
		((ActionHandler0) this).run();
	}

	/**
	 * Invokes this handler with the given arguments.
	 * <p><b>Note:</b>The {@link #getParameterCount()} is the same as the length of the given arguments,
	 * otherwise, the {@link IndexOutOfBoundsException} will be raised.</p>
	 * @param args The arguments to pass to this handler.
	 * @throws Throwable
	 */
	default void call(Object... args) throws Throwable {
		if (args == null) {
			if (this instanceof ActionHandler0) {
				call();
			} else {
				((ActionHandler1) this).accept(null);
			}
			return;
		}
		switch (args.length) {
		case 0:
			((ActionHandler0) this).run();
			break;
		case 1:
			((ActionHandler1) this).accept(args[0]);
			break;
		case 2:
			((ActionHandler2) this).accept(args[0], args[1]);
			break;
		case 3:
			((ActionHandler3) this).accept(args[0], args[1], args[2]);
			break;
		case 4:
			((ActionHandler4) this).accept(args[0], args[1], args[2], args[3]);
			break;
		case 5:
			((ActionHandler5) this).accept(args[0], args[1], args[2], args[3], args[4]);
			break;
		case 6:
			((ActionHandler6) this).accept(args[0], args[1], args[2], args[3], args[4], args[5]);
			break;
		case 7:
			((ActionHandler7) this).accept(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
			break;
		case 8:
			((ActionHandler8) this).accept(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
			break;
		case 9:
			((ActionHandler9) this).accept(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + args.length + ", Size: 9");
		}
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	default SerializedLambda serialized() {
		try {
			return LOADED_LAMBDA_CACHE.computeIfAbsent(getClass().getCanonicalName(), (ignore) -> {
				try {
					Method replaceMethod = getClass().getDeclaredMethod(
							"writeReplace");
					replaceMethod.setAccessible(true);
					return (SerializedLambda) replaceMethod.invoke(this);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			});
		} catch (Exception e) {
			throw new UnableToGuessMethodException();
		}
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	default Class<?> getContainingClass(SerializedLambda lambda) {
		try {
			String className = lambda.getImplClass();;
			Class aClass = LOADED_CLASS_CACHE.computeIfAbsent(className, (ignore) -> {
				try {
					return Class.forName(className.replace("/", "."));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			});
			return aClass;
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	default Method method() {
		SerializedLambda lambda = serialized();
		Class<?> containingClass = getContainingClass(lambda);
		final String methodName = containingClass.getCanonicalName() + "#"
				+ lambda.getImplMethodName();
		return LOADED_METHOD_CACHE.computeIfAbsent(methodName, (ignore) ->
				asList(
						containingClass.getDeclaredMethods()).stream()
						.filter(method -> Objects.equals(method.getName(),
								lambda.getImplMethodName())).findFirst()
						.orElseThrow(UnableToGuessMethodException::new));
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	default Parameter parameter(int n) {
		return method().getParameters()[n];
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	default Object defaultValueForParameter(int n) {
		return ofType(parameter(n).getType());
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	class UnableToGuessMethodException extends RuntimeException {}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	static <T> T ofType(Class<?> type) {
		return (T) DEFAULT_VALUES.getOrDefault(type, null);
	}
}
