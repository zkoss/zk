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
 * Marker annotation to identify a initial method. <br/>
 * Only one (could be zero) initial method is allowed in a particular class.
 * If you want binder to call super calss's initial method also, you have to set {@link #superclass()} to true, 
 * and super class's initial method will be called first. 
 * You could annotate it on the type if the class doesn't has a init method but super-class has. 
 * <p/>
 * 
 * For example, in class hierarchy A(has @Init) &lt;- B(has @Init, superclass true) &lt;- C(no @Init) &lt;- D (has @Init, superclass false).  D is the last one. <br/> 
 * If D is the view model, will call D.init only<br/>
 * If C is the view model, no method will be called <br/> 
 * If B is the view model, will call A.init then B.init  <br/> 
 * If A is the view model, will call A.init  <br/> 
 * <p/>
 * Note that, if you override a method, which is both the init method of the the class and its super class. ex, X.m1() &lt;- Y.m1() <br/>   
 * Binder will throw a exception (or the Y.m1() will be called twice because of the java method overriding spec)<br/> 
 * <p/>
 * A initial method could also use with {@link BindingParam} and others to assign a argument as its parameter,  
 * and {@link Default} to assign a default value if the argument is null. <br/>
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
 * @author dennis
 * @since 6.0.0
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
	boolean superclass() default false;
}
