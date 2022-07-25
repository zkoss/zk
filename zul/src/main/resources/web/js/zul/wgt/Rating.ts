/* Rating.ts

	Purpose:

	Description:

	History:
		Thu Jul 12 10:24:21 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/

/**
 * An icon based rating component.
 *
 * @author wenninghsu
 * @since 8.6.0
 */
@zk.WrapClass('zul.wgt.Rating')
export class Rating extends zul.Widget {
	_orient = 'horizontal';
	_rating = 0;
	_cancelable = true;
	_max = 5;
	_disabled = false;
	_iconSclass = 'z-icon-star';
	_readonly?: boolean;

	/**
	 * Sets the iconSclass.
	 * @param String sclass
	 */
	setIconSclass(sclass: string, opts?: Record<string, boolean>): this {
		const o = this._iconSclass;
		this._iconSclass = sclass;

		if (o !== sclass || (opts && opts.force)) {
			if (this.desktop) {
				this.rerender();
			}
		}

		return this;
	}

	/**
	 * Returns the iconSclass.
	 * @return String
	 */
	getIconSclass(): string {
		return this._iconSclass;
	}

	/**
	 * Sets the rating.
	 * @param int rating
	 */
	setRating(rating: number, opts?: Record<string, boolean>): this {
		const o = this._rating;
		this._rating = rating;

		if (o !== rating || (opts && opts.force)) {
			if (this.desktop) {
				this._toggleClass('selected', rating);
			}
		}

		return this;
	}

	/**
	 * Returns the rating.
	 * @return int
	 */
	getRating(): number {
		return this._rating;
	}

	/**
	 * Sets whether this widget is disabled
	 * @param boolean disabled
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;

		if (o !== disabled || (opts && opts.force)) {
			if (this.desktop) {
				jq(this).children().toggleClass(this.$s('disabled'), disabled);
			}
		}

		return this;
	}

	/**
	 * Returns whether this widget is disabled
	 * @return boolean
	 */
	isDisabled(): boolean {
		return this._disabled;
	}

	/**
	 * Sets whether this widget is readonly
	 * @param boolean readonly
	 */
	setReadonly(readonly: boolean, opts?: Record<string, boolean>): this {
		const o = this._readonly;
		this._readonly = readonly;

		if (o !== readonly || (opts && opts.force)) {
			if (this.desktop) {
				jq(this).children().toggleClass(this.$s('readonly'), readonly);
			}
		}

		return this;
	}

	/**
	 * Returns whether this widget is readonly
	 * @return boolean
	 */
	isReadonly(): boolean {
		return !!this._readonly;
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var wgt = this,
			isVertical = 'vertical' == wgt._orient;
		jq(wgt).children().each(function (i) {
			jq(this).data('rate', isVertical ? wgt._max - i : i + 1);
			wgt.domListen_(this, 'onMouseOver', '_doMouseOver').domListen_(this, 'onMouseOut', '_doMouseOut');
		});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var wgt = this;
		jq(wgt).children().each(function () {
			wgt.domUnlisten_(this, 'onMouseOver', '_doMouseOver').domUnlisten_(this, 'onMouseOut', '_doMouseOut');
		});
		super.unbind_(skipper, after, keepRod);
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._orient);
		}
		return sc;
	}

	override doSelect_(evt: zk.Event): void {
		if (this._disabled || this._readonly)
			return;
		this._changeRating(evt);
	}

	_changeRating(evt: zk.Event): void {
		var rating = jq(evt.domTarget).data('rate') as number,
			isCanceling = this._cancelable && this._rating == rating;
		jq(this).children().removeClass(this.$s('hover'));
		this.setRating(isCanceling ? 0 : rating);
		this.fire('onChange', {rating: this._rating});
	}

	_doMouseOver(evt: zk.Event): void {
		if (this._disabled || this._readonly)
			return;
		this._toggleClass('hover', jq(evt.domTarget).data('rate') as number);
	}

	_doMouseOut(evt: zk.Event): void {
		if (this._disabled || this._readonly)
			return;
		jq(this).children().removeClass(this.$s('hover'));
	}

	_toggleClass(name: string, rate: number): void {
		var wgt = this;
		jq(wgt).children().each(function () {
			var jqCh = jq(this);
			jqCh.toggleClass(wgt.$s(name), jqCh.data('rate') <= rate);
		});
	}
}