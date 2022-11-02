/* LabelImageWidget.ts

	Purpose:

	Description:

	History:
		Sun Nov 16 14:59:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

// `zul.wgt.ADBS.prototype.autodisable` requires the properties _adbs, _disabled,
// and _autodisable to be public.
export interface LabelImageWidgetWithDisable extends LabelImageWidget {
	_adbs?: boolean;
	_disabled?: boolean;
	isDisabled(): boolean;
	setDisabled(disabled?: boolean, opts?: Record<string, boolean>): this;
}

export interface LabelImageWidgetWithAutodisable extends LabelImageWidgetWithDisable {
	_autodisable?: string;
	getAutodisable(): string | undefined;
	setAutodisable(autodisable: string): this;
}

/**
 * A skeletal implementation for ZUL widgets that support both label and image.
 */
@zk.WrapClass('zul.LabelImageWidget')
export abstract class LabelImageWidget<TElement extends HTMLElement = HTMLElement> extends zul.Widget<TElement> {
	/** @internal */
	_label = '';
	/** @internal */
	_iconSclass?: string;
	/** @internal */
	_image?: string;
	/** @internal */
	_hoverImage?: string;
	/** @internal */
	_eimg?: HTMLImageElement;
	/** @internal */
	_preloadImage?: boolean;

	/**
	 * Sets the label.
	 * <p>If label is changed, the whole component is invalidate.
	 * Thus, you want to smart-update, you have to override {@link updateDomContent_}.
	 */
	setLabel(label: string, opts?: Record<string, boolean>): this {
		const o = this._label;
		this._label = label;

		if (o !== label || opts?.force) {
			if (this.desktop)
				this.updateDomContent_();
		}

		return this;
	}

	/**
	 * @returns the label (never null).
	 * @defaultValue `""`.
	 */
	getLabel(): string {
		return this._label;
	}

	/**
	 * Sets the icon font
	 * @param iconSclass - a CSS class name for the icon font
	 * @since 7.0.0
	 */
	setIconSclass(iconSclass: string, opts?: Record<string, boolean>): this {
		const o = this._iconSclass;
		this._iconSclass = iconSclass;

		if (o !== iconSclass || opts?.force) {
			if (this.desktop)
				this.updateDomContent_();
		}

		return this;
	}

	/**
	 * @returns the CSS class name for the icon font (`iconSclass`)
	 * @since 7.0.0
	 */
	getIconSclass(): string | undefined {
		return this._iconSclass;
	}

	/**
	 * Sets the image URI. The image would hide if `src == null`
	 * @param image - the URI of the image
	 */
	setImage(image: string, opts?: Record<string, boolean>): this {
		const o = this._image;
		this._image = image;

		if (o !== image || opts?.force) {
			if (image && this._preloadImage) zUtl.loadImage(image);
			var n = this.getImageNode(),
				jqn = jq(n);
			if (n) {
				var img = image || '';
				if (jq.nodeName(n, 'img')) // ZK-1100
					n.src = img;
				else
					jqn.css('background-image', 'url(' + img + ')');
				jqn[!img ? 'hide' : 'show']();
			} else if (this.desktop) //<IMG> might not be generated (Bug 3007738)
				this.updateDomContent_();
		}

		return this;
	}

	/**
	 * @returns the image URI.
	 * @defaultValue `null`.
	 */
	getImage(): string | undefined {
		return this._image;
	}

	/**
	 * Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 */
	setHoverImage(hoverImage: string): this {
		this._hoverImage = hoverImage;
		return this;
	}

	/**
	 * @returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * @defaultValue `null`.
	 */
	getHoverImage(): string | undefined {
		return this._hoverImage;
	}

	/**
	 * Updates the DOM tree for the modified label and image. It is called by
	 * {@link setLabel} and {@link setImage} to update the new content of the
	 * label and/or image to the DOM tree.
	 * @defaultValue invoke {@link zk.Widget#rerender} to redraw and re-bind.
	 * @internal
	 */
	updateDomContent_(): void {
		this.rerender();
	}

	/**
	 * @returns the HTML image content.
	 * @internal
	 */
	domImage_(): string {
		var img = this._image;
		return img ? '<img src="' + img + '" align="absmiddle" alt="" aria-hidden="true">' : '';
	}

	/**
	 * @returns the icon font class name with HTML content.
	 * @since 7.0.0
	 * @internal
	 */
	domIcon_(): string {
		var icon = this.getIconSclass(), // use getIconSclass() to allow overriding
			result = '';
		//ZK-3636: Added simple support for stacked font awesome icons
		if (icon) {
			var icons = icon.split(','),
				length = icons.length;
			if (length > 1) {
				var arr = ['<span class="z-icon-stack" aria-hidden="true">'];
				for (var i = 0; i < length; i++) {
					var ic = icons[i];
					if (ic)
						arr.push('<i class="' + ic + '"></i>');
				}
				arr.push('</span>');
				result = arr.join('');
			} else {
				result = '<i class="' + icon + '" aria-hidden="true"></i>';
			}
		}
		return result;
	}

	/**
	 * @returns the encoded label.
	 * @see {@link zUtl.encodeXML}
	 * @internal
	 */
	domLabel_(): string {
		return zUtl.encodeXML(this.getLabel());
	}

	/**
	 * @returns the HTML content of the label and image.
	 * It is a fragment of HTML that you can use in the mold.
	 * @see {@link domImage_}
	 * @see {@link domLabel_}
	 * @internal
	 */
	domContent_(): string {
		var label = this.domLabel_(),
			icon = this.domIcon_(),
			img = this.domImage_();

		if (img) {
			if (icon) {
				return img + ' ' + icon + (label ? ' ' + label : '');
			} else {
				return label ? img + ' ' + label : img;
			}
		} else {
			return icon ? label ? icon + ' ' + label : icon : label;
		}
	}

	/** @internal */
	override doMouseOver_(evt: zk.Event): void {
		this._updateHoverImage(true);
		super.doMouseOver_(evt);
	}

	/** @internal */
	override doMouseOut_(evt: zk.Event): void {
		this._updateHoverImage();
		super.doMouseOut_(evt);
	}

	/**
	 * @returns the image node if any.
	 */
	getImageNode(): HTMLImageElement | undefined {
		if (!this._eimg && this._image) {
			var n = this.$n();
			if (n) this._eimg = jq(n).find<HTMLImageElement>('img:first')[0];
		}
		return this._eimg;
	}

	/** @internal */
	_updateHoverImage(inHover?: boolean): void {
		var n = this.getImageNode(),
			img = inHover ? this._hoverImage : this._image;
		if (n && this._hoverImage) {
			if (jq.nodeName(n, 'img'))
				n.src = img!;
			else
				jq(n).css('background-image', 'url(' + img + ')');
		}
	}

	override clearCache(): void {
		this._eimg = undefined;
		super.clearCache();
	}
}