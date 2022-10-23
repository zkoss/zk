/* IInputElement.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:15:58 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.zkoss.zel.impl.lang.ELSupport.coerceToString;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.ClientConstraint;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.impl.Utils;

/**
 * Immutable {@link org.zkoss.zul.impl.InputElement} interface.
 * <p>A skeletal implementation of an input box.</p>
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onChange</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.InputData}
 *          <br> Denotes the content of an input component has been modified by the user.</td>
 *       </tr>
 *       <tr>
 *          <td>onChanging</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.InputData}
 *          <br> Denotes that user is changing the content of an input component.</td>
 *       </tr>
 *       <tr>
 *          <td>onSelection</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.SelectionData}
 *          <br> Denotes that user is selecting a portion of the text of an input component.
 *          You can retrieve the start and end position of the selected text by
 *          use of the {@code getStart} and {@code getEnd} methods.</td>
 *       </tr>
 *       <tr>
 *          <td>onFocus</td>
 *          <td>Denotes when a component gets the focus. Remember event listeners
 *          execute at the server, so the focus at the client might be changed
 *          when the event listener for {@code onFocus} got executed.</td>
 *       </tr>
 *       <tr>
 *          <td>onBlur</td>
 *          <td>Denotes when a component loses the focus. Remember event listeners
 *          execute at the server, so the focus at the client might be changed
 *          when the event listener for {@code onBlur} got executed.</td>
 *       </tr>
 *       <tr>
 *          <td>onError</td>
 *          <td><strong>ActionData</strong>: {@link org.zkoss.zephyr.action.data.ErrorData}
 *          <br>Denotes when a component caused a validation error.</td>
 *       </tr>
 *    </tbody>
 * </table>
 * <br>
 * <br>
 * <h2>Example</h2>
 * <img src="doc-files/IInputElement_example.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("example")
 * public IComponent example() {
 *     return IGrid.DEFAULT.withRows(
 *         IRows.of(
 *             IRow.of(
 *                 ILabel.of("UserName: "),
 *                 ITextbox.of("Jerry").withWidth("150px")
 *             ),
 *             IRow.of(
 *                 ILabel.of("Password: "),
 *                 ITextbox.of("foo").withType("password").withWidth("150px")
 *             ),
 *             IRow.of(
 *                 ILabel.of("Phone: "),
 *                 IIntbox.of(12345678).withConstraint("no negative, no zero").withWidth("150px")
 *             ),
 *             IRow.of(
 *                 ILabel.of("Weight: "),
 *                 IDecimalbox.of("154.32").withFormat("###.##").withWidth("150px")
 *             ),
 *             IRow.of(
 *                 ILabel.of("Birthday: "),
 *                 IDatebox.ofId("db").withWidth("150px")
 *             ),
 *             IRow.of(
 *                 ILabel.of("E-mail: "),
 *                 ITextbox.of("zk@zkoss.org")
 *                     .withConstraint("/.+@.+\\.[a-z]+/: Please enter an e-mail address").withWidth("150px")
 *             )
 *         )
 *     );
 * }
 * </code>
 * </pre>
 *
 * <h2>Built-in Constraints</h2>
 * <p> To use the default constraint, you could specify a list of conditions in
 * {@link #withConstraint(String)}, such as {@code no positive} and {@code no empty}.
 * <br><br>
 * For example,
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/builtinConstraints")
 * public IComponent builtinConstraints() {
 *     return IDiv.of(
 *         ITextbox.ofConstraint("no empty"),
 *         IIntbox.ofConstraint("no negative, no zero")
 *     );
 * }
 * </code>
 * </pre>
 *
 * <table border="1" cellspacing="0">
 * <tr>
 * <th>Condition</th>
 * <th>Description
 * </th>
 * </tr>
 * </tbody>
 * <caption>You can specify following values with {@link #withConstraint(String)}
 * to apply them</caption>
 * <tbody>
 * <tr>
 *     <td>no empty</td>
 *     <td>Empty is not allowed.</td>
 * </tr>
 * <tr>
 *     <td>no future</td>
 *     <td>Date in the future is not allowed.</td>
 * </tr>
 * <tr>
 *     <td>no negative</td>
 *     <td>Negative numbers are not allowed.</td>
 * </tr>
 * <tr>
 *     <td>no past</td>
 *     <td>Date in the past is not allowed.</td>
 * </tr>
 * <tr>
 *     <td>no positive</td>
 *     <td>Postive numbers are not allowed.</td>
 * </tr>
 * <tr>
 *     <td>no today</td>
 *     <td>Today is not allowed.</td>
 * </tr>
 * <tr>
 *     <td>no zero</td>
 *     <td>Zero numbers are not allowed.</td>
 * </tr>
 * <tr>
 *     <td>between <i>yyyyMMdd</i> and <i>yyyyMMdd</i></td>
 *     <td>Date only allowed between the specified range.
 *     The format must be {@code yyyyMMdd}, such as
 *     <pre><code>
 * IDatebox.ofConstraint("between 20211225 and 20211203");
 *     </code></pre>
 * </td>
 * </tr>
 * <tr>
 *     <td>after <i>yyyyMMdd</i></td>
 *     <td>Date only allowed after (and including) the specified date.
 *     The format must be {@code yyyyMMdd}, such as
 *     <pre><code>
 * IDatebox.ofConstraint("after 20211225");
 *     </code></pre></td>
 * </tr>
 * <tr>
 *     <td>before <i>yyyyMMdd</i></td>
 *     <td>Date only allowed before (and including) the specified date.
 *     The format must be {@code yyyyMMdd}, such as
 *     <pre><code>
 * IDatebox.ofConstraint("before 20211225");
 *     </code></pre></td>
 * </tr>
 * <tr>
 *     <td>
 * <pre>
 * end_before
 * end_after
 * after_start
 * after_end
 * ...
 * </pre></td>
 *     <td>Specifies the position of the error box. Please refer  to {@link IPopup}
 *     for all allowed position.
 *     <pre><code>
 * ITextbox.ofConstraint("no empty, end_after");
 * ITextbox.ofConstraint("no empty, start_before");
 *     </code></pre></td>
 * </tr></tbody></table>
 *
 * <h2>Regular Expression</h2>
 * <p>To specify a regular expression, you could have to use {@code /} to enclose
 * the regular expression as follows.
 * <pre>
 * <code>
 * ITextbox.ofConstraint("/.+@.+\\.[a-z]+/");
 * </code>
 * </pre>
 * <h4>Flags</h4>
 * <p>To specify the flags to the regular expression, you could add the flags
 * after the ending slash of the regular expression.
 * <br><br>
 * For example, If you want to enable case-insensitive matching, you could add the flag as bellow.

 * <pre>
 * <code>
 * ITextbox.ofConstraint("/[A-Z]{3}/i");
 * </code>
 * </pre>
 * <p>The flags supported:</p>
 *<table border="1" cellspacing="0">
 * <tbody><tr>
 * <th>flags</th>
 * <th>Description
 * </th></tr>
 * <tr>
 * <td>i
 * </td>
 * <td>ignore case
 * </td></tr>
 * <tr>
 * <td>m
 * </td>
 * <td>multiline
 * </td></tr>
 * <tr>
 * <td>s
 * </td>
 * <td>dotAll
 * </td></tr>
 * <tr>
 * <td>u
 * </td>
 * <td>unicode
 * </td></tr></tbody></table>
 * <p><b>Note:</b> the regular expression will always using global match no matter
 * the {@code g} flag is added or not.</p>
 *
 * <h2>Multiple Constraints</h2>
 * <p>Notice that it is allowed to mix regular expression with other constraints
 * by separating them with a comma.
 * <p>If you prefer to display an application dependent message instead of default
 * one, you could append the constraint with colon and the message you want to
 * display when failed.</p>
 * <pre>
 * <code>
 * ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: e-mail address only");
 * IDatebox.ofConstraint("no empty, no future: now or never");
 * </code>
 * </pre>
 * of course, it supports multiple custom messages
 * <pre>
 * <code>
 * IIntbox.ofConstraint("no negative: forbid negative, no positive: forbid positive");
 * </code>
 * </pre>
 *
 * <h2>i18n Error Message</h2>
 *
 * <pre>
 * <code>
 * ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: " + CommonFns.getLabel("err.email.required"))
 * </code>
 * </pre>
 *
 * <h2>Escape a Comma</h2>
 * <p>If you want to write a longer sentence with comma separator, you can enclose
 * your customized sentence with curly braces</p>
 *
 * <pre>
 * <code>
 * ITextbox.ofConstraint("no empty: {Sorry, no empty allowed}, /.+@.+\\.[a-z]+/: email only")
 * </code>
 * </pre>
 *
 * <h2>Custom Constraint</h2>
 * <p>If you want a custom constraint, you could register an {@code onChange} action
 * to validate the input value
 * <br>
 * <br>
 * For example,</p>
 *
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/customConstraint")
 * public IComponent customConstraint() {
 *     return ITextbox.DEFAULT.withAction(this::doCustomConstraint);
 * }
 *
 * {@literal @}{@code Action}(type = Events.ON_CHANGE)
 * public void doCustomConstraint(UiAgent uiAgent, Self self, InputData inputData) {
 *     String value = inputData.getValue();
 *     if (value != null {@literal &&} new Integer(value).intValue() % 2 == 1) {
 *         uiAgent.smartUpdate(self, new ITextbox.Updater().errorMessage("Only even numbers are allowed, not " + value));
 *     }
 * }
 * </code>
 * </pre>
 * <p>If the validation fails, just use {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}
 * to update the error message of the ITextbox by its {@link ITextbox.Updater updater}.</p>
 *
 * @author katherine
 */
public interface IInputElement<I extends IInputElement, ValueType> extends IXulElement<I>,
		IReadonly<I>, IChildrenOfInputgroup<I> {

	/** Returns the name of this component.
	 * <p>Default: {@code null}.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	@Nullable
	String getName();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code name}.
	 *
	 * <p> Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name The name of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withName(@Nullable String name);

	/**
	 * Returns a short hint that describes the expected value of an input component
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getPlaceholder();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code placeholder}.
	 *
	 * <p> Sets the short hint that is displayed in the input component
	 * before the user enters a value.
	 *
	 * @param placeholder The short hint that describes the expected value of
	 * an input component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withPlaceholder(@Nullable String placeholder);

	/**
	 * Returns the additional attributes which is set by {@link #withInputAttributes(Map)}
	 * <p>Default: {@code null}.</p>
	 */
	@Nullable
	Map<String, String> getInputAttributes();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code inputAttributes}.
	 *
	 * <p> Sets some additional attributes to the input HTML tag of the component
	 * at client.
	 *
	 * @param inputAttributes The inputAttributes can take a Map with attribute
	 * names as the keys or a String separate by {@code ";"} and follow the {@code name=value} rule.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withInputAttributes(@Nullable Map<String, ? extends String> inputAttributes);

	/** Returns the value of the input component.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	ValueType getValue();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code value}.
	 *
	 * <p> Sets the value of the input component.
	 *
	 * @param value The value of the input component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withValue(@Nullable ValueType value);

	/**
	 * Returns the client constraint to validate the value entered by a user. if any.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getConstraint();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code constraint}.
	 *
	 * <p> Sets the client constraint to validate the value entered by a user; you
	 * can specify the following values or Regular Expression.
	 <table border="1" cellspacing="0">
	 * <tr>
	 * <th>Condition</th>
	 * <th>Description
	 * </th>
	 * </tr>
	 * </tbody>
	 * <tbody>
	 * <tr>
	 *     <td>no empty</td>
	 *     <td>Empty is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no future</td>
	 *     <td>Date in the future is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no negative</td>
	 *     <td>Negative numbers are not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no past</td>
	 *     <td>Date in the past is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no positive</td>
	 *     <td>Postive numbers are not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no today</td>
	 *     <td>Today is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no zero</td>
	 *     <td>Zero numbers are not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>between <i>yyyyMMdd</i> and <i>yyyyMMdd</i></td>
	 *     <td>Date only allowed between the specified range.
	 *     The format must be {@code yyyyMMdd}, such as
	 *     <pre><code>
	 * IDatebox.ofConstraint("between 20211225 and 20211203");
	 *     </code></pre>
	 * </td>
	 * </tr>
	 * <tr>
	 *     <td>after <i>yyyyMMdd</i></td>
	 *     <td>Date only allowed after (and including) the specified date.
	 *     The format must be {@code yyyyMMdd}, such as
	 *     <pre><code>
	 * IDatebox.ofConstraint("after 20211225");
	 *     </code></pre></td>
	 * </tr>
	 * <tr>
	 *     <td>before <i>yyyyMMdd</i></td>
	 *     <td>Date only allowed before (and including) the specified date.
	 *     The format must be {@code yyyyMMdd}, such as
	 *     <pre><code>
	 * IDatebox.ofConstraint("before 20211225");
	 *     </code></pre></td>
	 * </tr>
	 * <tr>
	 *     <td>
	 * <pre>
	 * end_before
	 * end_after
	 * after_start
	 * after_end
	 * ...
	 * </pre></td>
	 *     <td>Specifies the position of the error box. Please refer  to {@link IPopup}
	 *     for all allowed position.
	 *     <pre><code>
	 * ITextbox.ofConstraint("no empty, end_after");
	 * ITextbox.ofConstraint("no empty, start_before");
	 *     </code></pre></td>
	 * </tr></tbody></table>
	 *
	 * @param constraint The client constraint of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withConstraint(@Nullable String constraint);

	/**
	 * Returns the class name of the custom style applied to the errorbox of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getErrorboxSclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code errorboxSclass}.
	 *
	 * <p> Sets the class name of the custom style to be applied to the errorbox
	 * of this component.
	 *
	 * @param errorboxSclass The class name of the custom style.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withErrorboxSclass(@Nullable String errorboxSclass);

	/**
	 * Returns the class name of the custom style applied to the errorbox icon of this component.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getErrorboxIconSclass();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code errorboxIconSclass}.
	 *
	 * <p> Sets the class name of the custom style to be applied to the icon of the errorbox
	 * of this component.
	 *
	 * @param errorboxIconSclass The class name of the custom style for the icon
	 * of the errorbox.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withErrorboxIconSclass(@Nullable String errorboxIconSclass);

	/** Marshall value to be sent to the client if needed. (Internal use)
	 *
	 * <p>Overrides it if the value to be sent to the client is not JSON Compatible.
	 * @param value the value to be sent to the client
	 * @return the marshalled value
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Object marshall(Object value) {
		return value;
	}

	/** Returns whether it is disabled.
	 * <p>Default: {@code false}.
	 */
	default boolean isDisabled() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code disabled}.
	 *
	 * <p> Sets whether it is disabled.
	 *
	 * @param disabled {@code true} to disable this input component.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withDisabled(boolean disabled);

	/**
	 * Returns whether enable the inplace-editing.
	 * <p>default: {@code false}.
	 */
	default boolean isInplace() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code inplace}.
	 *
	 * <p>Sets to enable the inplace-editing function that the look and feel is
	 * like a label.
	 *
	 * @param inplace {@code true} to enable the inplace-editing fuction.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withInplace(boolean inplace);

	/** Returns the maxlength.
	 * <p>Default: {@code 0} (non-positive means unlimited).
	 */
	default int getMaxlength() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxlength}.
	 *
	 * <p> Sets the maxlength.
	 * <p> The length includes the format, if specified.
	 *
	 * @param maxlength The max length to display.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withMaxlength(int maxlength);

	/** Returns the cols which determines the visible width, in characters.
	 * <p>Default: {@code 0} (non-positive means the same as browser's default).
	 */
	default int getCols() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code cols}.
	 *
	 * <p> Sets the cols which determines the visible width, in characters.
	 *
	 * @param cols The cols which determines the visible width
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withCols(int cols);

	/** Returns {@code true} if {@code onChange} action is sent as soon as user types
	 * in the input component.
	 * <p>Default: {@code false}
	 */
	default boolean getInstant() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code instant}.
	 *
	 * <p>Sets the instant attribute. When the attribute is true, {@code onChange}
	 * action will be fired as soon as user type in the input component.
	 *
	 * @param instant {@code true} to send the action ASAP.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withInstant(boolean instant);

	/** Returns the error message that is set by {@link #withErrorMessage(String)}
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getErrorMessage();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code errorMessage}.
	 *
	 * <p>Associates an error message to this input.
	 * It will cause the given error message to be shown at the client.
	 *
	 * @param errorMessage The error message to show at client.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withErrorMessage(@Nullable String errorMessage);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkCols() {
		int cols = getCols();
		if (cols < 0) {
			throw new UiException("Illegal cols: " + cols);
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 * @throws IOException
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		Object value = getValue();
		if (value != null)
			render(renderer, "_value", marshall(value));
		//ZK-658: we have to render the value before constraint
		render(renderer, "readonly", isReadonly());
		render(renderer, "disabled", isDisabled());
		render(renderer, "name", getName());
		render(renderer, "inplace", isInplace());
		String _placeholder = getPlaceholder();
		if (_placeholder != null)
			render(renderer, "placeholder", _placeholder);
		Map<String, String> _inputAttributes = getInputAttributes();
		if (_inputAttributes != null)
			render(renderer, "inputAttributes", _inputAttributes);


		int v;
		if ((v = getMaxlength()) > 0)
			renderer.render("maxlength", v);
		int _cols = getCols();
		if (_cols > 0)
			renderer.render("cols", _cols);
		if (getInstant())
			renderer.render("instant", true);

		// Not support server constraint for Zephyr.
		boolean constrDone = false;
		final String constraint = getConstraint();
		if (constraint != null) {
			final ClientConstraint cc = SimpleConstraint.getInstance(constraint);
			if (cc != null) {
				final String cpkg = cc.getClientPackages();
				JavaScriptValue clientPackages = cpkg != null ? new JavaScriptValue("zk.load('" + cpkg + "')") : null;
				render(renderer, "_0", clientPackages); //name doesn't matter

				final String js = cc.getClientConstraint();
				Object clientConstraintCode;
				if (js != null && js.length() > 0) {
					final char c = js.charAt(0);
					if (c != '\'' && c != '"') {
						clientConstraintCode = new JavaScriptValue("{constraint:function(){\nreturn " + js + ";}}");
					} else {
						//some JavaScript code => z$al
						clientConstraintCode = js;
					}
				} else {
					clientConstraintCode = null;
				}
				final Object code = clientConstraintCode;
				if (code != null) {
					if (code instanceof JavaScriptValue)
						renderer.render("z$al", code);
					else //must be string
						renderer.renderDirectly("constraint", code);
					constrDone = true;
				}
			}
		}
		if (!constrDone && constraint != null)
			renderer.render("constraint", "[s");

		Utils.renderCrawlableText(coerceToString(getValue()));

		//ZK-2677
		render(renderer, "errorboxSclass", getErrorboxSclass());
		render(renderer, "errorboxIconSclass", getErrorboxIconSclass());
	}
}
