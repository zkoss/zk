/* Bandpopup.ts

	Purpose:

	Description:

	History:
		Fri Apr  3 15:24:37     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The popup that belongs to a {@link Bandbox} instance.
 *
 * <p>Developer usually listen to the onOpen event that is sent to
 * {@link Bandbox} and then creates proper components as children
 * of this component.
 *
 * <p>z-class: z-bandpopup
 */
@zk.WrapClass('zul.inp.Bandpopup')
export class Bandpopup extends zul.Widget {
	override parent!: zul.inp.Bandbox | null;
	override nextSibling!: zul.inp.Bandpopup | null;
	override previousSibling!: zul.inp.Bandpopup | null;
	_shallClosePopup?: boolean;

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		jq(this.$n()!).on('focusin', this.proxy(this._focusin))
			.on('focusout', this.proxy(this._focusout));
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		jq(this.$n()!).off('focusout', this.proxy(this._focusout))
			.off('focusin', this.proxy(this._focusin));
		super.unbind_(skipper, after, keepRod);
	}

	_focusin(e: JQuery.FocusInEvent): void {
		this._shallClosePopup = false;
	}

	_focusout(e: JQuery.FocusOutEvent): void {
		var bandbox = this.parent,
			self = this;
		if (e.relatedTarget) {
			if (bandbox && bandbox.isOpen() && !jq.isAncestor(bandbox.$n('pp'), e.relatedTarget as HTMLElement))
				bandbox.close();
		} else {
			// for solving B96-ZK-4748, treechildren will rerender itself when clicking
			// the open icon, and JQ will simulate a fake focusout event without any relatedTarget.

			self._shallClosePopup = true;
			setTimeout(function () {
				if (bandbox && bandbox.isOpen() && self._shallClosePopup) {
					bandbox.close();
					self._shallClosePopup = false;
				}
			});
		}
	}

	//super
	override afterChildrenMinFlex_(orient: zk.FlexOrient): void {
		if (orient == 'w') {
			var bandbox = this.parent,
				pp = bandbox && bandbox.$n('pp');
			if (pp) {
				// test case is B50-ZK-859.zul
				pp.style.width = jq.px0(this._hflexsz! + zk(pp).padBorderWidth());
				zk(pp)._updateProp(['width']);
			}
		}
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (evt.domTarget == this.$n())
			this.parent!.focus();
		super.doClick_(evt, popupOnly);
	}
}