/* BeanValidations.java

	Purpose:
		
	Description:
		
	History:
		2011/12/22 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * A utility class to help jsr 303 validation.
 * @author dennis
 * @since 6.0.0
 */
public class BeanValidations {
	private BeanValidations() {
		//no instance
	}
	private static class ValidatorHolder {
		private static ValidatorFactory buildFactory() {
			return Validation.buildDefaultValidatorFactory();
		}
		private static final Validator INSTANCE = buildFactory().getValidator();
	}

	public static Validator getValidator() {
		return ValidatorHolder.INSTANCE;
	}

	public static <T> Set<ConstraintViolation<T>> validate(Class<T> clazz, String propName, Object value) {
		return getValidator().validateValue(clazz, propName, value);
	}
}
