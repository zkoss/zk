/* Destroy.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 16 3:21 PM:13 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marker annotation to identify a destroy method. <br/>
 * Only one (could be zero) destroy method is allowed in a particular class.
 * The destroy method would occur when a component binded with a view model is detached;
 * After detaching, it will execute the function which has @Destroy.
 * If you want binder to call super class's Destroy method also, you have to set {@link #superclass()} to true,
 * and super class's Destroy method will be called last.
 * You could annotate it on the type if the class doesn't has a Destroy method but super-class has.
 * </p>
 * <p>
 * For example, in class hierarchy A(has @Destroy) &lt;- B(no @Destroy) &lt;- C(has @Destroy, superclass true) &lt;- D (has @Destroy, superclass false).  D is the last one. <br/>
 * If A is the view model, will call A.Destroy only<br/>
 * If B is the view model, no method will be called <br/>
 * If C is the view model, will call C.Destroy  <br/>
 * If D is the view model, will call D.Destroy then C.Destroy  <br/>
 * </p>
 *
 * <p> <b>Exception</b>: if {@link #superclass()} was been set to true and your initial method
 * is an overridden method to it's super's initial method, ex:<br>
 * <code> X.m1() &lt;- Y.m1() </code><br>
 * Binder will throw an exception due to the conflict of java language's overriding nature.<br>
 * </p>
 *
 * <code>@Destroy public void doDestroy()</code><br>
 *
 * @author klyve
 * @since 8.5.2
 */

@Target ({ ElementType.METHOD, ElementType.TYPE })
@Retention (RetentionPolicy.RUNTIME)
public @interface Destroy {
	boolean superclass() default false;
}
