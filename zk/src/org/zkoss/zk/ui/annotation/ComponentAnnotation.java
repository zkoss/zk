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
 * Annotation for specifying that a Java annotation class is used to
 * provide ZK component's annotations ({@link org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotations}).
 * In other words, it is used for specifying a ZUML annotation in a component's Java class.
 * <p>For example, you could implement a Java annotation as follows:
 * <pre><code>
@ComponentAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZKBIND {
  String ACCESS();
  String SAVE_EVENT();
  //...
}</code></pre>
 *
 * <p>Then, you can use it in a component's Java class.
 * <pre><code>
public class Foo extends AbstractComponent {
  @ZKBIND(ACCESS="both", SAVE_EVENT="onChange")
  public String getValue() {
  ....
  }
}</code></pre>
 *
 * <p>Notice that, if the component's Java class doesn't have the getter for
 * the given property, you can specify an attribute called <code>property</code>.
 * For example, the following snippet is equivalent to the above.
 * <pre><code>
@ZKBIND(property="value", ACCESS="both", SAVE_EVENT="onChange")
public class Foo extends AbstractComponent {
  public String getValue() {
  ....
  }
}</code></pre>
 *
 * <p>Of course, it means your annotation class has to implement
 * <code>getProperty()</code> (and it is reserved). And, <code>@Target</code>
 * shall allow <code>ElementType.METHOD</code> too.
 *
 * @author tomyeh
 * @since 6.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentAnnotation {
}
