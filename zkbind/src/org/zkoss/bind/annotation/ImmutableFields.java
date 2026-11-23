/* ImmutableFields.java

	Purpose:
		
	Description:
		
	History:
		11:13 AM 1/5/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to indicate all of the fields returned from the given class
 * are immutable (no setter). Unlike {@link Immutable}, this annotation can be made as a proxy
 * object for Form Binding, but not for all its fields.
 * <br/>
 * For example,
 * <pre>
 * public class VM {
 *	{@literal @}ImmutableFields
 * 	public Foo getFoo() {} // this Foo can make as a proxy, but not for its getDate() method.
 * }
 *
 * public class Foo {
 *	public Date getDate() {} // this date object cannot make as a proxy.
 * }
 * </pre>
 * @author jumperchen
 * @since 8.0.1
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ImmutableFields {
}
