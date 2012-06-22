/* AfterCompose.java

	Purpose:
		
	Description:
		
	History:
		Jun 20, 2012, Created by Ian YT Tsai(Zanyking)

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marker annotation to identify a life-cycle method of a 
 * View Model which will be called after host component's composition (AfterCompose).<br>
 * Only one method could be tagged with this annotation.<br>
 * </p>
 * <p><b>Inheritance:</b>
 * If you want binder to call super calss's afterCompose method also, you have to set 
 * {@link #superclass()} to true, and super class's afterCompose method will be called first.<br>
 * 
 * In the other hand, if target bean class doesn't has an afterCompose method but it's super has, 
 * you'll have to annotate {@link AfterCompose} on the bean type. 
 * </p>
 *  
 * <p> 
 * For example, in class hierarchy A(has @AfterCompose) &lt;- B(has @AfterCompose, superclass true) &lt;- C(no @AfterCompose) &lt;- D (has @AfterCompose, superclass false).  D is the last one. <br/> 
 * If D is the view model, will call D.afterCompose only<br/>
 * If C is the view model, no method will be called <br/> 
 * If B is the view model, will call A.afterCompose then B.afterCompose  <br/> 
 * If A is the view model, will call A.afterCompose  <br/> 
 * </p>
 * 
 * <p> <b>Exception</b>: if {@link #superclass()} was been set to true and your afterCompose method 
 * is an overridden method to it's super's afterCompose method, ex:<br>
 * <code> X.m1() &lt;- Y.m1() </code><br>
 * Binder will throw an exception due to the conflict of java language's overriding nature.<br> 
 * </p>
 * 
 * <p> <b>Parameter Binding</b>: for convenience, afterCompose method support several kinds of 
 * Parameter Annotations.  Binder will weave it's context(Zul page annotation, Java EE Context) 
 * with method's parameters while invocation.<br>
 * The difference between @afterCompose and @Init is - afterCompose has no zul declaration's part 
 * by it's self. Instead, it will share BindingParam's with @init's zul declaration. 
 * An example of afterCompose method signature might be looks like this:<br>
 * 
 * <code>viewModel="@id('vm') @Init('BlaBlaVM', a='b')"</code><br>
 * <code>@AfterCompose public void doAfterCompose(@BindingParam("a")@Default("d") String a)</code><br>
 * 
 * 
 * 
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
 * @see Init
 * 
 * @author Ian Y.T Tsai(zanyking)
 * @since 6.0.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterCompose {
	boolean superclass() default false;
}
