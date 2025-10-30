/* ParameterReflectionParanamer.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 18 09:42:07 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.paranamer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Use the Parameter reflection API since Java 8 to discover parameter names.
 *
 * Need to enable {@code -parameters} compiler flag.
 *
 * @author rudyhuang
 */
public class ParameterReflectionParanamer implements Paranamer {
	@Override
	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return lookupParameterNames(methodOrConstructor, true);
	}

	@Override
	public String[] lookupParameterNames(AccessibleObject methodOrConstructor,
	                                     boolean throwExceptionIfMissing) {
		final String[] names;
		if (methodOrConstructor instanceof Executable) {
			Parameter[] parameters = ((Executable) methodOrConstructor).getParameters();
			names = Arrays.stream(parameters).allMatch(Parameter::isNamePresent)
					? Arrays.stream(parameters).map(Parameter::getName).toArray(String[]::new)
					: null;
		} else {
			names = null;
		}
		if (names == null) {
			if (throwExceptionIfMissing)
				throw new ParameterNamesNotFoundException(
						"No parameter names found for this method or constructor: " + methodOrConstructor);
			return EMPTY_NAMES;
		}
		return names;
	}
}
