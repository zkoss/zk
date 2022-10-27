/* IFileupload.java

	Purpose:

	Description:

	History:
		Wed Oct 27 17:22:35 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Fileupload;

/**
 * Immutable {@link Fileupload} component
 *
 * <p>A fileupload dialog used to let user upload a file.</p>
 * <p>Fileupload is actually a button with {@code withUpload("true")}.</p>
 *
 * @author katherine
 * @see Fileupload
 */
@StatelessStyle
public interface IFileupload extends IButtonBase<IFileupload>, IAnyGroup<IFileupload> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IFileupload DEFAULT = new IFileupload.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Fileupload> getZKType() {
		return Fileupload.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Fileupload"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Fileupload";
	}

	default String getUpload() {
		return "true";
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the component
	 */
	static IFileupload of(String label) {
		return new IFileupload.Builder().setLabel(label).build();
	}


	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the button holds.
	 * @param image The image that the button holds.
	 */
	static IFileupload of(String label, String image) {
		return new IFileupload.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the button holds.
	 */
	static IFileupload ofImage(String image) {
		return new IFileupload.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given orient.
	 * @param orient The button orient
	 */
	static IFileupload ofOrient(Orient orient) {
		return new IFileupload.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given dir.
	 * @param dir The button dir
	 */
	static IFileupload ofDir(Direction dir) {
		return new IFileupload.Builder().setDir(dir.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IFileupload ofId(String id) {
		return new IFileupload.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IFileupload IFileupload}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIFileupload.Builder {
	}

	/**
	 * Builds an updater of type {@link IFileupload} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IFileuploadUpdater {}
}