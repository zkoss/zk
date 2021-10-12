/** AutoNotifyChange.java.

 	Purpose:

 	Description:

 	History:
 		Wed Dec 26 11:00:32 CST 2017, Created by jameschu

 	Copyright (C) 2017 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable posting NotifyChange when setter method called.
 * <p> If the view model class is annotated with this annotation, every setter method in @Command and @GlobalCommand will
 * post NotifyChange after being called.
 * </p>
 * @author jameschu
 * @since 8.5.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoNotifyChange {
}
