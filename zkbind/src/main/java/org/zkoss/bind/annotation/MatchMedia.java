/* MatchMedia.java

	Purpose:

	Description:

	History:
		Feb 18, 2016 12:50:02 PM, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Marker annotation to identify a MatchMedia method in View Model. <br/>
 * A MatchMedia method could also use with {@link ContextParam} to get the {@link org.zkoss.zk.ui.event.ClientInfoEvent}. <br/>
 * The annotated methods are not in any particular order if the intervals are overlapping.<br/>
 * </p>
 * <p>This annotation is for RWD in zk mvvm.
 * </p>
 * <p>Example:<br/>
 * <code>@MatchMedia("all and (max-width: 500px)")</code><br/>
 * <code>public void method(@ContextParam(ContextType.TRIGGER_EVENT) ClientInfoEvent event)</code>
 * </p>
 * <p>When the media query matches the value in the annotation, it'll trigger a {@link org.zkoss.zk.ui.event.ClientInfoEvent} and the annotated method in the viewmodel will be invoked .<br/>
 * You could get the client's information by {@link ContextParam}
 * </p>
 * <p>The values of this annotation should be unique in each viewmodel, and has to follow the criteria which is listed in <a href="http://www.w3schools.com/cssref/css3_pr_mediaquery.asp">this media query's spec</a>
 * </p>
 *
 * @author wenninghsu
 * @since 8.0.2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchMedia {
    String[] value();
}
