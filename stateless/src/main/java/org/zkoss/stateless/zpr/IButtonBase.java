/* IButtonBase.java

	Purpose:

	Description:

	History:
		Tue Oct 19 09:48:34 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.action.data.FileData;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zul.Button;

/**
 * Immutable {@link Button} base component
 *
 * @author katherine
 */
public interface IButtonBase<I extends IButtonBase>
		extends ILabelImageElement<I> {
	@Value.Check
	default void checkOrient() {
		IComponentChecker.checkOrient(getOrient());
	}

	/**
	 * Returns the direction.
	 * <p>Default: {@code "normal"}.
	 */
	default String getDir() {
		return IButtonCtrl.NORMAL;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code dir}.
	 *
	 * <p> Sets the direction to layout image.
	 *
	 * @param dir The direction to layout image, either {@code "normal"} or {@code "reverse"}
	 *            <p>Default: {@code "normal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withDir(String dir);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code dir}.
	 *
	 * <p> Sets the direction to layout image.
	 *
	 * @param dir The direction to layout image, either {@code "normal"} or {@code "reverse"}
	 *            <p>Default: {@code "normal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withDir(Direction dir) {
		return withDir(dir.value);
	}

	/** Returns the orient.
	 * <p>Default: {@code "horizontal"}.
	 */
	default String getOrient() {
		return IButtonCtrl.HORIZONTAL;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient to layout image.
	 *
	 * @param orient the orient to layout image., either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient to layout image.
	 *
	 * @param orient the orient to layout image., either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withOrient(Orient orient) {
		return withOrient(orient.value);
	}

	/** Returns the button type.
	 * <p>Default: {@code "button"}.
	 */
	default String getType() {
		return IButtonCtrl.BUTTON;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the button type.
	 * It is meaningful only if it is used with a HTML form.
	 * Refer to <a href="http://www.htmlcodetutorial.com/forms/_BUTTON_TYPE.html">HTML Button Type</a>
	 * for details.
	 *
	 * @param type Either {@code "button"}, {@code "submit"} or {@code "reset"}.
	 * <p>Default: {@code "button"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withType(String type);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the button type.
	 * It is meaningful only if it is used with a HTML form.
	 * Refer to <a href="http://www.htmlcodetutorial.com/forms/_BUTTON_TYPE.html">HTML Button Type</a>
	 * for details.
	 *
	 * @param type Either {@code "button"}, {@code "submit"} or {@code "reset"}.
	 * <p>Default: {@code "button"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withType(Type type) {
		return withType(type.value);
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
	 * @param disabled Whether it is disabled.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withDisabled(boolean disabled);

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this button.
	 */
	@Nullable
	default String getAutodisable() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autodisable}.
	 *
	 * <p> Sets a list of component IDs that shall be disabled when the user
	 * clicks this button.
	 *  <p>To represent the button itself, the developer can specify <code>self</code>.
	 * For example, <code>.withAutodisable("self,cancel");</code>
	 * is the same as <code>IButton.ofId("ok").withAutodisable("ok,cancel");</code>
	 * that will disable
	 * both the ok and cancel buttons when a user clicks it.
	 *
	 * <p>The button being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a button is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <code>IButton.ofId("ok").withAutodisable("+self,+cancel");</code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  uiAgent.smartUpdate(Locator.ofId("ok"), new IButton.Updater().disabled(false));
	 *  uiAgent.smartUpdate(Locator.ofId("cancel"), new IButton.Updater().disabled(false));
	 *</code></pre>
	 *
	 * @param autodisable Whether it is auto-disable.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withAutodisable(@Nullable String autodisable);

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #withHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	default String getTarget() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code target}.
	 *
	 * <p> Sets the target frame or window
	 *
	 * @param target The name of the frame or window to hyperlink.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withTarget(@Nullable String target);

	/**
	 * Returns non-null if this component is used for file upload, or null otherwise.
	 * Refer to {@link #withUpload} for more details.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	default String getUpload() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code upload}.
	 *
	 * <p> Sets the JavaScript class at the client to handle the upload if this
	 * component is used for file upload.
	 *
	 * <p>For example, the following example declares a button for file upload:
	 * <pre><code>IButton.of("Upload").withUpload("true").withAction(onUpload(this::doHandle));</code></pre>
	 *
	 * <p>As shown above, after the file is uploaded, an instance of
	 * {@link FileData} is sent this handler.
	 *
	 * <p>If you want to customize the handling of the file upload at
	 * the client, you can specify a JavaScript class when calling
	 * this method:
	 * <code>withUpload("foo.Upload");</code>
	 *
	 * <p> Another options for the upload can be specified as follows:
	 *  <pre><code>.withUpload("true,maxsize=-1,multiple=true,accept=audio/*|video/*|image/*,native");</code></pre>
	 *  <ul>
	 *  <li>maxsize: the maximal allowed upload size of the component, in kilobytes, or
	 * a negative value if no limit, if the maxsize is not specified, it will use {@link Configuration#getMaxUploadSize()}</li>
	 *  <li>native: treating the uploaded file(s) as binary, i.e., not to convert it to
	 * image, audio or text files.</li>
	 *  <li>multiple: treating the file chooser allows multiple files to upload,
	 *  the setting only works with HTML5 supported browsers.</li>
	 *  <li>accept: specifies the types of files that the server accepts,
	 *  the setting only works with HTML5 supported browsers.</li>
	 *  <li>suppressedErrors: specifies the suppressed uploading errors, separated by <code>|</code> (e.g. missing-required-component|illegal-upload) (since ZK 9.5.1).
	 *  </ul>
	 *
	 * <p> Note: if the options of the <code>false</code> or the customized handler
	 * (like <code>foo.Upload</code>) are not specified, the option of <code>true</code>
	 * is implicit by default.
	 *
	 * @param upload a JavaScript class to handle the file upload
	 * at the client, or "true" if the default class is used,
	 * or null or "false" to disable the file download (and then
	 * this button behaves like a normal button).
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withUpload(@Nullable String upload);

	/** Returns the href that the browser shall jump to, if a user clicks
	 * this button.
	 * <p>Default: {@code null}. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	@Nullable
	default String getHref() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code href}.
	 *
	 * <p> Sets he href that the browser shall jump to, if a user clicks
	 * this button.
	 *
	 * @param href The href that the browser shall jump to, if a user clicks
	 * this button.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withHref(@Nullable String href);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkDir() {
		String dir = getDir();
		if (!"normal".equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkType() {
		String type = getType();
		if (type != null && !"button".equals(type) && !"submit".equals(type)
				&& !"reset".equals(type))
			throw new WrongValueException(type);
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		ILabelImageElement.super.renderProperties(renderer);

		String s;
		if (!IButtonCtrl.NORMAL.equals(s = getDir()))
			render(renderer, "dir", s);
		if (!IButtonCtrl.HORIZONTAL.equals(s = getOrient()))
			render(renderer, "orient", s);
		if (!IButtonCtrl.BUTTON.equals(s = getType()))
			render(renderer, "type", s);

		render(renderer, "disabled", isDisabled());
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href",
				href = Executions.getCurrent().encodeURL(getHref()));
		render(renderer, "target", getTarget());
		String upload = getUpload();
		if (!Strings.isEmpty(upload) && !upload.contains("maxsize="))
			render(renderer, "upload", upload.concat(",maxsize=5120"));

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}

	/**
	 * Specifies the direction to layout image with {@link #withDir(Direction)}
	 */
	enum Direction {
		/**
		 * The normal direction.
		 */
		NORMAL("normal"),

		/**
		 * The reverse direction.
		 */
		REVERSE("reverse");
		final String value;

		Direction(String value) {
			this.value = value;
		}
	}

	/**
	 * Specifies the orient with {@link #withOrient(Orient)}
	 */
	enum Orient {
		/**
		 * The horizontal orient.
		 */
		HORIZONTAL("horizontal"),

		/**
		 * The vertical orient.
		 */
		VERTICAL("vertical");
		final String value;

		Orient(String value) {
			this.value = value;
		}
	}

	/**
	 * Specifies the type with {@link #withType(Type)}
	 */
	enum Type {
		/**
		 * The normal button type.
		 */
		BUTTON("button"),

		/**
		 * The submit button type.
		 */
		SUBMIT("submit"),

		/**
		 * The reset button type.
		 */
		RESET("reset");
		final String value;

		Type(String value) {
			this.value = value;
		}
	}
}