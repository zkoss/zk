/* ActionVariable.java

	Purpose:
		
	Description:
		
	History:
		6:36 PM 2021/10/4, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.action.ActionTarget;
import org.zkoss.stateless.action.ActionType;

/**
 * Annotation which indicates that a method parameter of an {@link Action}
 * or a field member of a POJO class should be bound to a value of {@link IComponent}
 * attribute at client if possible.
 *
 * <h2>Purpose:</h2>
 * <ol>
 *     <li>To specify on a field of a POJO class to retrieve the value from the {@link #field()} of the {@link IComponent}
 *     with the {@link #targetId()} at client.</li>
 *     <li>To specify on a method parameter to retrieve the value from the {@link #field()} of the {@link IComponent} with the {@link #targetId()} at client.</li>
 * </ol>
 *
 * <h2>Example:</h2>
 * <h3>Default Behavior</h3>
 * <p>With the annotation without any arguments.</p>
 * For example,
 * <br><br>
 * <pre>
 * <code>{@literal @}{@code Action}(type=Events.ON_CLICK)
 * public void doClick(<b>{@literal @}ActionVariable</b> String email) {
 *
 * }
 * </code>
 * </pre>
 * <p>
 * As shown above, it will be treated as the same as
 * {@literal @}{@code ActionVariable(targetId="email", field="value")},
 * it means to retrieve the value from the {@code email} of a {@link IComponent} at client.
 * </p>
 * <h3>AutoMapping with Primitive Type, String, and Date Time Objects in JDK 8</h3>
 * <p> In a usage of {@literal @}{@code Action} or {@code IComponent.withAction()}
 * to bind an action handler, the {@literal @}{@code ActionVariable} annotation
 * can be omitted with Primitive Type, String, {@link ActionType}, and Date Time Objects in JDK 8.
 * The following example is the same as the declaration with {@literal @}{@code ActionVariable String email}.
 * </p>
 * <pre>
 * <code>{@literal @}{@code Action}(type=Events.ON_CLICK)
 * public void doClick(String email) {
 *
 * }
 * </code>
 * </pre>
 * <p>
 * <b>Note:</b> need to enable {@code -parameters} compiler flag to allow the Parameter reflection API
 * since Java 8.
 * </p>
 * <h3>POJO Mapping</h3>
 * <p>The annotation can be specified on POJO fields to indicate that the value
 * of that {@code field} from the {@code id} of {@link IComponent} can be bound
 * to the field.
 * <br><br>
 * For example,
 * <br><br></p>
 * <pre>
 * <code>{@literal @}{@code Action}(type=Events.ON_CLICK)
 * public void doClick(MyAccount account) {
 *
 * }
 *
 * public static class MyAccount {
 *     {@literal @}{@code ActionVariable}
 *     private String email;
 *
 *     // getter and setter.
 * }
 * </code>
 * </pre>
 * <p>
 * Or to specify the annotation on a method parameter to indicate all fields of
 * POJO should be bound to the {@code id} of {@link IComponent} with all its
 * {@code fields}.
 * <br><br>
 * For example,
 * <br><br></p>
 * <pre>
 * <code>{@literal @}{@code Action}(type=Events.ON_CLICK)
 * public void doClick({@literal @}{@code ActionVariable}(targetId="listbox") MyScrollData listboxScrollData) {
 *
 * }
 *
 * public static class MyScrollData {
 *     private int scrollTop;
 *     private int scrollLeft;
 *     private int scrollHeight;
 *     private int scrollWidth;
 *
 *   // getter and setter.
 * }
 * </code></pre>
 * <p>
 * As you can see above, the {@code MyScrollData} POJO with four fields, {@code scrollTop},
 * {@code scrollLeft}, {@code scrollHeight}, and {@code scrollWidth},
 * will be bound to the values of the {@code fields} of the {@code listbox}
 * immutable component from client.</p>
 *
 * <p>
 * <b>Note:</b> if some fields are aliases, these fields need to be specified
 * with {@literal @}{@code ActionVariable(field="alias")}
 * and their {@code id} will inherit from the declaration of the method parameter,
 * and all the other fields without the declaration annotation will be ignored.
 * <br><br>
 * For example,
 * <br><br></p>
 * <pre>
 * <code>{@literal @}{@code Action}(type=Events.ON_CLICK)
 * public void doClick({@literal @}{@code ActionVariable}(targetId="listbox") MyScrollData listboxScrollData) {
 *
 * }
 *
 * public static class MyScrollData {
 *
 *     {@literal @}{@code ActionVariable}(field="scrollTop") // the id is inherited from "listbox", not the "top" itself.
 *     private int top;
 *
 *     private int scrollLeft; // will be ignored
 *     private int scrollHeight; // will be ignored
 *     private int scrollWidth; // will be ignored
 *
 *     // getter and setter.
 * }
 * </code>
 * </pre>
 * <p>
 * <b>Note:</b> need to enable {@code -parameters} compiler flag to allow the Parameter reflection API
 * since Java 8.
 * </p>
 * @author jumperchen
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionVariable {
	/**
	 * The target id of the {@link IComponent} or
	 * an empty string to indicate the name of the method parameter or the name of
	 * field member is the same as the id.
	 * <h5>Reserved IDs</h5>
	 * <p>{@code ID} equals with {@link ActionTarget#SELF SELF} meaning the action target itself.</p>
	 * <br>
	 * For example,
	 * <br>
	 * <pre>
	 * <code>public class InputData {
	 *     private String value;
	 *
	 *     {@literal @}{@code ActionVariable}(targetId = <b>ActionTarget.SELF</b>, field = "value")
	 *     private Object oldValue;
	 *     // omitted
	 * }
	 * </code>
	 * </pre>
	 * <p>As you can see above, the {@code oldValue} is bound to the value of
	 * the {@code "value"} field from the action target that triggers an
	 * {@code onChange} action or other similar input actions.</p>
	 */
	String targetId() default "";

	/**
	 * The field name of the {@link IComponent}.
	 * By default, it's {@code "value"} meaning {@code IComponent#getValue()} if any.
	 * <br>
	 * For example:
	 * <br>
	 * <pre>
	 * <code>{@literal @}{@code ActionVariable}(targetId="SelectboxID", field="selectedIndex")
	 * private int index;
	 * </code></pre>
	 * <h2>Expressions</h2>
	 * <p>The string value can be the following.
	 * <br><br>
	 * <b>Note:</b> The expression target can be either the action target or
	 * the DOM element of the action target. (The getter priority of DOM element is lower than the client widget).
	 * <table border="1" cellspacing="0">
	 *     <tbody>
	 *         <tr>
	 *             <th>Expression</th>
	 *             <th>Result</th>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code "value"}</td>
	 *             <td>The value of {@code getValue()} of the expression target at client</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code "a.b.c"} (Expert only)</td>
	 *             <td>The value of {@code getC()} of the result of {@code getB()} of the
	 *             result of {@code getA()} of the expression target at client
	 *             <br>
	 *             <b>Note:</b> if any of the {@code fields} is {@code null}, then {@code null} is returned.
	 *             </td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@code "a|b"} (Expert only)</td>
	 *             <td>The value of {@code getA()} if any, otherwise, the value of {@code getB()} is assumed
	 *             <br>
	 *             <b>Note:</b> the parentheses {@code "()"} can be used to wrap the expression, for example,
	 *             {@code "(a|b)"}</td>
	 *         </tr>
	 *     </tbody>
	 * </table>
	 * </p>
	 */
	String field() default "value";
}