/* Classes.java

{{IS_NOTE

	Purpose: Utilities to handle Class
	Description:
	History:
	 2001/4/19, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Date;
import java.lang.reflect.Constructor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.zkoss.mesg.MCommon;
import org.zkoss.mesg.Messages;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;
import org.zkoss.math.BigDecimals;
import org.zkoss.math.BigIntegers;
import org.zkoss.util.MultiCache;
import org.zkoss.util.Cache;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.logging.Log;

/**
 * Utilities to handle java.lang.Class
 *
 * @author tomyeh
 */
public class Classes {
	private static final Log log = Log.lookup(Classes.class);

	/**
	 * Instantiates a new instance of the specified class with
	 * the specified arguments and argument types.
	 *
	 * <p>Note only public constructors are searched.
	 *
	 * @param cls the class of the instance to create
	 * @param argTypes the argument types of the constructor to inovke
	 * @param args the arguments to initialize the instance
	 * @return the new instance
	 *
	 * @exception NoSuchMethodException if a matching method is not found
	 * @exception InstantiationException if the class that declares the
	 * underlying constructor represents an abstract class
	 * @exception InvocationTargetException if the underlying constructor
	 * throws an exception
	 * @see #newInstance(String, Class[], Object[])
	 */
	public static final
	Object newInstance(Class cls, Class[] argTypes, Object[] args)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, IllegalAccessException {
 		return cls.getConstructor(argTypes).newInstance(args);
	}
	/**
	 * Instantiates a new instance of the specified class with the
	 * specified argument.
	 *
	 * <p>It searches all contructor for the first one that matches
	 * the specified arguments.
	 * @since 3.0.1
	 */
	public static final
	Object newInstance(Class cls, Object[] args)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, IllegalAccessException {
		if (args == null || args.length == 0)
			return cls.newInstance();

		final Constructor[] cs = cls.getConstructors();
		for (int j = 0; j < cs.length; ++j) {
			final Class[] types = cs[j].getParameterTypes();
			if (types.length == args.length) {
				for (int k = args.length;;) {
					if (--k < 0)
						return cs[j].newInstance(args);

					final Object arg = args[k];
					final Class type = types[k];
					if (arg == null)
						if (type.isPrimitive()) break; //mismatch
						else continue; //match

					if (type.isInstance(arg)) continue; //match
					if (!type.isPrimitive()
					|| !Primitives.toWrapper(type).isInstance(arg))
						break; //mismatch
				}
			}
		}

		final StringBuffer sb = new StringBuffer(80)
			.append("No contructor compatible with ");
		for (int j = 0; j < args.length; ++j)
			sb.append(j != 0 ? ", ": "[")
				.append(args[j] != null ? args[j].getClass().getName(): null);
		throw new NoSuchMethodException(
			sb.append("] in ").append(cls.getName()).toString());
	}

	/**
	 * Instantiates a new instance of the specified class name
	 * with the specified arguments.
	 *
	 * <p>It uses Class.forName to get the class.
	 *
	 * @param clsName the class name of the instance to create
	 * @param argTypes the argument types of the constructor to inovke
	 * @param args the arguments to initialize the instance
	 * @return the new instance
	 *
	 * @exception NoSuchMethodException if a matching method is not found
	 * @exception InstantiationException if the class that declares the
	 * underlying constructor represents an abstract class
	 * @exception InvocationTargetException if the underlying constructor
	 * throws an exception
	 * @exception ClassNotFoundException if the specified class name is not a class
	 * @see #newInstance(Class, Class[], Object[])
	 */
	public static final Object
	newInstance(String clsName, Class[] argTypes, Object[] args)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, ClassNotFoundException, IllegalAccessException {
	 	return newInstance(Class.forName(clsName), argTypes, args);
	}
	/**
	 * Creates and initializes a new instance of the specified class name
	 * with the specified arguments, by use of {@link #forNameByThread}.
	 *
	 * <p>It uses {@link #forNameByThread} to get the class.
	 *
	 * @param clsName the class name of the instance to create
	 * @param argTypes the argument types of the constructor to inovke
	 * @param args the arguments to initialize the instance
	 * @return the new instance
	 *
	 * @exception NoSuchMethodException if a matching method is not found
	 * @exception InstantiationException if the class that declares the
	 * underlying constructor represents an abstract class
	 * @exception InvocationTargetException if the underlying constructor
	 * throws an exception
	 * @exception ClassNotFoundException if the specified class name is not a class
	 * @see #newInstance(Class, Class[], Object[])
	 */
	public static final Object
	newInstanceByThread(String clsName, Class[] argTypes, Object[] args)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, ClassNotFoundException, IllegalAccessException {
	 	return newInstance(forNameByThread(clsName), argTypes, args);
	}
	/**
	 * Creates and initializes a new instance of the specified class name
	 * with default constructor, by use of {@link #forNameByThread}.
	 */
	public static final Object newInstanceByThread(String clsName)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, ClassNotFoundException, IllegalAccessException {
		return newInstance(forNameByThread(clsName), null, null);
	}
	/**
	 * Returns the Class object of the specified class name, using
	 * the current thread's context class loader.
	 * <p>It first tries Thread.currentThread().getContextClassLoader(),
	 * and then {@link Classes}'s class loader if not found.
	 *
	 * <p>In additions, it handles the primitive types, such as int and double.
	 *
	 * @param clsName fully qualified name of the desired class
	 * @return the Class object representing the desired class
	 * @exception ClassNotFoundException if the class cannot be located by the specified class loader
	 */
	public static final Class forNameByThread(String clsName)
	throws ClassNotFoundException {
		clsName = toInternalForm(clsName);
		final Class cls = Primitives.toClass(clsName);
		if (cls != null)
			return cls;

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null)
			try {
				return Class.forName(clsName, true, cl);
			} catch (ClassNotFoundException ex) { //ignore and try the other
			}
		return Classes.class.forName(clsName);
	}

	/** Returns whether the specified class exists for the current thread's
	 * context class loader.
	 * @param clsnm the class name to test
	 * @since 3.0.7
	 */
	public static final boolean existsByThread(String clsnm) {
		try {
			forNameByThread(clsnm);
			return true;
		} catch (Throwable ex) {
			return false;
		}
	}

	/**
	 * Change class name to internal form (e.g. byte[] -> [B). If already in
	 * internal form, then just return it.
	 */
	public static final String toInternalForm(String clsName) {
		final int k = clsName.indexOf('[');
		if (k <= 0) { //not an array, or already in internal form
			return clsName;	//just return
		}

		//component class
		final String elm = clsName.substring(0, k).trim();
		if (elm.length() == 0)
			throw new IllegalArgumentException("Not a legal class name: \""+clsName+'"');

		//array depth
		boolean leftb = false;
		final String stub = clsName.substring(k);
		final StringBuffer sb = new StringBuffer(128);
		for (int j = 0; j < stub.length(); j++) {
			final char ch = stub.charAt(j);
			if (ch == '[') {
				if (leftb)
					throw new IllegalArgumentException("Not a legal class name: \""+clsName+'"');
				leftb = true;
				sb.append('[');
			} else if (ch == ']') {
				if (!leftb)
					throw new IllegalArgumentException("Not a legal class name: \""+clsName+'"');
				leftb = false;
			}
		}
		if (leftb)
			throw new IllegalArgumentException("Not a legal class name: \""+clsName+'"');

		final char code = Primitives.getNotation(elm);
		if (code != (char)0) {//primitive array
			sb.append(code);
		} else {//object array
			sb.append('L').append(elm).append(';');
		}
		return sb.toString();
	}

	/**
	 * Gets the topmost interface of the specified class or interface that
	 * implements or extends the specified interface.
	 * For example, if A extends B, and C implements A,
	 * then getTopInterface(C, B) returns A.
	 *
	 * <p>The interfaces implemented by the specified class is checked first,
	 * and then the subclass.
	 *
	 * @param cls the class or interface
	 * @param subIF the sub-interface
	 * @return the topmost interface extending subIF, or null if subIF
	 * is not implemented by cls
	 */
	public static Class getTopmostInterface(Class cls, Class subIF) {
		if (cls.isInterface())
			return subIF.isAssignableFrom(cls) ? cls: null;

		while (cls != null) {
			final Class[] ifs = cls.getInterfaces();
			for (int j = 0; j < ifs.length; ++j)
				if (subIF.isAssignableFrom(ifs[j]))
					return ifs[j];

			cls = cls.getSuperclass();
		}
		return null;
	}
	/** Returns all interfaces that are implemented by the specified class.
	 * <p>Unlike {@link Class#getInterfaces}, it recursively searches
	 * for all derived classes.
	 */
	public static Class[] getAllInterfaces(Class cls) {
		final List l = new LinkedList();
		while (cls != null) {
			final Class[] ifs = cls.getInterfaces();
			for (int j = 0; j < ifs.length; ++j)
				l.add(ifs[j]);

			cls = cls.getSuperclass();
		}
		final int sz = l.size();
		return (Class[])l.toArray(new Class[sz]);
	}		

	/**
	 * Tests whether a class contains the specified method.
	 * Only public methods are tested.
	 *
	 * @param cls the class to test
	 * @param name the method name
	 * @param paramTypes the list of parameter types
	 * @return true if it contains the method
	 */
	public static final boolean containsMethod
	(Class cls, String name, Class[] paramTypes) {
		try {
			cls.getMethod(name, paramTypes);
			return true;
		} catch (NoSuchMethodException ex) {//no found
			return false;
		}
	}

	/** Corrects a string to a valid Java name.
	 * Currently, it only removes '-' and capitalizes the succeeding
	 * character. Example, 'field-name' becomes 'fieldName'.
	 */
	public final static String correctFieldName(String name) {
		int j = name.indexOf('-');
		if (j < 0)
			return name; //nothing to change

		for (final StringBuffer sb = new StringBuffer(name);;) {
			sb.deleteCharAt(j);
			if (sb.length() == j)
				return sb.toString();

			sb.setCharAt(j, Character.toUpperCase(sb.charAt(j)));

			j = sb.indexOf("-", j);
			if (j < 0)
				return sb.toString();
		}
	}

	/**
	 * Convert an attribute name, returned by toAttributeName, to
	 * a method name.
	 *
	 * <p>toMethodName("true", "is") => "isTrue"<br>
	 * toMethodName("true", "") => "true"
	 *
	 * @param attrName the attribute name
	 * @param prefix the prefix; one of is, get and set
	 * @return the method name
	 * @see #toAttributeName
	 */
	public static final String toMethodName(String attrName, String prefix) {
		if (prefix.length() == 0)
			return attrName;

		StringBuffer sb = new StringBuffer(prefix);
		char[] buf = attrName.toCharArray();
		buf[0] = Character.toUpperCase(buf[0]);
		return sb.append(buf).toString();
	}

	/**
	 * Tests if a method name is an attribute, i.e., prefixing with is,
	 * get or set. Caller could then test if it is a setter or getter
	 * by charAt(0)=='s'.
	 *
	 * <p>Note 'set' is considered as an attribute, whose name is an
	 * empty string.
	 *
	 * @param methodName the method name to test
	 * @return true if it is setter or getter
	 */
	public static final boolean isAttribute(String methodName) {
		int len = methodName.length();
		if (len < 2)
			return false;

		int j;
		switch (methodName.charAt(0)) {
		case 's':
		case 'g':
			if (len<3 || methodName.charAt(1)!='e' || methodName.charAt(2)!='t')
				return false;
			j = 3;
			break;
		case 'i':
			if (methodName.charAt(1)!='s')
				return false;
			j = 2;
			break;
		default:
			return false;
		}
		return j==len || Character.isUpperCase(methodName.charAt(j));
	}

	/**
	 * Converts a method name to an attribute name by removing the prefix
	 * is, get or set, or null if it doesn't start with is, get or set.
	 *
	 * <p>The code is optimized for better performance.
	 *
	 * @param methodName the method name
	 * @return the attribute name; null if it is not an attribute name
	 * @see #toMethodName
	 */
	public static final String toAttributeName(String methodName) {
		int len = methodName.length();
		if (len < 2)
			return null;

		int j;
		switch (methodName.charAt(0)) {
		case 's':
		case 'g':
			if (len<3 || methodName.charAt(1)!='e' || methodName.charAt(2)!='t')
				return null;
			j = 3;
			break;
		case 'i':
			if (methodName.charAt(1)!='s')
				return null;
			j = 2;
			break;
		default:
			return null;
		}
		if (j==len || Character.isUpperCase(methodName.charAt(j))) {
			char[] buf = new char[len - j];
			if (buf.length>0) {
				methodName.getChars(j, len, buf, 0);
				buf[0] = Character.toLowerCase(buf[0]);
			}
			return new String(buf);
		}
		return null;
	}

	//--Support Class--
	/**The method info class used for {@link #parseMethod(String signature)}.
	 * This info describe the method return type, method name and two collections for
	 * arguments type and arguments name;
	 */
	public static class MethodInfo {
		/** The return type (class name), or null if no specified. */
		public String returnType;
		public String method;
		public String[] argTypes;
		public String[] argNames;
		public String throwsEx;
		
		//constructor
		protected MethodInfo(String r, String m, String[] argTs, String[] argNs, String tEx) {
			returnType = r;
			method = m;
			argTypes = argTs;
			argNames = argNs;
			throwsEx = tEx;
		}
	}
	/**
	 * Gets the method information from a signature. 
	 * It returns a method info with the return type, method name and two collections 
	 * of arguments type and arguments name.
	 *
	 * @param signature the method signature.
	 * @return MethodInfo The method information including return type, method name
	 *			and two collections for argument type annd arguments name.
	 */
	public static final MethodInfo parseMethod(String signature) 
	throws IllegalSyntaxException {
		int len = signature.length();
		int j = Strings.skipWhitespaces(signature, 0);
		int k = Strings.anyOf(signature, "( \t\n\r", j);
		k = Strings.skipWhitespaces(signature, k);
		if (k >= len)
			throw new IllegalSyntaxException(signature);
		
		String returnType = null;
		char cc = signature.charAt(k);
		if (cc != '(') {	
			//skip the return type
			returnType = signature.substring(j, k).trim();
			//
			j = k;
			k = signature.indexOf('(', j + 1);
			if (k < 0)
				throw new IllegalSyntaxException(signature);	
		}
		String method = signature.substring(j, k).trim();
		
		Collection argTypes = new LinkedList();
		Collection argNames = new LinkedList();
		do {
			j = Strings.skipWhitespaces(signature, k + 1);
			if (signature.charAt(j) == ')') break;
			k = Strings.anyOf(signature, ",) \t\n\r", j);
			k = Strings.skipWhitespaces(signature, k);
			if (k >= len)
				throw new IllegalSyntaxException(signature);
			argTypes.add(signature.substring(j, k).trim());

			cc = signature.charAt(k);
			if (cc != ',' && cc != ')') { //parameter name found
				k = Strings.anyOf(signature, ",) \t\n\r", j = k);
				k = Strings.skipWhitespaces(signature, k);
				argNames.add(signature.substring(j, k).trim());

				k = Strings.anyOf(signature, ",)", k);
				if (k >= len)
					throw new IllegalSyntaxException(signature);
			} else {
				argNames.add(""); //no name specified
			}
		} while (signature.charAt(k) == ',');

		assert argTypes.size() == argNames.size();

		//process throws ...
		String strThrows = "throws";
		String tEx = null;
		j = signature.indexOf(strThrows, k);
		if (j >= k && j < len) { //got throws
			k = signature.indexOf(';', j);
			if (k >= 0) 
				tEx = signature.substring(j + strThrows.length(), k).trim();
			else
				tEx = signature.substring(j + strThrows.length(), len).trim();
		}
	
		return new MethodInfo(returnType, method, 
					(String[])argTypes.toArray(new String[argTypes.size()]), 
					(String[])argNames.toArray(new String[argNames.size()]), 
					tEx);
	}
	
	/**
	 * Gets the method based on the signature. It also returns the parameter
	 * names to the params list.
	 *
	 * <p>Like {@link #getMethodInPublic(Class, String, Class[])}, it returns
	 * only public method in a public class/interface.
	 *
	 * <p>For example, "find(java.lang.String name)" will return
	 * the method with one String-typed argument and params will hold "name".
	 * The return type is optional (actualy ignored).
	 *
	 * <p>If params is null, the parameter names are not returned and
	 * the signature could be simplified as "find(java.lang.String)".
	 *
	 * <p>A cache mechanism is implemented, so you don't need to cache it
	 * again in the caller.
	 *
	 * @param cls the class to look
	 * @param signature the method signature; the return type is optional
	 * @param params the collection to hold the parameter names returned;
	 * null means no parameter names to return
	 */
	public static final Method
	getMethodBySignature(Class cls, String signature, Collection params)
	throws NoSuchMethodException, ClassNotFoundException {
		MethodInfo mi = parseMethod(signature);

		LinkedList argTypes = new LinkedList();
		for (int i = 0; i < mi.argTypes.length; i++) {
			argTypes.add(forNameByThread(mi.argTypes[i]));
			if (params != null)
				params.add(mi.argNames[i]);	//param name found
		}
		
		return getMethodInPublic(cls, mi.method,
			(Class[])argTypes.toArray(new Class[argTypes.size()]));
	}

	/**
	 * Gets the method that is declared in a public class/interface.
	 *
	 * <p>Class.getMethod returns a public method but the class itself
	 * might not be public. However, in many cases, that class
	 * also implements a public interface or class.
	 *
	 * <p>This method will search all its public classes to look for
	 * the method that is 'real' public.
	 *
	 * <p>NoSuchMethodException is thrown if no public 
	 * class/interface is found to have the method.
	 */
	public static final Method
	getMethodInPublic(Class cls, String name, Class[] argTypes)
	throws NoSuchMethodException {
		final Method m = cls.getMethod(name, argTypes);
		if (Modifier.isPublic(m.getDeclaringClass().getModifiers()))
			return m;

		final Class[] clses = cls.getInterfaces();
		for (int j = 0; j< clses.length; ++j)
			try {
				return getMethodInPublic(clses[j], name, argTypes);
			} catch (NoSuchMethodException ex) { //ignore it
			}

		final Class basecls = cls.getSuperclass();
		if (basecls != null)
			try {
				return getMethodInPublic(basecls, name, argTypes);
			} catch (NoSuchMethodException ex) { //ignore it
			}

		throw new NoSuchMethodException(cls+": "+name+" "+Objects.toString(argTypes));
	}

	/** Gets one of the close method by specifying the arguments, rather
	 * than the argument types. It actually calls {@link #getCloseMethod}.
	 */
	public static final Method
	getMethodByObject(Class cls, String name, Object[] args)
	throws NoSuchMethodException {
		if (args == null)
			return getMethodInPublic(cls, name, null);

		final Class[] argTypes = new Class[args.length];
		for (int j = 0; j < args.length; ++j)
			argTypes[j] = args[j] != null ? args[j].getClass(): null;
		return getCloseMethod(cls, name, argTypes);
	}
	/**
	 * Gets one of the close methods -- a close method is a method
	 * with the same name and the compatible argument type.
	 * By compatiable we mean the real method's argument type is
	 * the same as or a superclass of the specified one.
	 *
	 * <p>It might not be the best fit one, unless there is a method
	 * whose argument types are exactly argTypes.
	 *
	 * <p>You might specify the exact number in argTypes. If any of them is
	 * unknwon, use null. Example, in the following, the first argument could
	 * be anything and the second is anything deriving from MyClass:<br>
	 * <code>new Class[] {null, MyClass.class}</code>
	 *
	 * <p>Note: if an argument accepts int, then Integer is considered
	 * as compatible (unlike Class.getMethod). So are long, byte...
	 *
	 * <p>A cache mechanism is implemented, so you don't need to cache it
	 * again in the caller.
	 *
	 * @param cls the class to locate the method
	 * @param name the method name
	 * @param argTypes an array of the argument classes;
	 * null to denote no argument at all (i.e., exact match).
	 * Any argTypes[i] could be null to denote any class.
	 * @return the method
	 * @exception NoSuchMethodException if the method is not found
	 */
	public static final Method
	getCloseMethod(Class cls, String name, Class[] argTypes)
	throws NoSuchMethodException {
		if (argTypes == null || argTypes.length == 0)
			return getMethodInPublic(cls, name, null);

		final AOInfo aoi = new AOInfo(cls, name, argTypes, 0);
		Method m = (Method)_closms.get(aoi);
		if (m != null)
			return m;

		m = myGetCloseMethod(cls, name, argTypes, false);
		_closms.put(aoi, m);
		return m;
	}
	/**
	 * Like {@link #getCloseMethod} to get a 'close' method, but
	 * it look for subclass of the arguement (instead of superclass).
	 * In other words, it looks for the method whose argument type is
	 * the same as or a subclass of the specified one.
	 */
	public static final Method
	getCloseMethodBySubclass(Class cls, String name, Class[] argTypes)
	throws NoSuchMethodException {
		if (argTypes == null || argTypes.length == 0)
			return getMethodInPublic(cls, name, null);

		final AOInfo aoi = new AOInfo(cls, name, argTypes, B_BY_SUBCLASS);
		Method m = (Method)_closms.get(aoi);
		if (m != null)
			return m;

		m = myGetCloseMethod(cls, name, argTypes, true);
		_closms.put(aoi, m);
		return m;
	}
	private static Cache _closms = new MultiCache(20, 1024, 4*60*60*1000);
	private static final Method
	myGetCloseMethod(final Class cls, final String name,
	final Class[] argTypes, final boolean bySubclass)
	throws NoSuchMethodException {
		assert D.OFF || argTypes != null: "Caller shall handle null";
		for (int j = 0;; ++j) {
			if (j == argTypes.length) {//all argTypes[j] non-null
				try {
					return getMethodInPublic(cls, name, argTypes);
				} catch (NoSuchMethodException ex) { //ignore it
					break;
				}
			}
			if (argTypes[j] == null) //specil handling required
				break;
		}

		final Method [] ms = cls.getMethods();
		for (int j = 0; j < ms.length; ++j) {
			if (!ms[j].getName().equals(name))
				continue;

			final Class[] mTypes = ms[j].getParameterTypes();
			if (mTypes.length != argTypes.length)
				continue; //not matched

			final boolean bPublic =
				Modifier.isPublic(ms[j].getDeclaringClass().getModifiers());
			for (int k = 0;; ++k) {
				if (k == argTypes.length) { //all matched
					if (bPublic)
						return ms[j];
					try {
						return getMethodInPublic(
							cls, ms[j].getName(), ms[j].getParameterTypes());
					} catch (NoSuchMethodException ex) {
					}
					break;//not match; look for next
				}

				final Class argType = argTypes[k], mType = mTypes[k];
				if (argType == null
				|| (!bySubclass && mType.isAssignableFrom(argType))
				|| (bySubclass && argType.isAssignableFrom(mType)))
					continue; //match

				final Class c = Primitives.toPrimitive(argType);
				if (c == null || !c.equals(mType))
					break; //not match
			}
		}
		throw new NoSuchMethodException(cls+": "+name+" argTypes: "+Objects.toString(argTypes));
	}

	/** Returns all close methods that match the specified condition, or
	 * a zero-length array if none is found.
	 * <p>Unlike {@link #getCloseMethod}, we don't cache the searched result,
	 * and it won't throw any exception.
	 */
	public static final
	Method[] getCloseMethods(Class cls, String name, Class[] argTypes) {
		if (argTypes == null || argTypes.length == 0) {
			try {
				return new Method[] {getMethodInPublic(cls, name, null)};
			} catch (NoSuchMethodException ex) {
				return new Method[0];
			}
		}
		return myGetCloseMethods(cls, name, argTypes, false);
	}
	/**
	 * Like {@link #getCloseMethods} to get all 'close' methods, but
	 * it look for subclass of the arguement (instead of superclass).
	 * In other words, it looks for the method whose argument type is
	 * the same as or a subclass of the specified one.
	 */
	public static final
	Method[] getCloseMethodsBySubclass(Class cls, String name, Class[] argTypes) {
		if (argTypes == null || argTypes.length == 0)
			return getCloseMethods(cls, name, null);
		return myGetCloseMethods(cls, name, argTypes, true);
	}
	private static final Method[]
	myGetCloseMethods(final Class cls, final String name,
	final Class[] argTypes, final boolean bySubclass) {
		assert D.OFF || argTypes != null: "Caller shall handle null";
		final List mtds = new LinkedList();
		final Method [] ms = cls.getMethods();
		for (int j = 0; j < ms.length; ++j) {
			if (!ms[j].getName().equals(name))
				continue;

			final Class[] mTypes = ms[j].getParameterTypes();
			if (mTypes.length != argTypes.length)
				continue; //not matched

			final boolean bPublic =
				Modifier.isPublic(ms[j].getDeclaringClass().getModifiers());
			for (int k = 0;; ++k) {
				if (k == argTypes.length) { //all matched
					if (bPublic) {
						mtds.add(ms[j]);
					} else {
						try {
							mtds.add(getMethodInPublic(
							cls, ms[j].getName(), ms[j].getParameterTypes()));
						} catch (NoSuchMethodException ex) { //ignore; not add
						}
					}
					break; //found; next method
				}

				final Class argType = argTypes[k], mType = mTypes[k];
				if (argType == null
				|| (!bySubclass && mType.isAssignableFrom(argType))
				|| (bySubclass && argType.isAssignableFrom(mType)))
					continue; //match one argument

				final Class c = Primitives.toPrimitive(argType);
				if (c == null || !c.equals(mType))
					break; //not match; next method
			}
		}
		return (Method[])mtds.toArray(new Method[mtds.size()]);
	}

	/**
	 * Search the get method; not imply B_METHODONLY.
	 */
	public static final int B_GET=0; //must be 0
	/**
	 * Search the set method; not imply B_METHODONLY.
	 */
	public static final int B_SET=1; //must be 1
	/**
	 * Only search for public methods or fields.
	 */
	public static final int B_PUBLIC_ONLY=0x0002;
	/**
	 * Only search for methods; excluding fields.
	 */
	public static final int B_METHOD_ONLY=0x0004;

	/** Used by {@link #getCloseMethodBySubclass} to distiquish
	 * {@link #getCloseMethod}.
	 */
	private static final int B_BY_SUBCLASS = 0x1000;

	/**
	 * Gets the specified accessible object, either a method or a field, by
	 * searching the specified name.
	 *
	 * <p>The search sequence is: (assume field name is body)><br>
	 * getBody(...)<br>
	 * isBody(...)<br>
	 * body(...)<br>
	 * body
	 *
	 * <p>If B_SET is specified, setBody(...) is searched instead of
	 * getBody(...) and isBody(...). The field is searched only if
	 * argsType.length is 0 or 1.
	 *
	 * <p>Note: it uses {@link #getCloseMethod} to get the method.
	 *
	 * <p>A cache mechanism is implemented, so you don't need to cache it
	 * again in the caller.
	 *
	 * @param cls the class to find
	 * @param name the name of the accessible object
	 * @param argTypes the parameter type of the method to find
	 * @param flags a combination of B_xxx or zero
	 * @return the accessible object; either Field or Method
	 * @exception NoSuchMethodException if neither the set method of
	 *   specified field nor the field itself not found
	 * @exception SecurityException if access to the information is denied
	 */
	public static final AccessibleObject
	getAccessibleObject(Class cls, String name, Class[] argTypes, int flags)
	throws NoSuchMethodException {
		final AOInfo aoi = new AOInfo(cls, name, argTypes, flags);
		AccessibleObject ao = (AccessibleObject)_acsos.get(aoi);
		if (ao != null)
			return ao;

		ao = myGetAcsObj(cls, name, argTypes, flags);
		_acsos.put(aoi, ao);
		return ao;
	}
	private static Cache _acsos = new MultiCache(20, 600, 4*60*60*1000);
	private static final AccessibleObject
	myGetAcsObj(Class cls, String name, Class[] argTypes, int flags)
	throws NoSuchMethodException {
		//try public set/get
		final String decoratedName =
			toMethodName(name, (flags&B_SET) != 0 ? "set": "get");
		try {
			return getCloseMethod(cls, decoratedName, argTypes);
		} catch (NoSuchMethodException ex) { //ignore
		}

		//try public is
		String isMethodName = null;
		if ((flags&B_SET) == 0)
			try {
				isMethodName = toMethodName(name, "is");
				return getCloseMethod(cls, isMethodName, argTypes);
			} catch (NoSuchMethodException ex) { //ignore
			}

		//try public same
		try {
			return getCloseMethod(cls, name, argTypes);
		} catch (NoSuchMethodException ex) {
			if ((flags & (B_PUBLIC_ONLY|B_METHOD_ONLY))
					  ==(B_PUBLIC_ONLY|B_METHOD_ONLY))
				throw ex;
		}

		if ((flags & B_PUBLIC_ONLY) == 0) {
			//try any set/get
			try {
				return getAnyMethod(cls, decoratedName, argTypes);
			} catch (NoSuchMethodException ex) { //ignore
			}

			//try any is
			if ((flags&B_SET) == 0)
				try {
					return getAnyMethod(cls, isMethodName, argTypes);
				} catch (NoSuchMethodException ex) { //ignore
				}

			//try any same
			try {
				return getAnyMethod(cls, name, argTypes);
			} catch (NoSuchMethodException ex) {
				if ((flags & B_METHOD_ONLY) != 0)
					throw ex;
			}
		}

		if (argTypes != null && argTypes.length > 1)
			throw new NoSuchMethodException(cls+": "+name+" "+Objects.toString(argTypes));

		try {
			//try public field
			try {
				return cls.getField(name);
			} catch (NoSuchFieldException ex) { //ignore
				if ((flags & B_PUBLIC_ONLY) != 0)
					throw ex;
			}

			//try any field
			return getAnyField(cls, name);
		} catch (NoSuchFieldException ex) { //ignore
			throw new NoSuchMethodException(cls+": name="+name+" args="+Objects.toString(argTypes));
		}
	}
	/** The infomation of the access object. */
	private static class AOInfo {
		private Class cls;
		private String name;
		private Class[] argTypes;
		private int flags;
		private AOInfo(Class cls, String name, Class[] argTypes, int flags) {
			this.cls = cls;
			this.name = name;
			this.argTypes = argTypes;
			this.flags = flags;
		}
		public int hashCode() {
			return cls.hashCode() + name.hashCode() + flags;
		}
		public boolean equals(Object o) {
			if (!(o instanceof AOInfo))
				return false;

			AOInfo aoi = (AOInfo)o;
			int len =  argTypes != null ? argTypes.length: 0;
			int len2 = aoi.argTypes != null ? aoi.argTypes.length: 0;
			if (len != len2)
				return false;

			if (flags != aoi.flags || !cls.equals(aoi.cls)
			|| !name.equals(aoi.name))
				return false;

			for (int j = 0; j < len; ++j)
				if (!Objects.equals(argTypes[j], aoi.argTypes[j]))
					return false;
			return true;
		}
	}

	/**
	 * Gets the specified method by searching all methods including
	 * <i>any</i> access control and any base class.
	 * Note: you rarely need to call this metod. In most cases,
	 * Class.getMethod, {@link #getCloseMethod}, and
	 * {@link #getMethodInPublic} are what you need.
	 *
	 * <p>The search sequence is: this class's methods, and then
	 * the superclass's methods.
	 *
	 * <p>Note: public methods don't be treated different. If the caller
	 * wants to search public methods first, it has to call Class.getMethod
	 * first.
	 *
	 * @param cls the class to search
	 * @param name the method name
	 * @param argTypes the parameter array of types
	 * @return the Method object
	 * @exception NoSuchMethodException if a matching method is not found.
	 * @exception SecurityException if access to the information is denied.
	 * @see #getAccessibleObject(Class, String, Class[], int)
	 * @see #getAnyField(Class, String)
	 */
	public static final Method
	getAnyMethod(Class cls, String name, Class[] argTypes)
	throws NoSuchMethodException {
		try {
			return cls.getDeclaredMethod(name, argTypes);
		} catch (NoSuchMethodException ex) {
			final Class[] clses = cls.getInterfaces();
			for (int j = 0; j< clses.length; ++j)
				try {
					return getAnyMethod(clses[j], name, argTypes);
				} catch (NoSuchMethodException e2) { //ignore it
				}

			cls = cls.getSuperclass();
			if (cls == null)
				throw ex;
			return getAnyMethod(cls, name, argTypes);
		}
	}

	/**
	 * Gets the specified field by searching all fields including
	 * any access control and any base class.
	 * The search sequence is: this class's fields, and then
	 * the superclass's fields.
	 *
	 * <p>Note: public fields don't be treated different. If the caller
	 * wants to search public fields first, it has to call Class.getField
	 * first.
	 *
	 * @param cls the class to search
	 * @param name the field name
	 * @return the Field object
	 * @exception NoSuchFieldException if a matching field is not found.
	 * @exception SecurityException if access to the information is denied.
	 * @see #getAccessibleObject(Class, String, Class[], int)
	 * @see #getAnyMethod(Class, String, Class[])
	 */
	public static final Field getAnyField(Class cls, String name)
	throws NoSuchFieldException {
		for (;;) {
			try {
				return cls.getDeclaredField(name);
			} catch (NoSuchFieldException ex) {
				cls = cls.getSuperclass();
				if (cls == null)
					throw ex;
			}
		}
	}

	/**
	 * Searches thru each element of the specified array of classes, and
	 * returns classes that are super-classes (or equal) of
	 * the sepcified class.
	 *
	 * @param cls the specified class; null is not allowed
	 * @param clsToCheck the class array to check; null is acceptable
	 * @return a subset of clsToCheck that are super-class of cls;
	 * null if no one qualified
	 */
	public static final Class[] getSuperClasses(Class cls, Class[] clsToCheck) {
		if (clsToCheck!=null) {
			int[] hits = new int[clsToCheck.length];
			int no = 0;
			for (int j=0; j<clsToCheck.length; ++j)
				if (clsToCheck[j].isAssignableFrom(cls))
					hits[no++] = j;

			if (no != clsToCheck.length) {
				if (no == 0)
					return null;
				Class[] exc = new Class[no];
				for (int j=0; j<no; ++j)
					exc[j] = clsToCheck[hits[j]];
				return exc;
			}
		}
		return clsToCheck;
	}

	/**
	 * Check whether the specified class is a primitive or a primitive wrapper.
	 */
	public static final boolean isPrimitiveWrapper(Class cls) {
		return Objects.equals(cls.getPackage(), Boolean.class.getPackage())
			&& (cls.equals(Boolean.class) || cls.equals(Byte.class)
				|| cls.equals(Character.class) || cls.equals(Double.class)
				|| cls.equals(Float.class) || cls.equals(Integer.class)
				|| cls.equals(Long.class) || cls.equals(Short.class));
	}
	/** Checks whether the specified class is a numeric class.
	 *
	 * @param extend whether to consider Date, char, boolean, Character
	 * and Boolean as a numeric object.
	 */
	public static final boolean isNumeric(Class cls, boolean extend) {
		if (cls.isPrimitive())
			return extend ||
				(!cls.equals(char.class) && !cls.equals(boolean.class));

		if (Number.class.isAssignableFrom(cls))
			return true;

		return extend &&
			(cls.equals(Date.class) || cls.equals(Boolean.class)
			|| cls.equals(Character.class));
	}

	/** Converts an object to the specified class.
	 * It is the same as coerce(cls, val, true).
	 *
	 * <p>Future: use org.apache.commons.el.Coercions
	 *
	 * @param val the value.
	 * @exception ClassCastException if failed to convert
	 * @see #coerce(Class, Object, boolean)
	 */
	public static Object coerce(Class cls, Object val)
	throws ClassCastException {
		if (cls.isInstance(val))
			return val;

		if (String.class == cls) {
			return Objects.toString(val);
		} else if (BigDecimal.class == cls) {
			if (val == null) {
				return null;
			} else if (val instanceof Double) {
				return new BigDecimal(((Double)val).doubleValue());
			} else if (val instanceof Float) {
				return new BigDecimal(((Float)val).doubleValue());
			} else if (val instanceof BigInteger) {
				return new BigDecimal((BigInteger)val);
			} else if (val instanceof Number) {
				return BigDecimals.toBigDecimal(((Number)val).intValue());
			} else if (val instanceof String) {
				return new BigDecimal((String)val);
			} else if (val instanceof Date) {
				return new BigDecimal(((Date)val).getTime());
			}
		} else if (Integer.class == cls || int.class == cls) {
			if (val == null) {
				return Integer.class == cls ? null: Objects.ZERO_INTEGER;
			} else if (val instanceof Integer) { //int.class
				return val;
			} else if (val instanceof Number) {
				return new Integer(((Number)val).intValue());
			} else if (val instanceof String) {
				return new Integer((String)val);
			}
		} else if (Boolean.class == cls || boolean.class == cls) {
			if (val == null) {
				return Boolean.class == cls ? null: Boolean.FALSE;
			} else if (val instanceof Boolean) { //boolean.class
				return val;
			} else if (val instanceof String) {
				return Boolean.valueOf((String)val);
			} else if (val instanceof BigDecimal) {
				return Boolean.valueOf(((BigDecimal)val).signum() != 0);
			} else if (val instanceof BigInteger) {
				return Boolean.valueOf(((BigInteger)val).signum() != 0);
			} else if (val instanceof Number) {
				return Boolean.valueOf(((Number)val).intValue() != 0);
			} else {
				return Boolean.TRUE; //non-null is true
			}
		} else if (Short.class == cls || short.class == cls) {
			if (val == null) {
				return Short.class == cls ? null: Objects.ZERO_SHORT;
			} else if (val instanceof Short) { //short.class
				return val;
			} else if (val instanceof Number) {
				return new Short(((Number)val).shortValue());
			} else if (val instanceof String) {
				return new Short((String)val);
			}
		} else if (Long.class == cls || long.class == cls) {
			if (val == null) {
				return Long.class == cls ? null: Objects.ZERO_LONG;
			} else if (val instanceof Long) { //long.class
				return val;
			} else if (val instanceof Number) {
				return new Long(((Number)val).longValue());
			} else if (val instanceof String) {
				return new Long((String)val);
			} else if (val instanceof Date) {
				return new Long(((Date)val).getTime());
			}
		} else if (Double.class == cls || double.class == cls) {
			if (val == null) {
				return Double.class == cls ? null: Objects.ZERO_DOUBLE;
			} else if (val instanceof Double) { //double.class
				return val;
			} else if (val instanceof Number) {
				return new Double(((Number)val).doubleValue());
			} else if (val instanceof String) {
				return new Double((String)val);
			} else if (val instanceof Date) {
				return new Double(((Date)val).getTime());
			}
		} else if (BigInteger.class == cls) {
			if (val == null) {
				return null;
			} else if (val instanceof Integer) {
				return BigIntegers.toBigInteger((Integer)val);
			} else if (val instanceof Short) {
				return BigIntegers.toBigInteger((Short)val);
			} else if (val instanceof Byte) {
				return BigIntegers.toBigInteger((Byte)val);
			} else if (val instanceof Number) {
				return BigIntegers.toBigInteger(((Number)val).longValue());
			} else if (val instanceof String) {
				return new BigInteger((String)val);
			} else if (val instanceof Date) {
				return BigIntegers.toBigInteger(((Date)val).getTime());
			}
		} else if (Float.class == cls || float.class == cls) {
			if (val == null) {
				return Float.class == cls ? null: Objects.ZERO_FLOAT;
			} else if (val instanceof Float) { //float.class
				return val;
			} else if (val instanceof Number) {
				return new Float(((Number)val).floatValue());
			} else if (val instanceof String) {
				return new Float((String)val);
			} else if (val instanceof Date) {
				return new Float(((Date)val).getTime());
			}
		} else if (Byte.class == cls || byte.class == cls) {
			if (val == null) {
				return Byte.class == cls ? null: Objects.ZERO_BYTE;
			} else if (val instanceof Byte) { //byte.class
				return val;
			} else if (val instanceof Number) {
				return new Byte(((Number)val).byteValue());
			} else if (val instanceof String) {
				return new Byte((String)val);
			}
		} else if (Character.class == cls || char.class == cls) {
			if (val == null) {
				return Character.class == cls ? null: Objects.NULL_CHARACTER;
			} else if (val instanceof Character) { //character.class
				return val;
			} else if (val instanceof Number) {
				return new Character((char)((Number)val).shortValue());
			} else if (val instanceof String) {
				final String s = (String)val;
				return s.length() > 0 ? new Character(s.charAt(0)): Objects.NULL_CHARACTER;
			}
		} else if (Date.class == cls) {
			if (val == null) {
				return null;
			} else if (val instanceof Number) {
				return new Date(((Number)val).longValue());
			}
		} else if (Number.class == cls) {
			if (val == null) {
				return null;
			} else if (val instanceof String) {
				return new BigDecimal((String)val);
			} else if (val instanceof Date) {
				return new BigDecimal(((Date)val).getTime());
			}
		} else {
			if (val == null) {
				return null;
			} else {
				try {
					return newInstance(cls, new Object[] {val});
				} catch (Exception ex) {
					final ClassCastException t =
						new ClassCastException(
							Messages.get(MCommon.CLASS_NOT_COMPATIABLE,
							new Object[] {val.getClass(), cls}));
					t.initCause(ex);
					throw t;
				}
			}
		}

		throw new ClassCastException(
			Messages.get(MCommon.CLASS_NOT_COMPATIABLE,
			new Object[] {
				val != null ? val+"("+val.getClass().getName()+")": "null",
				cls}));
	}
	/** Converts to the specified type.
	 *
	 * @param nullable whether the result could be null.
	 * If false, 0 is used for number, the default constructor is used
	 * for others. {@link #coerce(Class, Object)} is a special case that
	 * is equivalent to nullable=true.
	 * @exception ClassCastException if failed to convert
	 */
	public static Object coerce(Class cls, Object val, boolean nullable)
	throws ClassCastException {
		if (nullable || val != null)
			return coerce(cls, val);

		if (BigDecimal.class == cls) {
			return Objects.ZERO_BIG_DECIMAL;
		} else if (Integer.class == cls || int.class == cls) {
			return Objects.ZERO_INTEGER;
		} else if (Boolean.class == cls || boolean.class == cls) {
			return Boolean.FALSE;
		} else if (Short.class == cls || short.class == cls) {
			return Objects.ZERO_SHORT;
		} else if (Long.class == cls || long.class == cls) {
			return Objects.ZERO_LONG;
		} else if (Double.class == cls || double.class == cls) {
			return Objects.ZERO_DOUBLE;
		} else if (Byte.class == cls || byte.class == cls) {
			return Objects.ZERO_BYTE;
		} else if (BigInteger.class == cls) {
			return Objects.ZERO_BIG_INTEGER;
		} else if (Float.class == cls || float.class == cls) {
			return Objects.ZERO_FLOAT;
		} else if (Character.class == cls || char.class == cls) {
			return Objects.NULL_CHARACTER;
		} else {
			try {
				return newInstance(cls, new Object[] {val});
			} catch (Exception ex) {
				final ClassCastException t =
					new ClassCastException(
						Messages.get(MCommon.CLASS_NOT_COMPATIABLE,
						new Object[] {"null", cls}));
				t.initCause(ex);
				throw t;
			}
		}
	}
}
