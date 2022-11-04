/* Progressmeter.ts

	Purpose:

	Description:

	History:
		Thu May 14 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A progress meter is a bar that indicates how much of a task has been completed.
 *
 * @defaultValue {@link getZclass}: z-progressmeter.
 */
@zk.WrapClass('zul.wgt.Progressmeter')
export class Progressmeter extends zul.Widget {
	/** @internal */
	_value = 0;
	/** @internal */
	_indeterminate = false;
	/** @internal */
	_indeterminateAnimation = false;

	/**
	 * @returns the current value of the progress meter.
	 */
	getValue(): number {
		return this._value;
	}

	/**
	 * Sets the current value of the progress meter.
	 * <p>Range: 0~100.
	 */
	setValue(value: number, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = value;

		if (o !== value || opts?.force) {
			if (this.$n())
				this._fixImgWidth();
		}

		return this;
	}

	/**
	 * @returns the indeterminate state of the progress meter.(default false)
	 */
	isIndeterminate(): boolean {
		return this._indeterminate;
	}

	/**
	 * Sets the indeterminate state of the progress meter.
	 */
	setIndeterminate(indeterminate: boolean, opts?: Record<string, boolean>): this {
		const o = this._indeterminate;
		this._indeterminate = indeterminate;

		if (o !== indeterminate || opts?.force) {
			if (this.$n()) {
				jq(this.$n()).toggleClass(this.$s('indeterminate'), indeterminate);
			}
		}

		return this;
	}

	/** @internal */
	_fixImgWidth(): void {
		var n = this.$n(),
			img = this.$n('img');
		if (img) {
			//B70-ZK-2453 remember to add brackets
			if (zk(n).isRealVisible() && !this._indeterminateAnimation) { //Bug 3134159
				var $img = jq(img);
				$img.animate({
					width: this._value + '%'
				}, { duration: $img.zk.getAnimationSpeed('slow'), queue: false, easing: 'linear' }); //ZK-4079: progressmeter animation not catching up with actual value
			}
		}
	}

	override onSize(): void {
		this._fixImgWidth();
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {//after compose
		super.bind_(desktop, skipper, after);
		this._fixImgWidth();
		zWatch.listen({onSize: this});
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onSize: this});
		super.unbind_(skipper, after, keepRod);
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		this._fixImgWidth();
		return this;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			if (this._indeterminate)
				scls += ' ' + this.$s('indeterminate');
		}
		return scls;
	}
}