/* Caption.ts

	Purpose:

	Description:

	History:
		Sun Nov 16 13:01:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 *  A header for a {@link Groupbox}.
 * It may contain either a text label, using {@link #setLabel},
 * or child elements for a more complex caption.
 * <p>Default {@link #getZclass}: z-caption.
 *
 */
@zk.WrapClass('zul.wgt.Caption')
export class Caption extends zul.LabelImageWidget<HTMLDivElement> {
	// NOTE: parent could be null as asserted in domCon
	// In essence, parent is `zul.wnd.Window | zul.wnd.Panel | zul.wgt.Groupbox`.
	override parent!: zk.Widget & Partial<zul.wnd.Panel> | undefined;
	//super//
	domDependent_ = true;

	override rerender(skipper?: zk.Skipper | number): this {
		var p = this.parent;
		if (p)
			p.clearCache(); // B50-ZK-244
		return super.rerender(skipper);
	}

	override domContent_(): string {
		var label = this.getLabel(),
			img = this.getImage(),
			title = this.parent ? this.parent._title : '',
			iconSclass = this.domIcon_();
		if (title) label = label ? title + ' - ' + label : title;
		label = zUtl.encodeXML(label);
		if (!img && !iconSclass) return label;

		if (!img) img = iconSclass;
		else img = '<img id="' + this.uuid + '-img" src="' + img + '" class="' + this.$s('image') + '" alt="" aria-hidden="true" />' + (iconSclass ? ' ' + iconSclass : '');
		return label ? img + ' ' + '<span class="' + this.$s('label') + '">' + label + '</span>' : img;
	}

	override updateDomContent_(): void {
		var cnt = this.domContent_(),
			dn = this.$n('cave');
		if (dn) {
			var firstWgtDom;
			if (this.firstChild) {
				firstWgtDom = this.firstChild.$n();
			}
			// eslint-disable-next-line zk/noNull
			for (var child = dn.firstChild, nextChild: ChildNode | null; child && child !== firstWgtDom; child = nextChild) {
				nextChild = child.nextSibling;
				jq(child).remove();
			}
			this.clearCache(); //B70-ZK-2370: clearCache after remove dom content
			jq(dn).prepend(cnt ? cnt : '&nbsp;');
		}
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var sc = super.domClass_(no),
			parent = this.parent;

		if (!(parent instanceof zul.wgt.Groupbox))
			return sc;

		return sc + (parent._closable ? '' : ' ' + this.$s('readonly'));
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (this.parent instanceof zul.wgt.Groupbox && this.parent.isClosable())
			this.parent.setOpen(!this.parent.isOpen());
		super.doClick_(evt, popupOnly);
	}

	//private//
	_getBlank(): string {
		return '&nbsp;';
	}

	/** Whether to generate a collapsible button (determined by parent only). */
	_isCollapsibleVisible(): boolean {
		var parent = this.parent!;
		return !!(parent.isCollapsible && parent.getCollapseOpenIconClass_ && parent.isCollapsible());
	}

	/** Whether to generate a close button (determined by parent only). */
	_isCloseVisible(): boolean {
		var parent = this.parent!;
		return !!(parent.isClosable && parent.getClosableIconClass_ && parent.isClosable());
	}

	/** Whether to generate a minimize button (determined by parent only). */
	_isMinimizeVisible(): boolean {
		var parent = this.parent!;
		return !!(parent.isMinimizable && parent.getMinimizableIconClass_ && parent.isMinimizable());
	}

	/** Whether to generate a maximize button (determined by parent only). */
	_isMaximizeVisible(): boolean {
		var parent = this.parent!;
		return !!(parent.isMaximizable && parent.getMaximizableIconClass_ && parent.isMaximizable());
	}

	override beforeMinFlex_(o: zk.FlexOrient): number | undefined { // Fixed for B50-3343388.zul
		// FIXME: Div has no property width. Setting it in the console has no effect.
		// Dead code?
		if (o == 'w')
			(this.$n_() as HTMLElement & { width?: string }).width = '';
		return undefined;
	}

	// override for the bug ZK-1799
	// eslint-disable-next-line zk/javaStyleSetterSignature
	override setFlexSizeW_(n: HTMLElement, zkn: zk.JQZK, width: number, isFlexMin?: boolean): void {
		if (isFlexMin) {
			if (this._isCloseVisible()) {
				var close = this.parent!.$n_('close');
				width += close.offsetWidth + zk(close).marginWidth();
			}
			if (this._isMaximizeVisible()) {
				var max = this.parent!.$n_('max');
				width += max.offsetWidth + zk(max).marginWidth();
			}
			if (this._isMinimizeVisible()) {
				var min = this.parent!.$n_('min');
				width += min.offsetWidth + zk(min).marginWidth();
			}
			if (this._isCollapsibleVisible()) {
				var exp = this.parent!.$n_('exp');
				width += exp.offsetWidth + zk(exp).marginWidth();
			}
		}
		super.setFlexSizeW_(n, zkn, width, isFlexMin);
	}

	// override
	// ZK-786
	override getImageNode(): HTMLImageElement | undefined {
		if (!this._eimg && this._image) {
			var n = this.$n<HTMLImageElement>('img');
			if (n) this._eimg = n;
		}
		return this._eimg;
	}
}
