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
 * if you want binder to call super calss's initial method also, you have to set {@link #upward()} to true, 
 * and super class's initial method will be called first.
 * 
 * For example, in class hierarchy A(has init) &lt;- B(has init) &lt;- C(no init) &lt;- D (has init, upward true).  D is the last one. <br/> 
 * If D is the view model, will call D.init<br/>
 * If C is the view model, no method will be called br/&gt; 
 * If B is the view model, will call A.init then B.init  <br/> 
 * If A is the view model, will call A.init  <br/> 
 * 
 * Note that, if you override a method, which is also the init method of the super class. ex, X.m1() &lt;- Y.m1() <br/>   
 * Because of the java limitation, you should set upward to true in Y.m1() or Y.m1() will be called twice. <br/> 
 * 
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
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
	boolean upward() default false;
}
