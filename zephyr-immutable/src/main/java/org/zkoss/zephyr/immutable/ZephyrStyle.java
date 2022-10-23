/* ZephyrStyle.java

	Purpose:
		
	Description:
		
	History:
		4:09 PM 2021/9/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.immutable;

/**
 * An annotation of a customize immutable style.
 *
 * @author jumperchen
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.immutables.value.Value;

@Target(ElementType.TYPE)
@Value.Style(
		visibility = Value.Style.ImplementationVisibility.PACKAGE,
		defaultAsDefault = true,
		get = {"get*", "is*"},
		init = "set*",
		overshadowImplementation = true,
		passAnnotations = {ZephyrOnly.class},
		defaults = @Value.Immutable())
public @interface ZephyrStyle {
}
