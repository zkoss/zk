/* ComponentAnnotation.java

	History:
		Tue Feb 21 19:17:49 TST 2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying ZK component's annotations
 * ({@link org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotations}).
 *
 * <p>For example, you could specify component's annotations as follows:
 * <pre><code>
@ComponentAnnotation(
 "@ZKBIND(ACCESS=both, SAVE_EVENT=onChange, LOAD_REPLACEMENT=rawValue, LOAD_TYPE=java.lang.String")
public String getValue() {
    //...
}</code></pre>
 *
 * <p>The sytax of the value is the same as
 * <a href="http://books.zkoss.org/wiki/ZK%20Developer%27s%20Reference/Annotations/Annotate%20in%20ZUML">ZUML's annotations</a>.
 * Like ZUML, if you have multiple annotations, you can specify it in the same string.
 * For example,
<pre><code>
@ComponentAnnotation("@bind(datasource='author', value='selected') @validate({cond1,cond2})")
public String getValue() {</code></pre>
 *
 * <p>However, for better readability, you can split it into multiple strings.
 * They are equivalent. For example,
<pre><code>
@ComponentAnnotation({
 "@bind(datasource='author', value='selected')"
 "@validate({cond1,cond2})"})
public String getValue() {</code></pre>
 *
 * <p>If the component's Java class doesn't have the getter or setter
 * for the given property, you can specify the annotations at the class level
 * by prefixing the annotation with the property name and a colon. For example,
 * <pre><code>
@ComponentAnnotation({
 "selectedItem: @ZKBIND(ACCESS=both, SAVE_EVENT=onSelect)",
 "openedItem: @ZKBIND(ACCESS=load, LOAD_EVENT=onOpen)")
public class Foo extends AbstractComponent {
    ....
}</code></pre>
 *
 * <p>Notice, only the public getter and setter methods (i.e., getXxx, setXxx and
 * isXxx) are scanned when loading the component annotations.
 * Thus, if the method doesn't fit, you have to specify at the class level.
 *
 * @author tomyeh
 * @since 6.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ComponentAnnotation {
	String[] value();
}
