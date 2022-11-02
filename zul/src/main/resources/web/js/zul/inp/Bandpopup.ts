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
	override parent?: zul.inp.Bandbox;
	override nextSibling?: zul.inp.Bandpopup;
	override previousSibling?: zul.inp.Bandpopup;
	/** @internal */
	_shallClosePopup?: boolean;

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		jq(this.$n())
			.on('focusin', this._focusin.bind(this))
			.on('focusout', this._focusout.bind(this));
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		jq(this.$n())
			.off('focusout', this._focusout.bind(this))
			.off('focusin', this._focusin.bind(this));
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_focusin(e: JQuery.FocusInEvent): void {
		this._shallClosePopup = false;
	}

	/** @internal */
	_focusout(e: JQuery.FocusOutEvent): void {
		const bandbox = this.parent;
		if (e.relatedTarget) {
			if (bandbox?.isOpen() && !jq.isAncestor(bandbox.$n('pp'), e.relatedTarget as HTMLElement))
				bandbox.close();
		} else if ((e.originalEvent?.target as {disabled?: boolean} | null | undefined)?.disabled) { // eslint-disable-line zk/noNull
			if (bandbox) {
				// ZK-5155: A focusout/blur event can be fired when an element is disabled. If a child of this Bandpopup
				// loses its focus due to being disabled, let the popup receive focus.
				const popup = bandbox.$n_('pp');
				popup.focus(); // The popup can receive focus because it has tabindex set.
				e.relatedTarget = popup;
			}
		} else {
			// for solving B96-ZK-4748, treechildren will rerender itself when clicking
			// the open icon, and JQ will simulate a fake focusout event without any relatedTarget.

			this._shallClosePopup = true;
			setTimeout(() => {
				if (bandbox?.isOpen() && this._shallClosePopup && !jq.isAncestor(bandbox.$n('pp'), document.activeElement)) {
					const blurredElement = e.originalEvent?.target;
					if (blurredElement && !document.body.contains(blurredElement as Node)) {
						// ZK-5155: The focusout/blur event is due to unmounting the target from DOM. In this case, let the
						// bandpopup take focus, and don't close the bandpopup.
						const popup = bandbox.$n_('pp');
						popup.focus(); // The popup can receive focus because it has tabindex set.
						e.relatedTarget = popup;
					} else {
						bandbox.close();
					}
					this._shallClosePopup = false;
				}
			});
		}
	}

	//super
	/** @internal */
	override afterChildrenMinFlex_(orient: zk.FlexOrient): void {
		if (orient == 'w') {
			const pp = this.parent?.$n('pp');
			if (pp) {
				// test case is B50-ZK-859.zul
				pp.style.width = jq.px0(this._hflexsz! + zk(pp).padBorderWidth());
				zk(pp)._updateProp(['width']);
			}
		}
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (evt.domTarget == this.$n())
			this.parent!.focus();
		super.doClick_(evt, popupOnly);
	}
}