/* BindingParams.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 09 11:46:49 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify the name of a parameter of a method.<br/>
 * The value of this parameter, which is a Bean object, will be created
 * with properties from the current binding arguments.
 *
 * Example:
 * <pre><code>
 * {@code
 * POJO:
 * public class Product {
 *     private int id;
 *     private String label;
 *     // Getters & setters are omitted
 * }
 *
 * ZUL:
 * <button onClick="@command('addProduct', id=1, label='Water')"/>
 *
 * VM:
 * public class MyVM {
 *   @Command
 *   public void addProduct(@BindingParams Product product) {
 *   }
 * }}</code></pre>
 * 
 * @see Init
 * @see Command
 * @see BindingParam
 * @author rudyhuang
 * @since 9.2.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindingParams {
}
