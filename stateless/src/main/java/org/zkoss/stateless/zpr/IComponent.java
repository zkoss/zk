/* IComponent.java

	Purpose:

	Description:

	History:
		3:15 PM 2021/9/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.action.ActionType;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.stateless.ui.util.StatelessContentRenderer;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.util.ActionHandler0;
import org.zkoss.stateless.util.ActionHandler1;
import org.zkoss.stateless.util.ActionHandler2;
import org.zkoss.stateless.util.ActionHandler3;
import org.zkoss.stateless.util.ActionHandler4;
import org.zkoss.stateless.util.ActionHandler5;
import org.zkoss.stateless.util.ActionHandler6;
import org.zkoss.stateless.util.ActionHandler7;
import org.zkoss.stateless.util.ActionHandler8;
import org.zkoss.stateless.util.ActionHandler9;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.EventListenerMap;
import org.zkoss.zk.ui.sys.EventListenerMapCtrl;

/**
 * Immutable {@link org.zkoss.zk.ui.Component} interface.
 * <p>Provides a set of immutable APIs of a ZK component to create a same result
 * as ZK component to client side, and by hooking an {@link #withAction(ActionHandler) action}
 * or multiple {@link #withActions(ActionHandler...) actions} on a client widget
 * triggers a user action to server to handle it in a stateless HTTP request protocol,
 * and then the developer can manipulate the client state at server by an action handler.</p>
 *
 * For example,
 * <br>
 * <br>
 * <pre>
 * <code>{@literal @}RichletMapping("")
 * public IComponent index() {
 *     return IVlayout.of(ITextbox.ofId("msg"),
 *         IButton.of("Submit").withAction(this::doSubmit));
 * }
 *
 * {@literal @}Action(type = Events.ON_CLICK)
 * public void doSubmit(@ActionVariable(targetId="msg", field="value") String msg) {
 *     Clients.alert("Hello " + msg);
 * }
 * </code></pre>
 * As you can see, the {@code @ActionVariable()} annotation declared on the {@code doSubmit()}
 * method handler is a way to receive the client widget state at server,
 * in this case, the {@code msg} value is the value of the {@link ITextbox},
 * which the user types if any.
 *
 * <p><b>Note:</b> It's convenient to use {@code doSubmit(String msg)} directly if the Javac compiler
 * with {@code -parameters} to generate metadata for reflection on method parameters.
 * </p>
 * @author jumperchen
 * @see ActionVariable
 * @see Action
 */
public interface IComponent<R extends IComponent> {

	/**
	 * Returns the ID.
	 * <p>Default: {@code ""} (an empty string; it means no ID at all).
	 * <br>
	 * <b>Note:</b> Unlike ZK Component, there is no <a href="https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Composing/ID_Space">ID Space</a>
	 * in Stateless Component, so the {@code ID} should be unique within a <a href="https://www.zkoss.org/wiki/ZK_Developer%27s_Guide/Fundamental_ZK/Basic_Concepts/Page_and_Desktop">Desktop Scope</a>,
	 * aka a browser page.
	 * </p>
	 */
	default String getId() {
		return ""; // never be null as the same as AbstractComponent#_id
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code ID}.
	 *
	 * <p><b>Note:</b> Unlike ZK Component, there is no <a href="https://www.zkoss.org/wiki/ZK_Developer's_Reference/UI_Composing/ID_Space">ID Space</a>
	 * in Stateless Component, so the {@code ID} should be unique within a <a href="https://www.zkoss.org/wiki/ZK_Developer%27s_Guide/Fundamental_ZK/Basic_Concepts/Page_and_Desktop">Desktop Scope</a>,
	 * aka a browser page.</p>
	 * @param id The identifier. You could specify null or an empty string to remove ID.
	 * <p>Default: {@code ""} (an empty string; it means no ID at all).
	 * @return A modified copy of the {@code this} object
	 */
	R withId(String id);

	/**
	 * Returns the client widget class which belongs to this component.
	 */
	String getWidgetClass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code widgetClass}.
	 *
	 * @param widgetClass The client widget class (a.k.a., the widget type).
	 * The widget class is a JavaScript class, including the package name.
	 * <br>
	 * For example, {@code "zul.wnd.Window"}.
	 * @return A modified copy of the {@code this} object
	 */
	R withWidgetClass(String widgetClass);

	/**
	 * Returns the {@link ActionHandler} if any.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	@StatelessOnly
	ActionHandler getAction();

	/**
	 * Returns all the {@link ActionHandler}s if any.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	@StatelessOnly
	List<ActionHandler> getActions();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code actions}.
	 *
	 * <p>
	 * To register multiple action handler, the action type can be one of
	 * the {@link ActionType types} or the types in {@code org.zkoss.statelessex.action.ActionTypeEx}
	 * <br>
	 * <br>
	 * For example,
	 * <br>
	 * <br>
	 * <pre>
	 * <code>{@literal @}RichletMapping("")
	 * public IComponent index() {
	 *     return ICheckbox.ofId("myCheckbox").withActions(ActionType.onCheck(this::doMyCheck),
	 *         ActionType.onRightClick(this::doMyRightClick), ...);
	 * }
	 *
	 * public void doMyCheck() {}
	 *
	 * public void doMyRightClick() {}
	 * </code></pre>
	 * </p>
	 * <p><b>Note:</b> The {@code withActions()} won't overwrite the one returned from {@link #getAction()},
	 * which is convenient for registering a single action.
	 * </p>
	 * @param actions The multiple actions to register with different {@link ActionType ActionTypes}
	 * @return A modified copy of {@code this} object
	 * @see #withAction(ActionHandler)
	 * @see #withActions(Iterable)
	 */
	R withActions(@Nullable  ActionHandler... actions);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code actions}.
	 * @param actions The multiple actions to register with different {@link ActionType}s
	 * @return A modified copy of {@code this} object
	 * @see #withActions(ActionHandler...)
	 */
	R withActions(@Nullable Iterable<? extends ActionHandler> actions);


	/**
	 * Returns whether this component is visible at client.
	 * <p>Default: {@code true}</p>
	 */
	default boolean isVisible() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code visible}.
	 *
	 * @param visible Sets whether {@code this} component is visible at client.
	 * <p>Default: {@code true}</p>
	 * @return A modified copy of {@code this} object
	 */
	R withVisible(boolean visible);

	/**
	 * Returns the mold used to render this component.
	 * <p>Default: {@code "default"}</p>
	 */
	default String getMold() {
		return "default";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code mold}.
	 *
	 * @param mold Sets the mold to render {@code this} component.
	 * <p>Default: {@code "default"}</p>, if {@code null} or an empty string {@code ""},
	 * the {@code "default"} value is assumed.
	 * @return A modified copy of {@code this} object
	 */
	R withMold(String mold);

	/**
	 * Internal use only for copying all event listeners from ZK component within a {@code Stateless} file.
	 * @hidden for Javadoc
	 */
	@Nullable
	EventListenerMap getEventListenerMap();

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 */
	R withEventListenerMap(@Nullable EventListenerMap value);

	/**
	 * Returns the ZK Component type, which this component refers to. (Internal use only)
	 * @hidden for Javadoc
	 */
	Class<? extends Component> getZKType();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * <p>
	 * To register a single action handler, the action type can be one of
	 * the {@link ActionType types} or the types in {@code org.zkoss.statelessex.action.ActionTypeEx}
	 * <br>
	 * For example, (with a specific {@code AcitonType.onClick()})
	 * <br>
	 * <pre>
	 * <code>{@literal @}RichletMapping("")
	 * public IComponent index() {
	 *     return ICheckbox.ofId("myCheckbox").withAction(ActionType.onClick(this::doMyClick));
	 * }
	 *
	 * public void doMyClick() {}
	 * </code></pre>
	 * Or with {@code @Action} annotation on a method, for example,
	 * <br>
	 * <pre>
	 * <code>{@literal @}RichletMapping("")
	 * public IComponent index() {
	 *     return ICheckbox.ofId("myCheckbox").withAction(this::doMyClick);
	 * }
	 *
	 * {@literal @}Action(type = Events.ON_CLICK)
	 * public void doMyClick() {}
	 * </code></pre>
	 * As you can see above, both ways are the same result to register a single action handler
	 * to an immutable component, but the priority of an action type of the first one with {@code ActionType.onClick()} is
	 * higher than the second one with {@code @Action()} annotation, in other words, the action
	 * type can be overwritten by any {@link ActionType types} or the types in {@code org.zkoss.statelessex.action.ActionTypeEx}
	 * with the syntax {@code withAction(ActionType.onClick())}. It's convenient to
	 * share one method handler for multiple action types.
	 * </p>
	 * <p><b>Note:</b> The {@code withAction()} won't overwrite any one returned from {@link #getActions()},
	 * which is convenient for registering multiple actions.
	 * </p>
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler
	 */
	R withAction(@Nullable ActionHandler action);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler0
	 * @see #withAction(ActionHandler)
	 */
	default R withAction(@Nullable ActionHandler0 action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler1
	 * @see #withAction(ActionHandler)
	 */
	default <A> R withAction(@Nullable ActionHandler1<A> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler2
	 * @see #withAction(ActionHandler)
	 */
	default <A, B> R withAction(@Nullable ActionHandler2<A, B> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler3
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C> R withAction(@Nullable ActionHandler3<A, B, C> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler4
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C, D> R withAction(@Nullable ActionHandler4<A, B, C, D> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler5
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C, D, E> R withAction(@Nullable
			ActionHandler5<A, B, C, D, E> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler6
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C, D, E, F> R withAction(@Nullable
			ActionHandler6<A, B, C, D, E, F> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler7
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C, D, E, F, G> R withAction(@Nullable
			ActionHandler7<A, B, C, D, E, F, G> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler8
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C, D, E, F, G, H> R withAction(@Nullable
			ActionHandler8<A, B, C, D, E, F, G, H> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code action}.
	 *
	 * @param action A new action of {@link ActionType} to register (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 * @see ActionHandler9
	 * @see #withAction(ActionHandler)
	 */
	default <A, B, C, D, E, F, G, H, I> R withAction(@Nullable
			ActionHandler9<A, B, C, D, E, F, G, H, I> action) {
		return withAction(ActionHandler.of(action));
	}

	/**
	 * Internal use only
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkActions() {
		ActionHandler action = this.getAction();
		if (action != null) {
			try {
				Method method = action.method();
				if (IComponentCtrl.lookupActionAnnotation(action, method) == null) {
					throw new IllegalArgumentException(
							"The binding of withAction() should have @Action annotation.");
				}
			} catch (ActionHandler.UnableToGuessMethodException e) {
				throw new IllegalArgumentException(
						"withAction() cannot accept anonymous class, please convert it to Lambda syntax.");
			}
		}
		List<ActionHandler> actions = this.getActions();
		if (actions != null && !actions.isEmpty()) {
			actions.forEach(action0 -> {
				try {
					Method method = action0.method();
					if (IComponentCtrl.lookupActionAnnotation(action0, method) == null) {
						throw new IllegalArgumentException(
								"The binding of withActions() should have @Action annotation.");
					}
				} catch (ActionHandler.UnableToGuessMethodException e) {
					throw new IllegalArgumentException(
							"withActions() cannot accept anonymous class, please convert it to Lambda syntax.");
				}
			});
		}
	}

	/**
	 * Returns the client widget listeners.
	 */
	@Nullable
	Map<String, String> getWidgetListeners();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code widgetListeners}.
	 * <p> Sets or removes an event listener of the peer widget.
	 * If there is an event listener associated with the same event,
	 * the previous one will be replaced.
	 *
	 * @param widgetListeners The client widget listeners
	 * @return A modified copy of the {@code this} object
	 */
	R withWidgetListeners(@Nullable Map<String, ? extends String> widgetListeners);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code eventName} and {@code script}.
	 *
	 * <p> Sets or removes an event listener of the peer widget.
	 * If there is an event listener associated with the same event,
	 * the previous one will be replaced.
	 *
	 * @param eventName The event name, such as {@code onClick}
	 * @param script The script to handle the event. If null, the event
	 * handler is removed.
	 * @return A modified copy of the {@code this} object
	 */
	default R withWidgetListener(String eventName, String script) {
		Map<String, String> widgetListeners = getWidgetListeners();
		if (widgetListeners == null) {
			widgetListeners = new LinkedHashMap<>();
		}
		widgetListeners.put(eventName, script);
		return withWidgetListeners(widgetListeners);
	}

	/**
	 * Returns the mapping of the script and the function definition to override
	 * widget's methods and attributes, or null.
	 */
	@Nullable
	Map<String, String> getWidgetOverrides();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code widgetOverrides}.
	 *
	 * <p> Sets or removes a method or an attribute of the peer widget
	 * (at the client). If there is a method or an attribute associated with the same name,
	 * the previous one will be replaced.
	 *
	 * <p>If there is no previous method or attribute, the method/attribute will
	 * be assigned directly.
	 *
	 * @param widgetOverrides The client widget overrides for methods and attributes
	 * @return A modified copy of the {@code this} object
	 */
	R withWidgetOverrides(@Nullable Map<String, ? extends String> widgetOverrides);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code name} and {@code script}.
	 *
	 * <p>Sets or removes a method or an attribute of the peer widget (at the client).
	 * If there is a method or a attribute associated with the same name,
	 * the previous one will be replaced.
	 *
	 * @param name The attribute name to override,
	 * such as <code>setValue</code> and <code>miles</code>.
	 * @param script the method definition, such as <code>function (arg) {...}</code>,
	 * or a value, such as <code>123</code> and <code>new Date()</code>.
	 * If not null, this method will be added to the peer widget.
	 * If there was a method with the same name, it will be renamed to
	 * <code>$<i>name</i></code> so can you call it back.
	 * <pre><code>.withWidgetOverride("setValue", "function (value) {" +
	 *  "this.$setValue(value); //old method" +
	 * "}");</code></pre>
	 * If null, the previous method, if any, will be restored.
	 * @return A modified copy of the {@code this} object
	 */
	default R withWidgetOverride(String name, String script) {
		Map<String, String> widgetOverrides = getWidgetOverrides();
		if (widgetOverrides == null) {
			widgetOverrides = new LinkedHashMap<>();
		}
		widgetOverrides.put(name, script);
		return withWidgetOverrides(widgetOverrides);
	}

	/**
	 * Returns the client DOM attributes.
	 */
	@Nullable
	Map<String, String> getClientAttributes();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code clientAttributes}.
	 * <p> Sets or removes a DOM attribute of the peer widget (at the client).
	 * ZK pass the attributes directly to the DOM attribute generated
	 * at the client.
	 *
	 * @param clientAttributes The client DOM attributes
	 * @return A modified copy of the {@code this} object
	 */
	R withClientAttributes(@Nullable Map<String, ? extends String> clientAttributes);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code name} and {@code value}.
	 *
	 * <p> Sets or removes a DOM attribute of the peer widget (at the client).
	 * ZK pass the attributes directly to the DOM attribute generated
	 * at the client.
	 *
	 * @param name the attribute name to generate to the DOM element,
	 * such as <code>onload</code>.
	 * @param value the value of the attribute. It could be anything
	 * depending on the attribute.
	 * If null, the attribute will be removed. Make sure to specify an empty
	 * string if you want an attribute with an empty value.
	 * @return A modified copy of the {@code this} object
	 */
	default R withClientAttribute(String name, String value) {
		Map<String, String> clientAttributes = getClientAttributes();
		if (clientAttributes == null) {
			clientAttributes = new LinkedHashMap<>();
		}
		clientAttributes.put(name, value);
		return withClientAttributes(clientAttributes);
	}

	/**
	 * Internal use only.
	 * @hidden for Javadoc
	 * @param renderer
	 * @throws IOException
	 */
	// same as AbstractComponent#renderProperties(ContentRenderer)
	default void renderProperties(ContentRenderer renderer) throws IOException {
		this.render(renderer, "id", getId());
		if (!isVisible()) {
			renderer.render("visible", false);
		}

		EventListenerMap eventListenerMap = getEventListenerMap();
		if (eventListenerMap instanceof EventListenerMapCtrl) {
			Set<String> eventNames = ((EventListenerMapCtrl) eventListenerMap).getEventNames();
			for (String name : eventNames) {
				renderer.render("$" + name, true);
			}
		}

		renderer.renderWidgetListeners(getWidgetListeners());
		renderer.renderWidgetOverrides(getWidgetOverrides());
		renderer.renderClientAttributes(getClientAttributes());

		// stateless#41
		ActionHandler action = this.getAction();
		List<ActionHandler> actions = this.getActions();
		final int lengthX = action == null ? 0 : 1;
		final int lengthY = actions != null ? actions.size() : 0;
		if (lengthX > 0 || lengthY > 0) {
			final ArrayList<ActionHandler> allActions = new ArrayList<>(lengthX + lengthY);
			if (lengthX > 0) {
				allActions.add(action);
			}
			if (lengthY > 0) {
				allActions.addAll(actions);
			}

			IComponentCtrl.renderActions(allActions, (StatelessContentRenderer) renderer);
		}
	}

	/** A utility to be called by {@link #renderProperties} to
	 * render a string-value property. (Internal use only)
	 * It ignores if value is null or empty.
	 * If you want to render it even if null/empty, invoke
	 * {@link ContentRenderer#render(String, String)} directly.
	 * @hidden for Javadoc
	 */
	default void render(ContentRenderer renderer, String name, String value) throws IOException {
		if (value != null && value.length() > 0)
			renderer.render(name, value);
	}

	/** A utility to be called by {@link #renderProperties} to
	 * render a string-value property. (Internal use only)
	 * It ignores if value is null.
	 * If you want to render it even if null, invoke
	 * {@link ContentRenderer#render(String, Object)} directly.
	 * @hidden for Javadoc
	 */
	default void render(ContentRenderer renderer, String name, Object value) throws
			IOException {
		if (value instanceof String)
			render(renderer, name, (String) value);
		else if (value != null)
			renderer.render(name, value);
	}

	/** A utility to be called by {@link #renderProperties} to
	 * render a boolean-value property if it is true. (Internal use only)
	 * If you want to render it no matter true or false, use
	 * {@link ContentRenderer#render(String, boolean)} directly.
	 * @hidden for Javadoc
	 */
	default void render(ContentRenderer renderer, String name, boolean value) throws IOException {
		if (value)
			renderer.render(name, true);
	}
}