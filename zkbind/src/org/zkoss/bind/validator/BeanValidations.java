/* BeanValidations.java

	Purpose:
		
	Description:
		
	History:
		2011/12/22 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.validator;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * A utility class to help jsr 303 validation.
 * @author dennis
 * @since 6.0.0
 */
public class BeanValidations {
	// from javadoc, it is thread safe.
	private static volatile Validator _validator;

	private static ValidatorFactory buildFactory() {
		// TODO from configuration
		return Validation.buildDefaultValidatorFactory();
	}

	public static Validator getValidator() {
		if (_validator == null) {
			synchronized (BeanValidations.class) {
				if (_validator == null) { // check again
					_validator = buildFactory().getValidator();
				}
			}
		}
		return _validator;
	}

	public static <T> Set<ConstraintViolation<T>> validate(Class<T> clazz, String propName, Object value) {
		Set<ConstraintViolation<T>> set = getValidator().validateValue(clazz, propName, value);
		return set;
	}
}
