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
 * Marker annotation to identify a MatchMedia method. <br/>
 * A MatchMedia method could also use with {@link ContextParam} to get the ClientInfoEvent. <br/>
 * The annotated methods are not in any particular order if the intervals are overlapping . <br/>
 *
 *
 * @see ContextParam
 * @author wenninghsu
 * @since 8.0.2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchMedia {
    String[] value();
}
