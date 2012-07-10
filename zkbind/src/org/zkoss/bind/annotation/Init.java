/* Init.java

	Purpose:
		
	Description:
		
	History:
		2011/11/30 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marker annotation to identify a initial method. <br/>
 * Only one (could be zero) initial method is allowed in a particular class.
 * If you want binder to call super calss's initial method also, you have to set {@link #superclass()} to true, 
 * and super class's initial method will be called first. 
 * You could annotate it on the type if the class doesn't has a init method but super-class has. 
 * </p>
 * <p>
 * For example, in class hierarchy A(has @Init) &lt;- B(has @Init, superclass true) &lt;- C(no @Init) &lt;- D (has @Init, superclass false).  D is the last one. <br/> 
 * If D is the view model, will call D.init only<br/>
 * If C is the view model, no method will be called <br/> 
 * If B is the view model, will call A.init then B.init  <br/> 
 * If A is the view model, will call A.init  <br/> 
 * </p>
 * 
 * <p> <b>Exception</b>: if {@link #superclass()} was been set to true and your initial method 
 * is an overridden method to it's super's initial method, ex:<br>
 * <code> X.m1() &lt;- Y.m1() </code><br>
 * Binder will throw an exception due to the conflict of java language's overriding nature.<br> 
 * </p>
 * 
 * <p> <b>Parameter Binding</b>, for convenience, initial method support several kinds of 
 * Parameter Annotations. Binder will weave it's context(Zul page annotation, Java EE Context) 
 * with method's parameters while invocation.<br>
 * For example, you can wire @init('BlaBlaVM', a='b') to an initial method like:<br>
 * <code>@Init public void doInit(@BindingParam("a") String a)</code><br>
 *    
 * </p>
 * @see BindingParam
 * @see ExecutionParam
 * @see ExecutionArgParam
 * @see HeaderParam
 * @see CookieParam
 * @see QueryParam
 * @see ScopeParam
 * @see ContextParam
 * @see Default
 * 
 * @see AfterCompose
 * 
 * @author dennis
 * @since 6.0.0
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
	boolean superclass() default false;
}
