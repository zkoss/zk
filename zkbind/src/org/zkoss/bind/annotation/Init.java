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
 * Marker annotation to identify a init method. &lt;br/&gt;
 * Binder calls the method that has this annotation by the order (super first) of the class hierarchy. &lt;br/&gt;
 * Only one (could be zero) initial method is allowed in a particular class, and the super class could have another initial method. &lt;br/&gt;
 * You could disable invoking the initial method of super class by setting {@link #upward()} to false. &lt;br/&gt;
 * For example, in class hierarchy A(has init) &lt;- B(upward=false) &lt;- C(no init) &lt;- D (has init).  D is the last one. &lt;br/&gt; 
 * If D is the view model, will call B.init then D.init, ignores A.init and C since it doesn't has init method.  &lt;br/&gt; 
 * If C or B is the view model, will call B.init  &lt;br/&gt; 
 * If A is the view model, will call A.init  &lt;br/&gt; 
 * 
 * Note that, if you override a method, which is also the init method of the super class. ex, X.m1() &lt;- Y.m1()  
 * Because of the java limitation, binder has no way to call X.m1(), and Y.m1() will be called twice.
 * To avoid this, you should set {@link #upward()} to false of Y.m1() and call super.m1() inside it.
 * 
 * A initial method could also use with {@link BindingParam} and others to assign a argument as its parameter,  
 * and {@link Default} to assign a default value if the argument is null. &lt;br/&gt;
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
	boolean upward() default true;
}
