/* IMenuitem.java

	Purpose:

	Description:

	History:
		Fri Oct 15 19:04:37 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.action.data.CheckData;
import org.zkoss.stateless.action.data.FileData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zul.Menuitem;

/**
 * Immutable {@link Menuitem} component
 *
 * <p>
 * A single choice in a {@link IMenupopup} element.
 * It acts much like a button but it is rendered on a menu.
 * </p>
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
 *          <td>onCheck</td>
 *          <td><strong>ActionData</strong>: {@link CheckData}
 *          <br>Denotes user has checked the item.</td>
 *       </tr>
 *       <tr>
 *          <td>onUpload</td>
 *          <td><strong>ActionData</strong>: {@link FileData}
 *          <br>Denotes user has uploaded a file to the component.</td>
 *       </tr>
 *    </tbody>
 * </table>
 * @author katherine
 * @see Menuitem
 */
@StatelessStyle
public interface IMenuitem
		extends ILabelImageElement<IMenuitem>, IDisable<IMenuitem>,
		IChildrenOfMenupopup<IMenuitem> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IMenuitem DEFAULT = new IMenuitem.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Menuitem> getZKType() {
		return Menuitem.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.menu.Menuitem"}</p>
	 */
	default String getWidgetClass() {
		return "zul.menu.Menuitem";
	}

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this menuitem.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getAutodisable();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autodisable}.
	 *
	 * <p> Sets a list of component IDs that shall be disabled when the user
	 * clicks this menuitem.
	 *  <p>To represent the button itself, the developer can specify <code>self</code>.
	 * For example, <code>.withAutodisable("self,cancel");</code>
	 * is the same as <code>IMenuitem.ofId("ok").withAutodisable("ok,cancel");</code>
	 * that will disable
	 * both the ok and cancel menuitems when a user clicks it.
	 *
	 * <p>The menuitem being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a menuitem is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <code>IMenuitem.ofId("ok").withAutodisable("+self,+cancel");</code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  uiAgent.smartUpdate(Locator.ofId("ok"), new IMenuitem.Updater().disabled(false));
	 *  uiAgent.smartUpdate(Locator.ofId("cancel"), new IMenuitem.Updater().disabled(false));
	 *</code></pre>
	 *
	 * @param autodisable Whether it is auto-disable.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenuitem withAutodisable(@Nullable String autodisable);

	/** Returns the href that the browser shall jump to, if a user clicks
	 * this button.
	 * <p>Default: {@code null}. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	@Nullable
	String getHref();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code href}.
	 *
	 * <p> Sets he href that the browser shall jump to, if a user clicks
	 * this menuitem.
	 *
	 * @param href The href that the browser shall jump to, if a user clicks
	 * this menuitem.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenuitem withHref(@Nullable String href);

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #withHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getTarget();

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
	IMenuitem withTarget(@Nullable String target);

	/**
	 * Returns non-null if this component is used for file upload, or null otherwise.
	 * Refer to {@link #withUpload} for more details.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getUpload();

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
	IMenuitem withUpload(@Nullable String upload);

	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: {@code false}.
	 */
	default boolean isCheckmark() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code checkmark}.
	 *
	 * <p> Sets whether the check mark shall be displayed in front
	 * of each item.
	 * <p><b>Note:</b> the checkbox can be checked only if {@link #isAutocheck()} is true
	 *
	 * @param checkmark Whether the check mark shall be displayed in front
	 * of each item
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenuitem withCheckmark(boolean checkmark);

	/** Returns whether it is checked.
	 * <p>Default: {@code false}.
	 */
	default boolean isChecked() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code checked}.
	 *
	 * <p> Sets whether it is checked.
	 * <p><b>Note:</b> the checkbox can be checked only if {@link #isAutocheck()} is true
	 *
	 * @param checked Whether it is checked.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenuitem withChecked(boolean checked);

	/** Returns whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p>Default: {@code false}.
	 */
	default boolean isAutocheck() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code autocheck}.
	 *
	 * <p> Sets whether the menuitem check mark will update each time
	 * the menu item is selected.
	 *
	 * @param autocheck Whether the menuitem check mark will update each time
	 * the menu item is selected.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IMenuitem withAutocheck(boolean autocheck);

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the component
	 */
	static IMenuitem of(String label) {
		return new IMenuitem.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the menuitem holds.
	 * @param image The image that the menuitem holds.
	 */
	static IMenuitem of(String label, String image) {
		return new IMenuitem.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the menuitem holds.
	 */
	static IMenuitem ofImage(String image) {
		return new IMenuitem.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IMenuitem ofId(String id) {
		return new IMenuitem.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILabelImageElement.super.renderProperties(renderer);

		render(renderer, "checkmark", isCheckmark());
		render(renderer, "disabled", isDisabled());
		render(renderer, "checked", isChecked());
		render(renderer, "autocheck", isAutocheck());
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href", href = Executions.getCurrent().encodeURL(getHref())); //Bug #2871082
		String upload = getUpload();
		if (!Strings.isEmpty(upload) && !upload.contains("maxsize="))
			render(renderer, "upload", upload.concat(",maxsize=5120"));
		render(renderer, "target", getTarget());

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}

	/**
	 * Builds instances of type {@link IMenuitem IMenuitem}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIMenuitem.Builder {}

	/**
	 * Builds an updater of type {@link IMenuitem} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IMenuitemUpdater {}
}