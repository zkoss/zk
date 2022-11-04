/* Bandbox.ts

	Purpose:

	Description:

	History:
		Tue Mar 31 14:17:28     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A band box. A bank box consists of an input box ({@link Textbox} and
 * a popup window {@link Bandpopup}.
 * It is similar to {@link Combobox} except the popup window could have
 * any kind of children. For example, you could place a textbox in
 * the popup to let user search particular items.
 *
 * @defaultValue {@link getZclass}: z-bandbox.
 */
@zk.WrapClass('zul.inp.Bandbox')
export class Bandbox extends zul.inp.ComboWidget {
	override firstChild!: zul.inp.Bandpopup | undefined;
	override lastChild!: zul.inp.Bandpopup | undefined;
	/** @internal */
	override _iconSclass = 'z-icon-search';

	//super
	/** @internal */
	override getPopupSize_(pp: HTMLElement): zul.inp.PopupSize {
		var bp = this.firstChild, //bandpopup
			w, h;
		if (bp) {
			w = bp._hflex == 'min' && bp._hflexsz ? jq.px0(bp._hflexsz + zk(pp).padBorderWidth()) : bp.getWidth();
			h = bp._vflex == 'min' && bp._vflexsz ? jq.px0(bp._vflexsz + zk(pp).padBorderHeight()) : bp.getHeight();
		}
		return [w || 'auto', h || 'auto'];
	}

	override getCaveNode(): HTMLElement | undefined {
		return this.$n('pp') ?? this.$n();
	}

	/** @internal */
	override redrawpp_(out: string[]): void {
		var fc = this.firstChild;
		out.push('<div id="', this.uuid, '-pp" class="', this.$s('popup'),
		// tabindex=0 to prevent a11y scrollable popup issue, see https://dequeuniversity.com/rules/axe/3.5/scrollable-region-focusable?application=AxeChrome
		'" style="display:none" role="dialog" aria-labelledby="' + (fc ? fc.uuid : '') + '" tabindex="0">');

		for (var w = fc; w; w = w.nextSibling)
			w.redraw(out);

		out.push('</div>');
	}

	override open(opts?: zul.inp.OpenOptions): void {
		if (!this.firstChild) {
			// ignore when <bandpopup> is absent, but event is still fired
			if (opts && opts.sendOnOpen)
				this.fire('onOpen', {open: true, value: this.getInputNode()!.value}, {rtags: {onOpen: 1}});
			return;
		}
		super.open(opts);
	}

	/** @internal */
	override presize_(): boolean {
		var bp = this.firstChild;
		if (bp && (bp._hflex == 'min' || bp._vflex == 'min')) {
			zWatch.fireDown('onFitSize', bp, {reverse: true});
			return true;
		}
		return false;
	}

	/** @internal */
	override enterPressed_(evt: zk.Event): void {
		//bug 3280506: do not close when children press enter.
		if (evt.domTarget == this.getInputNode())
			super.enterPressed_(evt);
	}

	/** @internal */
	override doKeyUp_(evt: zk.Event): void {
		//bug 3287082: do not fire onChanging when children typing.
		if (evt.domTarget == this.getInputNode())
			super.doKeyUp_(evt);
	}

	/** @internal */
	override _fixsz(ppofs: zul.inp.PopupSize): void {
		super._fixsz(ppofs);
		var pp = this.getPopupNode_()!,
			zkpp = zk(pp),
			ppfc = pp.firstChild as HTMLElement;
		if (ppofs[0].endsWith('%') || this.getPopupWidth()) {
			ppfc.style.width = '100%';
		} else if (ppofs[0] != 'auto') {
			pp.style.width = zkpp.revisedWidth(ppfc.offsetWidth + zkpp.padBorderWidth()) + 'px';
		}
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		var target = evt.domTarget;
		if (!(target != this.getInputNode() && target != this.$n('btn'))) super.doFocus_(evt);
	}

	/** @internal */
	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (!(child instanceof zul.inp.Bandpopup)) {
			zk.error('Unsupported child for Bandbox: ' + child.className);
			return false;
		}
		if (this.firstChild) {
			zk.error('At most one bandpopup is allowed, ' + this.className);
			return false;
		}
		return true;
	}
}