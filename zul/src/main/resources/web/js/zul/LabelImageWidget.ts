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
	/** @internal */
	_adbs?: boolean;
	/** @internal */
	_disabled?: boolean;
	isDisabled(): boolean;
	setDisabled(disabled?: boolean, opts?: Record<string, boolean>): this;
}

export interface LabelImageWidgetWithAutodisable extends LabelImageWidgetWithDisable {
	/** @internal */
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
	_iconSclasses?: string[];
	/** @internal */
	_iconTooltip?: string;
	/** @internal */
	_iconTooltips?: string[];
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
	 * Thus, you want to smart-update, you have to override {@link updateDomContent_}.
	 */
	setLabel(label: string, opts?: Record<string, boolean>): this {
		const o = this._label;
		this._label = label;

		if (o !== label || opts?.force) {
			if (this.desktop) {
				const n = this.$n();

				// avoid to invalidate the whole widget if only changed by label.
				if (n && n.firstChild == n.lastChild && n.firstChild instanceof Text) {
					jq(n).text(label);
					return this;
				}
				this.updateDomContent_();
			}
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
	 * Sets the icon font, if iconSclasses is set, iconSclass will be ignored, iconSclasses will take precedence over iconSclass
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
	 * Sets multiple icons font, if iconSclasses is set, iconSclass will be ignored, iconSclasses will take precedence over iconSclass
	 * @param iconSclasses - a CSS class name String array for the icon font
	 * @since 10.0.0
	 */
	setIconSclasses(iconSclasses: string[], opts?: Record<string, boolean>): this {
		const o = this._iconSclasses;
		this._iconSclasses = iconSclasses;

		if (o !== iconSclasses || opts?.force) {
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
	 * @returns the CSS class name string array for the icon font (`iconSclass`)
	 * @since 10.0.0
	 */
	getIconSclasses(): string[] | undefined {
		return this._iconSclasses;
	}

	/**
	 * * Sets the iconTooltip, if iconTooltips is set, iconTooltip will be ignored, iconTooltips will take precedence over iconTooltip
	 * @param iconTooltip - a content string for iconTooltip
	 * @since 10.0.0
	 */
	setIconTooltip(iconTooltip: string, opts?: Record<string, boolean>): this {
		const o = this._iconTooltip;
		this._iconTooltip = iconTooltip;

		if (o !== iconTooltip || opts?.force) {
			if (this.desktop)
				this.updateDomContent_();
		}
		return this;
	}

	/**
	 * Sets multiple iconTooltips, if iconTooltips is set, iconTooltip will be ignored, iconTooltips will take precedence over iconTooltip
	 * @param iconTooltips - a content string array for iconTooltip
	 * @since 10.0.0
	 */
	setIconTooltips(iconTooltips: string[], opts?: Record<string, boolean>): this {
		const o = this._iconTooltips;
		this._iconTooltips = iconTooltips;

		if (o !== iconTooltips || opts?.force) {
			if (this.desktop)
				this.updateDomContent_();
		}
		return this;
	}

	/**
	 * @returns the iconTooltip content
	 * @since 10.0.0
	 */
	getIconTooltip(): string | undefined {
		return this._iconTooltip;
	}

	/**
	 * @returns the iconTooltip content string array
	 * @since 10.0.0
	 */
	getIconTooltips(): string[] | undefined {
		return this._iconTooltips;
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

	// for stateless and client mvvm to treat as "image" attribute at client side
	getImageContent(): string | undefined {
		return this.getImage();
	}
	// for stateless and client mvvm to treat as "image" attribute at client side
	setImageContent(imageContent: string, opts?: Record<string, boolean>): this {
		return this.setImage(imageContent, opts);
	}

	// for stateless and client mvvm to treat as "hoverImage" attribute at client side
	getHoverImageContent(): string | undefined {
		return this.getHoverImage();
	}
	// for stateless and client mvvm to treat as "hoverImage" attribute at client side
	setHoverImageContent(hoverImageContent: string): this {
		return this.setHoverImage(hoverImageContent);
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
		return img ? '<img src="' + zUtl.encodeXMLAttribute(img) + '" align="absmiddle" alt="" aria-hidden="true">' : '';
	}

	/**
	 * @returns the icon font class name with HTML content.
	 * @since 7.0.0
	 * @internal
	 */
	domIcon_(): string {
		const iconSclass = this.getIconSclass(),
			iconSclasses = this.getIconSclasses(),
			iconTooltip = this.getIconTooltip(),
			iconTooltips = this.getIconTooltips(),
			icon = iconSclasses ? iconSclasses : iconSclass ? [iconSclass] : undefined,
			tooltip = iconTooltips ? iconTooltips : iconTooltip ? [iconTooltip] : undefined;
		if (icon) {
			if (icon.length > 1) {
				let html = '<span class="z-icon-stack" aria-hidden="true">';
				for (let i = 0, icon_length = icon.length; i < icon_length; i++) {
					if (icon[i]) { // add icon if icon[i] not undefined
						html += '<i class="' + /*safe*/ zUtl.encodeXMLAttribute(icon[i]) + '"';
						if (tooltip?.[i]) { // add iconTooltip if iconTooltip[i] not undefined
							html += ' title="' + /*safe*/ zUtl.encodeXMLAttribute(tooltip[i]) + '"';
						}
						html += '></i>';
					}
				}
				html += '</span>';
				return html;
			} else {
				return '<i class="' + zUtl.encodeXMLAttribute(icon[0]) + '"' + (tooltip?.[0] ? ' title="' + zUtl.encodeXMLAttribute(tooltip[0]) + '"' : '') + ' aria-hidden="true"></i>';
			}
		}
		return '';
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
		const labelHTML = this.domLabel_(),
			iconHTML = this.domIcon_(),
			imgHTML = this.domImage_();

		if (imgHTML) {
			if (iconHTML) {
				return imgHTML + ' ' + iconHTML + (labelHTML ? ' ' + labelHTML : '');
			} else {
				return labelHTML ? imgHTML + ' ' + labelHTML : imgHTML;
			}
		} else {
			return iconHTML ? labelHTML ? iconHTML + ' ' + labelHTML : iconHTML : labelHTML;
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