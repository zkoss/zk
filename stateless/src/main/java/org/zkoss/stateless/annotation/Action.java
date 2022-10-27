/* Action.java

	Purpose:
		
	Description:
		
	History:
		5:53 PM 2021/10/4, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ITextbox;

/**
 * Annotation for mapping client widget events onto methods in
 * {@link StatelessComposer} or {@link StatelessRichlet}
 * classes with flexible method signatures.
 *
 * @author jumperchen
 */
@Target({ElementType.TYPE_USE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	/**
	 * Finding the queried target(s) of matching the {@link #from()}
	 * with the given {@link #type()} to bind to
	 * this action.
	 * <p>Default: {@code ""} an empty string, meaning no any target is bound to
	 * this action, unless specifying it into an individual action handler.
	 * <br><br>For example,<br><br>
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/click")
	 * public IComponent clickAction() {
	 *     return IButton.of("doClick").<b>withAction(this::doClick)</b>;
	 * }
	 *
	 * {@literal @}Action(type = Events.ON_CLICK)
	 * public void doClick() {}
	 * </code></pre>
	 *
	 * <p><b>Performance tip:</b> It's recommended to use an individual action
	 * handler with {@link IComponent#withAction(ActionHandler)}
	 * instead of the query selector with {@link #from()}.
	 * </p>
	 *
	 * <p><b>Note:</b> the annotation binding is only available when the page is
	 * at initial phase, all the other dynamically added components won't take effect.
	 * In other words, all components are created after the page initiated will not
	 * bind with the {@link Action} annotation, please use {@link IComponent#withAction(ActionHandler)}
	 * or {@link IComponent#withActions(ActionHandler...)} instead.</p>
	 * <h2>The selector expression</h2>
	 * <h3>ID</h3>
	 * <p>The ZK {@code ID} selector, which starts with {@code #}, similar with CSS selector.
	 * <br><br>For example,
	 * <br><br>
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/button/click")
	 * public IComponent clickAction() {
	 *     return IButton.ofId("btn");
	 * }
	 * {@literal @}Action(from = <b>"#btn"</b>, type = Events.ON_CLICK)
	 * public void doButtonClick() {}
	 * </code></pre>
	 * The above example is the same as this by specifying an individual handler.
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/button/click")
	 * public IComponent clickAction() {
	 *     return IButton.ofId("btn").<b>withAction(this::doButtonClick)</b>;
	 * }
	 * {@literal @}Action(type = Events.ON_CLICK)
	 * public void doButtonClick() {}
	 * </code></pre>
	 * </p>
	 * <h3>Component Widget Name</h3>
	 * <p>
	 * The widget name of ZK component at client, i.e. {@link IButton}
	 * is named {@code button} and {@link ITextbox} is named {@code textbox}.
	 * <br><br>For example,
	 * <br><br>
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/allButtons/click")
	 * public IComponent clickAction() {
	 *     return IDiv.of(IButton.of("button 1"), IButton.of("button 2"), ...);
	 * }
	 * {@literal @}Action(from = <b>"button"</b>, type = Events.ON_CLICK)
	 * public void doAllButtonsClick({@literal @}{@code ActionVariable}(targetId=ActionTarget.SELF, field="label") String buttonLabel) {}
	 * </code></pre>
	 * </p>
	 * <p><b>Note:</b> the action target is a type of ZK client widget, which extends from
	 * <a href="https://www.zkoss.org/javadoc/latest/jsdoc/zk/Widget.html">zk.Widget</a>,
	 * not the DOM element in this case.
	 *
	 * Be aware of that, if multiple actions are bound to a single target with the
	 * same {@link #type()}, then each action handler will be invoked in a
	 * non-deterministic order. As shown above, {@code doAllButtonsClick()}
	 * and {@code doButtonClick()} are all bound to a button {@code "#btn"}, when
	 * the button is clicked by a user, both action handlers are invoked in a unknown
	 * order. Even if the case with a specific action {@code .withAction(this::doButtonClick)},
	 * the invoked order cannot be deterministic.
	 * </p>
	 * @see ActionVariable
	 */
	String from() default "";

	/**
	 * The client widget events, i.e. {@link org.zkoss.zk.ui.event.Events#ON_CLICK}
	 * @see org.zkoss.zk.ui.event.Events
	 * @see org.zkoss.zul.event.ZulEvents
	 */
	String[] type();
}