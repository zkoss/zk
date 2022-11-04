/* Hlayout.ts

	Purpose:

	Description:

	History:
		Fri Aug  6 11:54:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A horizontal layout.
 * @defaultValue {@link getZclass}: z-hlayout.
 * @since 5.0.4
 */
@zk.WrapClass('zul.box.Hlayout')
export class Hlayout extends zul.box.Layout {
	/** @internal */
	_valign = 'top';
	/** @internal */
	_beforeSizeWidth?: number;

	/**
	 * Sets the vertical-align to top or bottom.
	 *
	 * @param valign - the value of vertical-align property
	 * "top", "middle", "bottom".
	 * @since 6.0.0
	 */
	setValign(valign: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = valign;

		if (o !== valign || opts?.force) {
			this.updateDomClass_();
		}

		return this;
	}

	getValign(): string {
		return this._valign;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({_beforeSizeForRead: this, beforeSize: this, onFitSize: this}); //ZK-4476
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({_beforeSizeForRead: this, beforeSize: this, onFitSize: this});
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override isVertical_(): boolean {
		return false;
	}

	// F60-ZK-537: Hlayout supports valign (top, middle and bottom),
	// set vertical-align to children cause wrong layout on IE6,
	// set it to parent directly
	/** @internal */
	override domClass_(no?: Partial<zk.DomClassOptions>): string {
		var clsnm = super.domClass_(no),
			v;
		if ((v = this._valign) == 'middle')
			clsnm += ' z-valign-middle';
		else if (v == 'bottom')
			clsnm += ' z-valign-bottom';
		return clsnm;
	}

	/** @internal */
	override getFlexDirection_(): string {
		return 'row';
	}

	//ZK-4476
	/** @internal */
	_beforeSizeForRead(): void {
		var n = this.$n();
		this._beforeSizeWidth = n ? n.offsetWidth : 0;
		for (var xc: undefined | zk.Widget & Partial<{_beforeSizeWidth: number}> = this.firstChild; xc; xc = xc.nextSibling) {
			n = xc.$n();
			xc._beforeSizeWidth = n ? n.offsetWidth : 0;
		}
	}

	beforeSize(): void {
		var xc: undefined | zk.Widget & Partial<{_beforeSizeWidth: number}> = this.firstChild,
			totalWdCached = this._beforeSizeWidth,
			totalWd = totalWdCached != null ? totalWdCached : this.$n()!.offsetWidth,
			flexCnt = 0,
			flexWgts: {wgt: zk.Widget; flex: number}[] = [];
		for (; xc; xc = xc.nextSibling) {
			if (xc.isVisible() && !zk(xc).hasVParent()) {
				var nhflex = xc._nhflex,
					nXc = xc.$n();
				if (nhflex) {
					flexWgts.push({wgt: xc, flex: nhflex});
					flexCnt += nhflex;
				} else if (nXc) {
					var xcOffsetWidthCached = xc._beforeSizeWidth,
						xcOffsetWidth = xcOffsetWidthCached != null ? xcOffsetWidthCached : nXc.offsetWidth;
					totalWd -= xcOffsetWidth;
					xc.$n('chdex')!.style.width = jq.px0(xcOffsetWidth);
				}
			}
			delete xc._beforeSizeWidth;
		}
		if (flexCnt > 0) {
			var perWd = totalWd / flexCnt;
			for (var i = 0, l = flexWgts.length; i < l; i++)
				flexWgts[i].wgt.$n_('chdex').style.width = jq.px0(perWd * flexWgts[i].flex);
		}
		delete this._beforeSizeWidth;
	}

	onFitSize(): void {
		var xc = this.firstChild;
		for (; xc; xc = xc.nextSibling) {
			if (xc.isVisible() && !zk(xc).hasVParent())
				xc.$n('chdex')!.style.width = '';
		}
	}
}