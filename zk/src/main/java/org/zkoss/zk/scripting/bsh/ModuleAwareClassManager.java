/* ModuleAwareClassManager.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 19:54:58 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.scripting.bsh;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import bsh.BshClassManager;
import bsh.Capabilities;
import bsh.Interpreter;
import bsh.ReflectManager;
import bsh.classpath.ClassManagerImpl;

/**
 * The BeanShell class manager of {@link BSHInterpreter}.
 * BeanShell resolves a Java method call to the most specific declaration it finds in
 * the class hierarchy of the target. On JDK 16 and later that declaration may be one
 * BeanShell is not allowed to invoke: it is in a package the JDK does not export, such
 * as {@code sun.util.calendar.ZoneInfo#getRawOffset()}, or, once a script declared a
 * class and so turned on BeanShell's accessibility, in a package the JDK does not open,
 * such as {@code java.util.ArrayList$Itr#next()}.
 * Only then does this class manager resolve the call to a declaration BeanShell can
 * invoke that the chosen one overrides, such as {@code java.util.TimeZone#getRawOffset()}
 * or {@code java.util.Iterator#next()}, so virtual dispatch still runs the same code.
 * Every other call is resolved by BeanShell as before.
 *
 * @author peakerlee
 */
/*package*/ class ModuleAwareClassManager extends ClassManagerImpl {
	private static final Module BSH_MODULE = Interpreter.class.getModule();
	/** bsh.Reflect#getCandidateMethods, or null if this BeanShell does not have it. */
	private static final Method GET_CANDIDATES;
	/** bsh.Reflect#findMostSpecificMethod, or null if this BeanShell does not have it. */
	private static final Method FIND_MOST_SPECIFIC;
	/** bsh.BshClassManager#declaringInterpreter, or null if this BeanShell does not have it. */
	private static final Field DECLARING_INTERPRETER;

	static {
		Method candidates = null, mostSpecific = null;
		Field declaring = null;
		try {
			final Class<?> reflect = Class.forName("bsh.Reflect", false, Interpreter.class.getClassLoader());
			candidates = reflect.getDeclaredMethod("getCandidateMethods", Class.class, String.class, int.class,
					boolean.class);
			mostSpecific = reflect.getDeclaredMethod("findMostSpecificMethod", Class[].class, Method[].class);
			declaring = BshClassManager.class.getDeclaredField("declaringInterpreter");
			candidates.setAccessible(true);
			mostSpecific.setAccessible(true);
			declaring.setAccessible(true);
		} catch (Throwable ex) { //not BeanShell 2.0b6: use its own class manager
			candidates = mostSpecific = null;
			declaring = null;
		}
		GET_CANDIDATES = candidates;
		FIND_MOST_SPECIFIC = mostSpecific;
		DECLARING_INTERPRETER = declaring;
	}

	/** Returns the class manager for the global namespace of the given interpreter.
	 * It is the interpreter's own class manager if this BeanShell is not the one
	 * this class is written for.
	 * @param cl the class loader, the same as the one given to the interpreter
	 */
	/*package*/ static BshClassManager newInstance(Interpreter ip, ClassLoader cl) {
		if (DECLARING_INTERPRETER != null) {
			try {
				final ModuleAwareClassManager bcm = new ModuleAwareClassManager();
				DECLARING_INTERPRETER.set(bcm, ip); //as BshClassManager.createClassManager does
				bcm.setClassLoader(cl);
				return bcm;
			} catch (Throwable ignore) { //use BeanShell's own
			}
		}
		return ip.getClassManager();
	}

	@Override
	protected Method getResolvedMethod(Class clas, String name, Class[] types, boolean onlyStatic) {
		final Method cached = super.getResolvedMethod(clas, name, types, onlyStatic);
		if (cached != null || onlyStatic || GET_CANDIDATES == null) //never answer a static call with an instance method
			return cached;

		try {
			//what bsh.Reflect#resolveJavaMethod does, and does itself if this returns null
			final boolean accessibility = Capabilities.haveAccessibility();
			final Method[] candidates = (Method[]) GET_CANDIDATES.invoke(null, clas, name, types.length,
					!accessibility);
			Method m = (Method) FIND_MOST_SPECIFIC.invoke(null, types, candidates);
			if (m != null && !isInvocable(m, accessibility))
				m = findOverridden(clas, m, candidates, accessibility);
			if (m != null) {
				if (accessibility)
					ReflectManager.RMSetAccessible(m); //as BeanShell does; it cannot fail for an invocable method
				cacheResolvedMethod(clas, types, m);
			}
			return m;
		} catch (ReflectiveOperationException | Capabilities.Unavailable | RuntimeException ex) {
			return null;
		}
	}

	/** Returns the candidate that the given method overrides and bsh.Reflect can
	 * invoke, or null if none. Invoking it still runs the given method.
	 * @param clas the class the method is looked up in
	 */
	private static Method findOverridden(Class<?> clas, Method method, Method[] candidates,
			boolean accessibility) {
		final int mods = method.getModifiers();
		if (Modifier.isStatic(mods) || Modifier.isPrivate(mods)) //neither is dispatched
			return null;
		for (Method m : candidates) {
			final int ms = m.getModifiers();
			if (!Modifier.isStatic(ms) && (ms & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0
					&& isInvocable(m, accessibility) && isOverriddenBy(clas, m, method))
				return m;
		}
		return null;
	}

	/** Returns whether the given method overrides m: with the same parameter types,
	 * or through the bridge javac generates with m's parameter types, such as
	 * {@code String.CaseInsensitiveComparator#compare(Object, Object)}.
	 */
	private static boolean isOverriddenBy(Class<?> clas, Method m, Method method) {
		final Class<?>[] types = m.getParameterTypes();
		if (Arrays.equals(types, method.getParameterTypes()))
			return true;

		//a call with m's parameter types is dispatched to the bridge only if clas declares it,
		//and the bridge calls method only if method is the one non-bridge it can call
		final Class<?> cls = method.getDeclaringClass();
		if (cls != clas)
			return false;
		boolean bridge = false;
		Method target = null;
		for (Method dm : cls.getDeclaredMethods()) {
			if (!dm.getName().equals(m.getName()) || dm.getParameterCount() != types.length)
				continue;
			if (dm.isBridge())
				bridge |= Arrays.equals(dm.getParameterTypes(), types);
			else if (target != null)
				return false; //an overload the bridge may call instead
			else
				target = dm;
		}
		if (!bridge || target == null || !Arrays.equals(target.getParameterTypes(), method.getParameterTypes()))
			return false;
		for (Class<?> c = cls.getSuperclass(); c != null; c = c.getSuperclass()) {
			try {
				c.getDeclaredMethod(m.getName(), types);
				return false; //a bridge that makes the superclass method public calls it instead
			} catch (NoSuchMethodException ex) { //check the next superclass
			}
		}
		return true;
	}

	/** Returns whether bsh.Reflect can invoke the given method: as is, or, with
	 * BeanShell's accessibility on, after making it accessible.
	 */
	private static boolean isInvocable(Method m, boolean accessibility) {
		final Class<?> cls = m.getDeclaringClass();
		final Module module = cls.getModule();
		final String pkg = cls.getPackageName();
		if (!accessibility)
			return module.isExported(pkg, BSH_MODULE);
		return module.isOpen(pkg, BSH_MODULE) || (module.isExported(pkg, BSH_MODULE)
				&& Modifier.isPublic(cls.getModifiers()) && Modifier.isPublic(m.getModifiers()));
	}
}
