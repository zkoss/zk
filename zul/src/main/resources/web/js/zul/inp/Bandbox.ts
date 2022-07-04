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
 * <p>Default {@link #getZclass}: z-bandbox.
 */
@zk.WrapClass('zul.inp.Bandbox')
export class Bandbox<ValueType> extends zul.inp.ComboWidget<ValueType> {
	protected override _iconSclass = 'z-icon-search';

	//super
	protected override getPopupSize_(pp: HTMLElement): zul.inp.PopupSize {
		var bp = this.firstChild, //bandpopup
			w, h;
		if (bp) {
			w = bp._hflex == 'min' && bp._hflexsz ? jq.px0(bp._hflexsz + zk(pp).padBorderWidth()) : bp.getWidth();
			h = bp._vflex == 'min' && bp._vflexsz ? jq.px0(bp._vflexsz + zk(pp).padBorderHeight()) : bp.getHeight();
		}
		return [w || 'auto', h || 'auto'];
	}

	public override getCaveNode(): HTMLElement | null | undefined {
		return this.$n('pp') || this.$n();
	}

	protected override redrawpp_(out: string[]): void {
		var fc = this.firstChild;
		out.push('<div id="', this.uuid, '-pp" class="', this.$s('popup'),
		// tabindex=0 to prevent a11y scrollable popup issue, see https://dequeuniversity.com/rules/axe/3.5/scrollable-region-focusable?application=AxeChrome
		'" style="display:none" role="dialog" aria-labelledby="' + (fc ? fc.uuid : '') + '" tabindex="0">');

		for (var w = fc; w; w = w.nextSibling)
			w.redraw(out);

		out.push('</div>');
	}

	public override open(opts?: zul.inp.OpenOptions): void {
		if (!this.firstChild) {
			// ignore when <bandpopup> is absent, but event is still fired
			if (opts && opts.sendOnOpen)
				this.fire('onOpen', {open: true, value: this.getInputNode()!.value}, {rtags: {onOpen: 1}});
			return;
		}
		super.open(opts);
	}

	protected override presize_(): boolean {
		var bp = this.firstChild;
		if (bp && (bp._hflex == 'min' || bp._vflex == 'min')) {
			zWatch.fireDown('onFitSize', bp, {reverse: true});
			return true;
		}
		return false;
	}

	protected override enterPressed_(evt: zk.Event): void {
		//bug 3280506: do not close when children press enter.
		if (evt.domTarget == this.getInputNode())
			super.enterPressed_(evt);
	}

	protected override doKeyUp_(evt: zk.Event): void {
		//bug 3287082: do not fire onChanging when children typing.
		if (evt.domTarget == this.getInputNode())
			super.doKeyUp_(evt);
	}

	protected override _fixsz(ppofs: zul.inp.PopupSize): void {
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

	protected override doFocus_(evt: zk.Event): void {
		var target = evt.domTarget;
		if (!(target != this.getInputNode() && target != this.$n('btn'))) super.doFocus_(evt);
	}
}